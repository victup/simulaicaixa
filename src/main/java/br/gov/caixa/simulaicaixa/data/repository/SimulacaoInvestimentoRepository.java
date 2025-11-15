package br.gov.caixa.simulaicaixa.data.repository;

import br.gov.caixa.simulaicaixa.domain.SimulacaoInvestimento;

import java.time.LocalDate;
import java.util.List;

public interface SimulacaoInvestimentoRepository {

    SimulacaoInvestimento salvar(SimulacaoInvestimento simulacaoInvestimento);

    List<SimulacaoInvestimento> listarTodas();

    List<SimulacaoInvestimento> listarPorProdutoEDia(String nomeProduto, LocalDate data);
}