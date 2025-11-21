package br.gov.caixa.simulaicaixa.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NivelRiscoProdutoEnumTest {

    @Test
    void deveObterPorCodigoQuandoCodigoExistir() {
        assertEquals(NivelRiscoProdutoEnum.BAIXO, NivelRiscoProdutoEnum.obterPorCodigo(1));
        assertEquals(NivelRiscoProdutoEnum.MEDIO, NivelRiscoProdutoEnum.obterPorCodigo(2));
        assertEquals(NivelRiscoProdutoEnum.ALTO, NivelRiscoProdutoEnum.obterPorCodigo(3));
    }

    @Test
    void deveRetornarNullQuandoCodigoForNuloOuInvalido() {
        assertNull(NivelRiscoProdutoEnum.obterPorCodigo(null));
        assertNull(NivelRiscoProdutoEnum.obterPorCodigo(99));
    }

    @Test
    void deveObterPorDescricaoIgnorandoCaixaEEspacos() {
        assertEquals(NivelRiscoProdutoEnum.BAIXO, NivelRiscoProdutoEnum.obterPorDescricao("Baixo"));
        assertEquals(NivelRiscoProdutoEnum.MEDIO, NivelRiscoProdutoEnum.obterPorDescricao("  médio "));
        assertEquals(NivelRiscoProdutoEnum.ALTO, NivelRiscoProdutoEnum.obterPorDescricao("ALTO"));
        assertEquals(NivelRiscoProdutoEnum.BAIXO, NivelRiscoProdutoEnum.obterPorDescricao("baixo"));
        assertEquals(NivelRiscoProdutoEnum.MEDIO, NivelRiscoProdutoEnum.obterPorDescricao("MEDIO"));
    }

    @Test
    void deveRetornarNullQuandoDescricaoForNulaOuInvalida() {
        assertNull(NivelRiscoProdutoEnum.obterPorDescricao(null));
        assertNull(NivelRiscoProdutoEnum.obterPorDescricao("desconhecido"));
    }
}