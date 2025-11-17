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

@Path("/")
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
    @Path("/simular-investimento")
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
    public List<SimulacaoPorProdutoDiaDto> listarSimulacoesPorProdutoDia() {
        long inicio = System.nanoTime();

        List<SimulacaoPorProdutoDiaDto> simulacoes = simulacaoService.listarSimulacoesPorProdutoEDia();

        long fim = System.nanoTime();
        long duracaoMs = Duration.ofNanos(fim - inicio).toMillis();

        coletorTelemetria.registrarChamada("simulacoes-por-produto-dia", duracaoMs);

        return simulacoes;
    }
}