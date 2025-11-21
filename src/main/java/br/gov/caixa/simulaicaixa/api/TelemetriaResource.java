package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.core.erro.ErroResposta;
import br.gov.caixa.simulaicaixa.dto.TelemetriaDto;
import br.gov.caixa.simulaicaixa.service.TelemetriaService;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.Duration;
import java.time.LocalDate;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Path("/telemetria")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("admin")
@Tag(
        name = "Telemetria",
        description = "Consulta de métricas de telemetria da API para acompanhamento e monitoramento."
)
public class TelemetriaResource {

    private final TelemetriaService telemetriaService;
    private final ColetorTelemetria coletorTelemetria;

    @Inject
    public TelemetriaResource(TelemetriaService telemetriaService,
                              ColetorTelemetria coletorTelemetria) {
        this.telemetriaService = telemetriaService;
        this.coletorTelemetria = coletorTelemetria;
    }

    @GET
    @Operation(
            summary = "Obter telemetria geral",
            description = """
                    Retorna uma visão geral da telemetria da API, com métricas agregadas por serviço.
                    
                    • Inclui quantidade de chamadas e tempo médio de resposta por serviço monitorado.
                    • Indicada para uso em painéis de monitoramento e análise operacional.
                    • Acesso restrito ao papel 'admin'.
                    """
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Telemetria geral retornada com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = TelemetriaDto.class)
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
                    description = "Usuário autenticado não possui permissão para acessar este recurso (somente 'admin').",
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
    public TelemetriaDto obterTelemetria() {
        long inicio = System.nanoTime();

        TelemetriaDto telemetria = telemetriaService.obterTelemetriaGeral();

        long fim = System.nanoTime();
        long duracaoMs = Duration.ofNanos(fim - inicio).toMillis();

        coletorTelemetria.registrarChamada("telemetria", duracaoMs);

        return telemetria;
    }

    @GET
    @Path("/resumo")
    @Operation(
            summary = "Obter resumo de telemetria por período",
            description = """
                    Retorna um resumo de telemetria filtrado por período (data inicial e final).
                    
                    • As datas são informadas via query string (inicio, fim) no formato ISO (yyyy-MM-dd).
                    • Quando não informadas, podem ser aplicados padrões de período recente conforme a implementação.
                    • Útil para análise histórica de desempenho em janelas específicas de tempo.
                    • Acesso restrito ao papel 'admin'.
                    """
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Resumo de telemetria por período retornado com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = TelemetriaDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Período informado é inválido (por exemplo, data inicial maior que a data final).",
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
                    description = "Usuário autenticado não possui permissão para acessar este recurso (somente 'admin').",
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
    public TelemetriaDto obterResumoPorPeriodo(
            @QueryParam("inicio") LocalDate inicio,
            @QueryParam("fim") LocalDate fim) {

        long inicioNanos = System.nanoTime();

        TelemetriaDto telemetria = telemetriaService.obterResumoPorPeriodo(inicio, fim);

        long fimNanos = System.nanoTime();
        long duracaoMs = Duration.ofNanos(fimNanos - inicioNanos).toMillis();

        coletorTelemetria.registrarChamada("telemetria-resumo", duracaoMs);

        return telemetria;
    }
}