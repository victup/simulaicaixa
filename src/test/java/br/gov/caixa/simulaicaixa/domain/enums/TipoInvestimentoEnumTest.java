package br.gov.caixa.simulaicaixa.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TipoInvestimentoEnumTest {

    @Test
    void deveObterPorCodigoQuandoCodigoExistir() {
        assertEquals(TipoInvestimentoEnum.CDB, TipoInvestimentoEnum.obterPorCodigo(1));
        assertEquals(TipoInvestimentoEnum.FUNDO, TipoInvestimentoEnum.obterPorCodigo(2));
        assertEquals(TipoInvestimentoEnum.TESOURO, TipoInvestimentoEnum.obterPorCodigo(5));
    }

    @Test
    void deveRetornarOutroQuandoCodigoForNuloOuInvalido() {
        assertEquals(TipoInvestimentoEnum.OUTRO, TipoInvestimentoEnum.obterPorCodigo(null));
        assertEquals(TipoInvestimentoEnum.OUTRO, TipoInvestimentoEnum.obterPorCodigo(999));
    }

    @Test
    void deveObterPorDescricaoIgnorandoCaixaEEspacos() {
        assertEquals(TipoInvestimentoEnum.CDB, TipoInvestimentoEnum.obterPorDescricao("CDB"));
        assertEquals(TipoInvestimentoEnum.FUNDO, TipoInvestimentoEnum.obterPorDescricao("fundo"));
        assertEquals(TipoInvestimentoEnum.LCI, TipoInvestimentoEnum.obterPorDescricao("  LCI "));
        assertEquals(TipoInvestimentoEnum.LCA, TipoInvestimentoEnum.obterPorDescricao("lca"));
        assertEquals(TipoInvestimentoEnum.TESOURO, TipoInvestimentoEnum.obterPorDescricao("tesouro direto"));
        assertEquals(TipoInvestimentoEnum.TESOURO, TipoInvestimentoEnum.obterPorDescricao("TESOURO"));
    }

    @Test
    void deveRetornarOutroQuandoDescricaoForNulaOuInvalida() {
        assertEquals(TipoInvestimentoEnum.OUTRO, TipoInvestimentoEnum.obterPorDescricao(null));
        assertEquals(TipoInvestimentoEnum.OUTRO, TipoInvestimentoEnum.obterPorDescricao("desconhecido"));
    }
}