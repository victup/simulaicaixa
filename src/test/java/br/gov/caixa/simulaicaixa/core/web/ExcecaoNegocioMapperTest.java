package br.gov.caixa.simulaicaixa.core.web;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.erro.ErroResposta;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExcecaoNegocioMapperTest {

    private final ExcecaoNegocioMapper mapper = new ExcecaoNegocioMapper();

    @Test
    void deveMapearNegocioExceptionParaErroRespostaComDadosDoCodigoErro() {
        CodigoErroNegocio codigo = CodigoErroNegocio.CLIENTE_SEM_PERFIL_RISCO;
        String mensagemCustomizada = "Mensagem customizada para o cliente";
        String detalhes = "Detalhes específicos do erro";

        NegocioException exception = new NegocioException(
                codigo,
                mensagemCustomizada,
                detalhes
        );

        Response response = mapper.toResponse(exception);

        assertEquals(codigo.getStatusHttp(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());

        Object entity = response.getEntity();
        assertNotNull(entity);
        assertInstanceOf(ErroResposta.class, entity);

        ErroResposta erro = (ErroResposta) entity;

        assertEquals(codigo.name(), erro.getCodigo());
        assertEquals(mensagemCustomizada, erro.getMensagem());
        assertEquals(detalhes, erro.getDetalhes());
        assertEquals(codigo.getStatusHttp(), erro.getStatus());
        assertEquals(codigo.getCodigoInterno(), erro.getCodigoInterno());
        assertNotNull(erro.getTimestamp());
        assertFalse(erro.getTimestamp().isBlank());
    }
}