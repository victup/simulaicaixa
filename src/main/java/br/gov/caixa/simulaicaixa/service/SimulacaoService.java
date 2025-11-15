package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.dto.RespostaSimulacaoDto;
import br.gov.caixa.simulaicaixa.dto.SimulacaoHistoricoDto;
import br.gov.caixa.simulaicaixa.dto.SimulacaoPorProdutoDiaDto;
import br.gov.caixa.simulaicaixa.dto.SolicitacaoSimulacaoDto;

import java.util.List;

public interface SimulacaoService {
    RespostaSimulacaoDto simularInvestimento(SolicitacaoSimulacaoDto solicitacao);

    List<SimulacaoHistoricoDto> listarSimulacoes();

    List<SimulacaoPorProdutoDiaDto> listarSimulacoesPorProdutoEDia();
}
