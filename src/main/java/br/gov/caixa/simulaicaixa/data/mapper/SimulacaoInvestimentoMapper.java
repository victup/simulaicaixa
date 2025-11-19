package br.gov.caixa.simulaicaixa.data.mapper;

import br.gov.caixa.simulaicaixa.data.entity.SimulacaoInvestimentoEntity;
import br.gov.caixa.simulaicaixa.domain.SimulacaoInvestimento;

public final class SimulacaoInvestimentoMapper {

    private SimulacaoInvestimentoMapper() {
    }

    public static SimulacaoInvestimentoEntity mapearParaEntity(SimulacaoInvestimento simulacao) {
        SimulacaoInvestimentoEntity entity = new SimulacaoInvestimentoEntity();

        entity.setId(simulacao.getId());
        entity.setClienteId(simulacao.getClienteId());
        entity.setNomeProduto(simulacao.getNomeProduto());
        entity.setTipoProduto(
                simulacao.getTipoProduto() != null
                        ? simulacao.getTipoProduto().name()
                        : null
        );
        entity.setValorInvestido(simulacao.getValorInvestido());
        entity.setValorFinal(simulacao.getValorFinal());
        entity.setPrazoMeses(simulacao.getPrazoMeses());
        entity.setDataSimulacao(simulacao.getDataSimulacao());

        return entity;
    }

    public static SimulacaoInvestimento mapearParaDominio(SimulacaoInvestimentoEntity entity) {
        SimulacaoInvestimento simulacao = new SimulacaoInvestimento();

        simulacao.setId(entity.getId());
        simulacao.setClienteId(entity.getClienteId());
        simulacao.setNomeProduto(entity.getNomeProduto());
        simulacao.setTipoProduto(entity.getTipoProduto());
        simulacao.setValorInvestido(entity.getValorInvestido());
        simulacao.setValorFinal(entity.getValorFinal());
        simulacao.setPrazoMeses(entity.getPrazoMeses());
        simulacao.setDataSimulacao(entity.getDataSimulacao());

        return simulacao;
    }
}