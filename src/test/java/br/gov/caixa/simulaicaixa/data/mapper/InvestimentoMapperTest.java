package br.gov.caixa.simulaicaixa.data.mapper;

import br.gov.caixa.simulaicaixa.data.entity.InvestimentoEntity;
import br.gov.caixa.simulaicaixa.domain.Investimento;
import br.gov.caixa.simulaicaixa.domain.enums.TipoInvestimentoEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InvestimentoMapperTest {

    @Test
    void deveMapearDominioParaEntityComTipo() {
        Investimento investimento = new Investimento();
        investimento.setId(10L);
        investimento.setClienteId(123L);
        investimento.setTipo(TipoInvestimentoEnum.CDB);
        investimento.setValor(new BigDecimal("1000.00"));
        investimento.setRentabilidade(new BigDecimal("0.12"));
        investimento.setData(LocalDate.of(2025, 1, 1));

        InvestimentoEntity entity = InvestimentoMapper.mapearParaEntity(investimento);

        assertEquals(10L, entity.getId());
        assertEquals(123L, entity.getClienteId());
        assertEquals(TipoInvestimentoEnum.CDB.getCodigo(), entity.getTipo());
        assertEquals(new BigDecimal("1000.00"), entity.getValor());
        assertEquals(new BigDecimal("0.12"), entity.getRentabilidade());
        assertEquals(LocalDate.of(2025, 1, 1), entity.getData());
    }

    @Test
    void deveMapearDominioParaEntityComTipoNulo() {
        Investimento investimento = new Investimento();
        investimento.setId(11L);
        investimento.setClienteId(456L);
        investimento.setTipo(null);
        investimento.setValor(new BigDecimal("500.00"));
        investimento.setRentabilidade(new BigDecimal("0.08"));
        investimento.setData(LocalDate.of(2025, 2, 2));

        InvestimentoEntity entity = InvestimentoMapper.mapearParaEntity(investimento);

        assertNull(entity.getTipo());
    }

    @Test
    void deveMapearEntityParaDominioComTipo() {
        InvestimentoEntity entity = new InvestimentoEntity();
        entity.setId(20L);
        entity.setClienteId(999L);
        entity.setTipo(TipoInvestimentoEnum.FUNDO.getCodigo());
        entity.setValor(new BigDecimal("3000.00"));
        entity.setRentabilidade(new BigDecimal("0.18"));
        entity.setData(LocalDate.of(2025, 3, 3));

        Investimento investimento = InvestimentoMapper.mapearParaDominio(entity);

        assertEquals(20L, investimento.getId());
        assertEquals(999L, investimento.getClienteId());
        assertEquals(TipoInvestimentoEnum.FUNDO, investimento.getTipo());
        assertEquals(new BigDecimal("3000.00"), investimento.getValor());
        assertEquals(new BigDecimal("0.18"), investimento.getRentabilidade());
        assertEquals(LocalDate.of(2025, 3, 3), investimento.getData());
    }
}