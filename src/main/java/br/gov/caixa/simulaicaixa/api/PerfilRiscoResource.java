package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.dto.PerfilRiscoDto;
import br.gov.caixa.simulaicaixa.service.PerfilRiscoService;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.Duration;

@Path("/perfil-risco")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PerfilRiscoResource {

    private final PerfilRiscoService perfilRiscoService;
    private final ColetorTelemetria coletorTelemetria;

    @Inject
    public PerfilRiscoResource(PerfilRiscoService perfilRiscoService,
                               ColetorTelemetria coletorTelemetria) {
        this.perfilRiscoService = perfilRiscoService;
        this.coletorTelemetria = coletorTelemetria;
    }

    @GET
    @Path("/{clienteId}")
    public PerfilRiscoDto obterPerfilRisco(@PathParam("clienteId") Long clienteId) {
        long inicio = System.nanoTime();

        PerfilRiscoDto perfil = perfilRiscoService.obterPorClienteId(clienteId);

        long fim = System.nanoTime();
        long duracaoMs = Duration.ofNanos(fim - inicio).toMillis();

        coletorTelemetria.registrarChamada("perfil-risco", duracaoMs);

        return perfil;
    }
}