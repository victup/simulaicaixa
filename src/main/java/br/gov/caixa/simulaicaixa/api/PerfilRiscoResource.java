package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.seguranca.ValidadorAcessoCliente;
import br.gov.caixa.simulaicaixa.dto.PerfilRiscoDto;
import br.gov.caixa.simulaicaixa.service.ContextoClienteService;
import br.gov.caixa.simulaicaixa.service.PerfilRiscoService;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.Duration;

@Path("/perfil-risco")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"cliente", "admin"})
public class PerfilRiscoResource {

    private final PerfilRiscoService perfilRiscoService;
    private final ColetorTelemetria coletorTelemetria;
    private final ContextoClienteService contextoClienteService;
    private final ValidadorAcessoCliente validadorAcessoCliente;

    @Inject
    public PerfilRiscoResource(PerfilRiscoService perfilRiscoService,
                               ColetorTelemetria coletorTelemetria,
                               ContextoClienteService contextoClienteService,
                               ValidadorAcessoCliente validadorAcessoCliente) {
        this.perfilRiscoService = perfilRiscoService;
        this.coletorTelemetria = coletorTelemetria;
        this.contextoClienteService = contextoClienteService;
        this.validadorAcessoCliente = validadorAcessoCliente;
    }

    @GET
    @Path("/{clienteId}")
    public PerfilRiscoDto obterPerfilRisco(@PathParam("clienteId") Long clienteId) {
        validadorAcessoCliente.validarClienteOuAdmin(
                clienteId,
                CodigoErroNegocio.OPERACAO_NAO_PERMITIDA_PARA_PERFIL
        );

        long inicio = System.nanoTime();

        PerfilRiscoDto perfil = perfilRiscoService.obterPorClienteId(clienteId);

        long fim = System.nanoTime();
        long duracaoMs = Duration.ofNanos(fim - inicio).toMillis();

        coletorTelemetria.registrarChamada("perfil-risco", duracaoMs);

        return perfil;
    }

    @GET
    @Path("/usuario-logado")
    public PerfilRiscoDto obterPerfilRiscoDoClienteAutenticado() {
        Long clienteId = contextoClienteService.obterClienteIdUsuarioObrigatorio();
        return obterPerfilRisco(clienteId);
    }
}