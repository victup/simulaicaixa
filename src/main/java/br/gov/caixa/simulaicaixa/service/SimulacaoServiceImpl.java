package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.data.repository.SimulacaoInvestimentoRepository;
import br.gov.caixa.simulaicaixa.dto.RespostaSimulacaoDto;
import br.gov.caixa.simulaicaixa.dto.SimulacaoHistoricoDto;
import br.gov.caixa.simulaicaixa.dto.SimulacaoPorProdutoDiaDto;
import br.gov.caixa.simulaicaixa.dto.SolicitacaoSimulacaoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class SimulacaoServiceImpl implements SimulacaoService{

    private final SimulacaoInvestimentoRepository simulacaoInvestimentoRepository;

    @Inject
    public SimulacaoServiceImpl(SimulacaoInvestimentoRepository simulacaoInvestimentoRepository) {
        this.simulacaoInvestimentoRepository = simulacaoInvestimentoRepository;
    }

    @Override
    public RespostaSimulacaoDto simularInvestimento(SolicitacaoSimulacaoDto solicitacao) {
        return null;
    }

    @Override
    public List<SimulacaoHistoricoDto> listarSimulacoes() {
        return List.of();
    }

    @Override
    public List<SimulacaoPorProdutoDiaDto> listarSimulacoesPorProdutoEDia() {
        return List.of();
    }
}
