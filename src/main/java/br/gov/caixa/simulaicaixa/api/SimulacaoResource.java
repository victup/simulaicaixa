package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.dto.RespostaSimulacaoDto;
import br.gov.caixa.simulaicaixa.dto.SimulacaoHistoricoDto;
import br.gov.caixa.simulaicaixa.dto.SimulacaoPorProdutoDiaDto;
import br.gov.caixa.simulaicaixa.dto.SolicitacaoSimulacaoDto;
import br.gov.caixa.simulaicaixa.service.SimulacaoService;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.Duration;
import java.util.List;

@Path("/simular-investimento")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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
    public RespostaSimulacaoDto simularInvestimento(SolicitacaoSimulacaoDto solicitacao) {
        long inicio = System.nanoTime();

        RespostaSimulacaoDto resposta = simulacaoService.simularInvestimento(solicitacao);

        long fim = System.nanoTime();
        long duracaoMs = Duration.ofNanos(fim - inicio).toMillis();

        coletorTelemetria.registrarChamada("simular-investimento", duracaoMs);

        return resposta;
    }
}