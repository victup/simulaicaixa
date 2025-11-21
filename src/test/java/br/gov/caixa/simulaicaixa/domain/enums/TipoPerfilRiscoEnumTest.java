package br.gov.caixa.simulaicaixa.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TipoPerfilRiscoEnumTest {

    @Test
    void deveObterPorCodigoQuandoCodigoExistir() {
        assertEquals(TipoPerfilRiscoEnum.CONSERVADOR, TipoPerfilRiscoEnum.obterPorCodigo(1));
        assertEquals(TipoPerfilRiscoEnum.MODERADO, TipoPerfilRiscoEnum.obterPorCodigo(2));
        assertEquals(TipoPerfilRiscoEnum.AGRESSIVO, TipoPerfilRiscoEnum.obterPorCodigo(3));
    }

    @Test
    void deveRetornarNullQuandoCodigoForNuloOuInvalido() {
        assertNull(TipoPerfilRiscoEnum.obterPorCodigo(null));
        assertNull(TipoPerfilRiscoEnum.obterPorCodigo(99));
    }

    @Test
    void deveObterPorDescricaoIgnorandoCaixaEEspacos() {
        assertEquals(TipoPerfilRiscoEnum.CONSERVADOR, TipoPerfilRiscoEnum.obterPorDescricao("Conservador"));
        assertEquals(TipoPerfilRiscoEnum.MODERADO, TipoPerfilRiscoEnum.obterPorDescricao("  moderado "));
        assertEquals(TipoPerfilRiscoEnum.AGRESSIVO, TipoPerfilRiscoEnum.obterPorDescricao("AGRESSIVO"));
        assertEquals(TipoPerfilRiscoEnum.CONSERVADOR, TipoPerfilRiscoEnum.obterPorDescricao("conservador"));
        assertEquals(TipoPerfilRiscoEnum.MODERADO, TipoPerfilRiscoEnum.obterPorDescricao("MODERADO"));
    }

    @Test
    void deveRetornarNullQuandoDescricaoForNulaOuInvalida() {
        assertNull(TipoPerfilRiscoEnum.obterPorDescricao(null));
        assertNull(TipoPerfilRiscoEnum.obterPorDescricao("desconhecido"));
    }
}