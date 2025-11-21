package br.gov.caixa.simulaicaixa.core.erro;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErroRespostaTest {

    @Test
    void deveExporCamposInformadosNoConstrutor() {
        ErroResposta erro = new ErroResposta(
                "CODIGO_TESTE",
                "Mensagem de erro",
                "Detalhes do erro",
                400,
                "INT-0001",
                "2025-01-01T10:00:00Z"
        );

        assertEquals("CODIGO_TESTE", erro.getCodigo());
        assertEquals("Mensagem de erro", erro.getMensagem());
        assertEquals("Detalhes do erro", erro.getDetalhes());
        assertEquals(400, erro.getStatus());
        assertEquals("INT-0001", erro.getCodigoInterno());
        assertEquals("2025-01-01T10:00:00Z", erro.getTimestamp());
    }
}