package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.erro.ErroResposta;
import br.gov.caixa.simulaicaixa.core.seguranca.ValidadorAcessoCliente;
import br.gov.caixa.simulaicaixa.dto.InvestimentoHistoricoDto;
import br.gov.caixa.simulaicaixa.service.ContextoClienteService;
import br.gov.caixa.simulaicaixa.service.InvestimentoService;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.Duration;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Path("/investimentos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"cliente", "admin"})
@Tag(
        name = "Investimentos",
        description = """
                Consulta do histórico de investimentos.
                
                Regras de acesso:
                • Usuários com papel "cliente" só podem consultar o próprio histórico.
                • Usuários com papel "admin" podem consultar o histórico de qualquer cliente.
                """
)
public class InvestimentoResource {

    private final InvestimentoService investimentoService;
    private final ColetorTelemetria coletorTelemetria;
    private final ContextoClienteService contextoClienteService;
    private final ValidadorAcessoCliente validadorAcessoCliente;

    @Inject
    public InvestimentoResource(InvestimentoService investimentoService,
                                ColetorTelemetria coletorTelemetria,
                                ContextoClienteService contextoClienteService,
                                ValidadorAcessoCliente validadorAcessoCliente) {
        this.investimentoService = investimentoService;
        this.coletorTelemetria = coletorTelemetria;
        this.contextoClienteService = contextoClienteService;
        this.validadorAcessoCliente = validadorAcessoCliente;
    }

    @GET
    @Path("/{clienteId}")
    @Operation(
            summary = "Listar histórico de investimentos de um cliente",
            description = """
                    Retorna o histórico de investimentos do cliente informado.
                    
                    • Clientes autenticados só podem consultar o próprio identificador.
                    • Usuários com papel 'admin' podem consultar o histórico de qualquer cliente.
                    """
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Histórico de investimentos retornado com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = InvestimentoHistoricoDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Cliente não possui histórico de investimentos ou não foi encontrado.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "422",
                    description = "Operação não permitida para o usuário autenticado (acesso a outro cliente sem permissão).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado ou token inválido.",
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
    public List<InvestimentoHistoricoDto> listarPorCliente(
            @Parameter(
                    description = """
                            Identificador do cliente cujo histórico será consultado.
                            • Para usuários 'cliente', deve ser o próprio identificador.
                            • Para usuários 'admin', pode ser qualquer cliente existente.
                            """,
                    required = true,
                    example = "123"
            )
            @PathParam("clienteId") Long clienteId) {

        validadorAcessoCliente.validarClienteOuAdmin(
                clienteId,
                CodigoErroNegocio.OPERACAO_NAO_PERMITIDA_PARA_INVESTIMENTOS
        );

        long inicio = System.nanoTime();

        List<InvestimentoHistoricoDto> investimentos = investimentoService.listarPorClienteId(clienteId);

        long fim = System.nanoTime();
        long duracaoMs = Duration.ofNanos(fim - inicio).toMillis();

        coletorTelemetria.registrarChamada("investimentos-por-cliente", duracaoMs);

        return investimentos;
    }

    @GET
    @Path("/usuario-logado")
    @Operation(
            summary = "Listar histórico de investimentos do cliente autenticado",
            description = """
                    Retorna o histórico de investimentos associado ao usuário autenticado.
                    O identificador do cliente é obtido a partir do token JWT.
                    """
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Histórico de investimentos do cliente autenticado retornado com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = InvestimentoHistoricoDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Cliente não encontrado ou sem histórico de investimentos cadastrado.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado ou token inválido.",
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
    public List<InvestimentoHistoricoDto> listarDoClienteAutenticado() {
        Long clienteId = contextoClienteService.obterClienteIdUsuarioObrigatorio();

        long inicio = System.nanoTime();

        List<InvestimentoHistoricoDto> investimentos =
                investimentoService.listarPorClienteId(clienteId);

        long fim = System.nanoTime();
        long duracaoMs = Duration.ofNanos(fim - inicio).toMillis();

        coletorTelemetria.registrarChamada("investimentos-usuario-logado", duracaoMs);

        return investimentos;
    }
}