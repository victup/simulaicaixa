package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.dto.RespostaSimulacaoDto;
import br.gov.caixa.simulaicaixa.dto.SimulacaoHistoricoDto;
import br.gov.caixa.simulaicaixa.dto.SimulacaoPorProdutoDiaDto;
import br.gov.caixa.simulaicaixa.dto.SolicitacaoSimulacaoDto;
import br.gov.caixa.simulaicaixa.service.SimulacaoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulacaoResource {

    private final SimulacaoService simulacaoService;

    @Inject
    public SimulacaoResource(SimulacaoService simulacaoService) {
        this.simulacaoService = simulacaoService;
    }

    @POST
    @Path("/simular-investimento")
    public RespostaSimulacaoDto simularInvestimento(SolicitacaoSimulacaoDto solicitacao) {
        return simulacaoService.simularInvestimento(solicitacao);
    }

    @GET
    @Path("/simulacoes")
    public List<SimulacaoHistoricoDto> listarSimulacoes() {
        return simulacaoService.listarSimulacoes();
    }

    @GET
    @Path("/simulacoes/por-produto-dia")
    public List<SimulacaoPorProdutoDiaDto> listarSimulacoesPorProdutoEDia() {
        return simulacaoService.listarSimulacoesPorProdutoEDia();
    }
}
