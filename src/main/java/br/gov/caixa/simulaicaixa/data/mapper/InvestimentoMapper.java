package br.gov.caixa.simulaicaixa.data.mapper;

import br.gov.caixa.simulaicaixa.data.entity.InvestimentoEntity;
import br.gov.caixa.simulaicaixa.domain.Investimento;

public final class InvestimentoMapper {

    private InvestimentoMapper() {
    }

    public static InvestimentoEntity mapearParaEntity(Investimento investimento) {
        InvestimentoEntity entity = new InvestimentoEntity();

        entity.setId(investimento.getId());
        entity.setClienteId(investimento.getClienteId());
        entity.setTipo(investimento.getTipo());
        entity.setValor(investimento.getValor());
        entity.setRentabilidade(investimento.getRentabilidade());
        entity.setData(investimento.getData());

        return entity;
    }

    public static Investimento mapearParaDominio(InvestimentoEntity entity) {
        Investimento investimento = new Investimento();

        investimento.setId(entity.getId());
        investimento.setClienteId(entity.getClienteId());
        investimento.setTipo(entity.getTipo());
        investimento.setValor(entity.getValor());
        investimento.setRentabilidade(entity.getRentabilidade());
        investimento.setData(entity.getData());

        return investimento;
    }
}