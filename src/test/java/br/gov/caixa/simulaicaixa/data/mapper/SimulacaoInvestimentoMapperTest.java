package br.gov.caixa.simulaicaixa.data.mapper;

import br.gov.caixa.simulaicaixa.data.entity.SimulacaoInvestimentoEntity;
import br.gov.caixa.simulaicaixa.domain.SimulacaoInvestimento;
import br.gov.caixa.simulaicaixa.domain.enums.TipoInvestimentoEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SimulacaoInvestimentoMapperTest {

    @Test
    void deveMapearDominioParaEntityComTipo() {
        SimulacaoInvestimento simulacao = new SimulacaoInvestimento();
        simulacao.setId(1L);
        simulacao.setClienteId(123L);
        simulacao.setNomeProduto("Produto CDB");
        simulacao.setTipoProduto(TipoInvestimentoEnum.CDB.getDescricao());
        simulacao.setValorInvestido(new BigDecimal("10000.00"));
        simulacao.setValorFinal(new BigDecimal("11200.00"));
        simulacao.setPrazoMeses(12);
        OffsetDateTime agora = OffsetDateTime.now();
        simulacao.setDataSimulacao(agora);

        SimulacaoInvestimentoEntity entity = SimulacaoInvestimentoMapper.mapearParaEntity(simulacao);

        assertEquals(1L, entity.getId());
        assertEquals(123L, entity.getClienteId());
        assertEquals("Produto CDB", entity.getNomeProduto());
        assertEquals(simulacao.getTipoProduto().name(), entity.getTipoProduto());
        assertEquals(new BigDecimal("10000.00"), entity.getValorInvestido());
        assertEquals(new BigDecimal("11200.00"), entity.getValorFinal());
        assertEquals(12, entity.getPrazoMeses());
        assertEquals(agora, entity.getDataSimulacao());
    }

    @Test
    void deveMapearDominioParaEntityComTipoNulo() {
        SimulacaoInvestimento simulacao = new SimulacaoInvestimento();
        simulacao.setId(10L);
        simulacao.setClienteId(123L);
        simulacao.setNomeProduto("Produto qualquer");
        simulacao.setValorInvestido(new BigDecimal("1000.00"));
        simulacao.setValorFinal(new BigDecimal("1100.00"));
        simulacao.setPrazoMeses(12);
        simulacao.setDataSimulacao(OffsetDateTime.parse("2025-01-01T10:00:00Z"));

        SimulacaoInvestimentoEntity entity =
                SimulacaoInvestimentoMapper.mapearParaEntity(simulacao);

        assertEquals(10L, entity.getId());
        assertEquals(123L, entity.getClienteId());
        assertEquals("Produto qualquer", entity.getNomeProduto());
        assertNull(entity.getTipoProduto()); // aqui agora passa
        assertEquals(new BigDecimal("1000.00"), entity.getValorInvestido());
        assertEquals(new BigDecimal("1100.00"), entity.getValorFinal());
        assertEquals(12, entity.getPrazoMeses());
        assertEquals(OffsetDateTime.parse("2025-01-01T10:00:00Z"), entity.getDataSimulacao());
    }

    @Test
    void deveMapearEntityParaDominio() {
        SimulacaoInvestimento simulacaoOriginal = new SimulacaoInvestimento();
        simulacaoOriginal.setId(3L);
        simulacaoOriginal.setClienteId(999L);
        simulacaoOriginal.setNomeProduto("LCI Caixa");
        simulacaoOriginal.setTipoProduto(TipoInvestimentoEnum.LCI.getDescricao());
        simulacaoOriginal.setValorInvestido(new BigDecimal("7000.00"));
        simulacaoOriginal.setValorFinal(new BigDecimal("7600.00"));
        simulacaoOriginal.setPrazoMeses(10);
        OffsetDateTime agora = OffsetDateTime.now();
        simulacaoOriginal.setDataSimulacao(agora);

        SimulacaoInvestimentoEntity entity =
                SimulacaoInvestimentoMapper.mapearParaEntity(simulacaoOriginal);

        SimulacaoInvestimento simulacaoMapeada =
                SimulacaoInvestimentoMapper.mapearParaDominio(entity);

        assertEquals(simulacaoOriginal.getId(), simulacaoMapeada.getId());
        assertEquals(simulacaoOriginal.getClienteId(), simulacaoMapeada.getClienteId());
        assertEquals(simulacaoOriginal.getNomeProduto(), simulacaoMapeada.getNomeProduto());
        assertEquals(simulacaoOriginal.getTipoProduto(), simulacaoMapeada.getTipoProduto());
        assertEquals(simulacaoOriginal.getValorInvestido(), simulacaoMapeada.getValorInvestido());
        assertEquals(simulacaoOriginal.getValorFinal(), simulacaoMapeada.getValorFinal());
        assertEquals(simulacaoOriginal.getPrazoMeses(), simulacaoMapeada.getPrazoMeses());
        assertEquals(simulacaoOriginal.getDataSimulacao(), simulacaoMapeada.getDataSimulacao());
    }
}