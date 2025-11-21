package br.gov.caixa.simulaicaixa.core.erro;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodigoErroNegocioTest {

    @Test
    void deveConterInformacoesCorretasParaClienteNaoEncontrado() {
        CodigoErroNegocio codigo = CodigoErroNegocio.CLIENTE_NAO_ENCONTRADO;

        assertEquals("CLI-0001", codigo.getCodigoInterno());
        assertEquals("Cliente não encontrado.", codigo.getMensagemPadrao());
        assertEquals(404, codigo.getStatusHttp());
    }

    @Test
    void todosOsCodigosDevemPossuirCamposObrigatorios() {
        for (CodigoErroNegocio codigo : CodigoErroNegocio.values()) {
            assertNotNull(codigo.getCodigoInterno());
            assertFalse(codigo.getCodigoInterno().isBlank());

            assertNotNull(codigo.getMensagemPadrao());
            assertFalse(codigo.getMensagemPadrao().isBlank());

            assertTrue(codigo.getStatusHttp() >= 400 && codigo.getStatusHttp() <= 599);
        }
    }
}