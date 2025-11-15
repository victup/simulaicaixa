package br.gov.caixa.simulaicaixa.data.repository;

import br.gov.caixa.simulaicaixa.domain.SimulacaoInvestimento;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class SimulacaoInvestimentoRepositoryImpl implements SimulacaoInvestimentoRepository {

    @Override
    public SimulacaoInvestimento salvar(SimulacaoInvestimento simulacaoInvestimento) {
        throw new IllegalStateException("Persistência de simulação ainda não implementada");
    }

    @Override
    public List<SimulacaoInvestimento> listarTodas() {
        return Collections.emptyList();
    }

    @Override
    public List<SimulacaoInvestimento> listarPorProdutoEDia(String nomeProduto, LocalDate data) {
        return Collections.emptyList();
    }
}
