package br.gov.caixa.simulaicaixa.data.repository;

import br.gov.caixa.simulaicaixa.domain.SimulacaoInvestimento;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@ApplicationScoped
public class SimulacaoInvestimentoRepositoryImpl implements SimulacaoInvestimentoRepository {

    private final List<SimulacaoInvestimento> simulacoes = new ArrayList<>();
    private final AtomicLong sequenciaId = new AtomicLong(1);

    @Override
    public synchronized SimulacaoInvestimento salvar(SimulacaoInvestimento simulacaoInvestimento) {
        if (simulacaoInvestimento.getId() == null) {
            simulacaoInvestimento.setId(sequenciaId.getAndIncrement());
        }

        if (simulacaoInvestimento.getDataSimulacao() == null) {
            simulacaoInvestimento.setDataSimulacao(OffsetDateTime.now());
        }

        simulacoes.add(simulacaoInvestimento);
        return simulacaoInvestimento;
    }

    @Override
    public synchronized List<SimulacaoInvestimento> listarTodas() {
        return new ArrayList<>(simulacoes);
    }

    @Override
    public synchronized List<SimulacaoInvestimento> listarPorProdutoEDia(String nomeProduto, LocalDate data) {
        return simulacoes.stream()
                .filter(simulacao -> nomeProduto.equals(simulacao.getNomeProduto())
                        && simulacao.getDataSimulacao() != null
                        && data.equals(simulacao.getDataSimulacao().toLocalDate()))
                .collect(Collectors.toList());
    }
}
