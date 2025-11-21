package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.erro.ErroResposta;
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

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Path("/perfil-risco")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"cliente", "admin"})
@Tag(
        name = "Perfil de Risco",
        description = "Consulta do perfil de risco de investimento dos clientes."
)
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
    @Operation(
            summary = "Consultar perfil de risco por cliente",
            description = """
                    Retorna o perfil de risco cadastrado para o cliente identificado pelo caminho {clienteId}.
                    
                    • Usuários com papel 'cliente' só podem consultar o próprio clienteId.
                    • Usuários com papel 'admin' podem consultar o perfil de qualquer cliente.
                    """
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Perfil de risco encontrado com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = PerfilRiscoDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Perfil de risco não encontrado para o cliente informado.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "422",
                    description = "Operação não permitida para o usuário autenticado (acesso a dados de outro cliente).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Requisição sem autenticação válida (token ausente ou inválido).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "403",
                    description = "Usuário autenticado, porém sem permissão para acessar este recurso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Erro interno ao processar a requisição.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            )
    })
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
    @Operation(
            summary = "Consultar perfil de risco do cliente autenticado",
            description = """
                    Retorna o perfil de risco associado ao cliente vinculado ao usuário autenticado.
                    
                    • O cliente é obtido a partir das informações de autenticação (token JWT).
                    """
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Perfil de risco do cliente autenticado retornado com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = PerfilRiscoDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Cliente autenticado não possui perfil de risco cadastrado ou não está vinculado corretamente.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Requisição sem autenticação válida (token ausente ou inválido).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Erro interno ao processar a requisição.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            )
    })
    public PerfilRiscoDto obterPerfilRiscoDoClienteAutenticado() {
        Long clienteId = contextoClienteService.obterClienteIdUsuarioObrigatorio();
        return obterPerfilRisco(clienteId);
    }
}