package br.gov.caixa.simulaicaixa.core.web;

import br.gov.caixa.simulaicaixa.core.erro.ErroResposta;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExcecaoGeralMapperTest {

    private final ExcecaoGeralMapper mapper = new ExcecaoGeralMapper();

    @Test
    void deveMapearThrowableParaErroRespostaComStatus500() {
        RuntimeException excecao = new RuntimeException("Falha inesperada");

        Response response = mapper.toResponse(excecao);

        assertEquals(500, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());

        Object entity = response.getEntity();
        assertNotNull(entity);
        assertInstanceOf(ErroResposta.class, entity);

        ErroResposta erro = (ErroResposta) entity;

        assertEquals("ERRO_INTERNO", erro.getCodigo());
        assertEquals("Erro inesperado ao processar a requisição.", erro.getMensagem());
        assertEquals("Falha inesperada", erro.getDetalhes());
        assertEquals(500, erro.getStatus());
        assertEquals("GEN-0001", erro.getCodigoInterno());
        assertNotNull(erro.getTimestamp());
        assertFalse(erro.getTimestamp().isBlank());
    }
}