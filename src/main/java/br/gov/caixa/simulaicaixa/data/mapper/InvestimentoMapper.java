package br.gov.caixa.simulaicaixa.data.mapper;

import br.gov.caixa.simulaicaixa.data.entity.InvestimentoEntity;
import br.gov.caixa.simulaicaixa.domain.Investimento;
import br.gov.caixa.simulaicaixa.domain.enums.TipoInvestimentoEnum;

public final class InvestimentoMapper {

    private InvestimentoMapper() {
    }

    public static InvestimentoEntity mapearParaEntity(Investimento investimento) {
        InvestimentoEntity entity = new InvestimentoEntity();

        entity.setId(investimento.getId());
        entity.setClienteId(investimento.getClienteId());

        TipoInvestimentoEnum tipo = investimento.getTipo();
        entity.setTipo(tipo != null ? tipo.getCodigo() : null);

        entity.setValor(investimento.getValor());
        entity.setRentabilidade(investimento.getRentabilidade());
        entity.setData(investimento.getData());

        return entity;
    }

    public static Investimento mapearParaDominio(InvestimentoEntity entity) {
        Investimento investimento = new Investimento();

        investimento.setId(entity.getId());
        investimento.setClienteId(entity.getClienteId());
        investimento.setTipo(TipoInvestimentoEnum.obterPorCodigo(entity.getTipo()));
        investimento.setValor(entity.getValor());
        investimento.setRentabilidade(entity.getRentabilidade());
        investimento.setData(entity.getData());

        return investimento;
    }
}