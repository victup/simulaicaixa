package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.core.erro.ErroResposta;
import br.gov.caixa.simulaicaixa.dto.RespostaSimulacaoDto;
import br.gov.caixa.simulaicaixa.dto.SimulacaoHistoricoDto;
import br.gov.caixa.simulaicaixa.dto.SimulacaoPorProdutoDiaDto;
import br.gov.caixa.simulaicaixa.dto.SolicitacaoSimulacaoDto;
import br.gov.caixa.simulaicaixa.service.SimulacaoService;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.Duration;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"cliente", "admin"})
@Tag(
        name = "Simulações",
        description = "Operações de simulação de investimento e consulta de históricos de simulações."
)
public class SimulacaoResource {

    private final SimulacaoService simulacaoService;
    private final ColetorTelemetria coletorTelemetria;

    @Inject
    public SimulacaoResource(SimulacaoService simulacaoService,
                             ColetorTelemetria coletorTelemetria) {
        this.simulacaoService = simulacaoService;
        this.coletorTelemetria = coletorTelemetria;
    }

    @POST
    @Path("/simular-investimento")
    @Operation(
            summary = "Simular investimento",
            description = """
                    Realiza a simulação de um investimento com base no valor, prazo e tipo de produto informados.
                    
                    • Para usuários com papel 'cliente', a simulação é sempre vinculada ao cliente do token.
                    • Para usuários com papel 'admin', é possível informar explicitamente o cliente no payload.
                    • A resposta traz o produto validado (tipo, risco, rentabilidade) e o resultado da simulação
                      (valor final, rentabilidade efetiva, prazo).
                    """
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Simulação realizada com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = RespostaSimulacaoDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Parâmetros da simulação inválidos (valor, prazo ou tipo de produto).",
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
                    description = "Usuário autenticado não possui permissão para acessar este recurso.",
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
    public RespostaSimulacaoDto simularInvestimento(SolicitacaoSimulacaoDto solicitacao) {
        long inicio = System.nanoTime();

        RespostaSimulacaoDto resposta = simulacaoService.simularInvestimento(solicitacao);

        long fim = System.nanoTime();
        long duracaoMs = Duration.ofNanos(fim - inicio).toMillis();

        coletorTelemetria.registrarChamada("simular-investimento", duracaoMs);

        return resposta;
    }

    @GET
    @Path("/simulacoes")
    @Operation(
            summary = "Listar simulações",
            description = """
                    Lista o histórico de simulações de investimento.
                    
                    • Usuários com papel 'cliente' visualizam apenas simulações associadas ao seu próprio cliente.
                    • Usuários com papel 'admin' podem visualizar simulações de todos os clientes.
                    • Em caso de ausência de registros, é retornada uma lista vazia.
                    """
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Lista de simulações retornada com sucesso (pode ser vazia).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = SimulacaoHistoricoDto.class)
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
                    description = "Usuário autenticado não possui permissão para acessar este recurso.",
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
    public List<SimulacaoHistoricoDto> listarSimulacoes() {
        long inicio = System.nanoTime();

        List<SimulacaoHistoricoDto> simulacoes = simulacaoService.listarSimulacoes();

        long fim = System.nanoTime();
        long duracaoMs = Duration.ofNanos(fim - inicio).toMillis();

        coletorTelemetria.registrarChamada("listar-simulacoes", duracaoMs);

        return simulacoes;
    }

    @GET
    @Path("/simulacoes/por-produto-dia")
    @Operation(
            summary = "Listar simulações agregadas por produto e dia",
            description = """
                    Retorna um resumo de simulações agregadas por produto e por dia.
                    
                    • Cada registro representa um produto em uma data específica, com quantidade de simulações
                      e média do valor final.
                    • Usuários com papel 'cliente' visualizam agregados apenas das suas simulações.
                    • Usuários com papel 'admin' podem visualizar agregados considerando simulações de todos os clientes.
                    """
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Resumo de simulações por produto e dia retornado com sucesso (pode ser vazio).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = SimulacaoPorProdutoDiaDto.class)
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
                    description = "Usuário autenticado não possui permissão para acessar este recurso.",
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
    public List<SimulacaoPorProdutoDiaDto> listarSimulacoesPorProdutoDia() {
        long inicio = System.nanoTime();

        List<SimulacaoPorProdutoDiaDto> simulacoes = simulacaoService.listarSimulacoesPorProdutoEDia();

        long fim = System.nanoTime();
        long duracaoMs = Duration.ofNanos(fim - inicio).toMillis();

        coletorTelemetria.registrarChamada("simulacoes-por-produto-dia", duracaoMs);

        return simulacoes;
    }
}