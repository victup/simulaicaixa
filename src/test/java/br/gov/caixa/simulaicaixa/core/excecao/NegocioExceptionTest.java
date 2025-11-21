package br.gov.caixa.simulaicaixa.core.excecao;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NegocioExceptionTest {

    @Test
    void construtorComApenasCodigoDeveUsarMensagemPadraoESemDetalhes() {
        CodigoErroNegocio codigo = CodigoErroNegocio.CREDENCIAIS_INVALIDAS;

        NegocioException exception = new NegocioException(codigo);

        assertEquals(codigo, exception.getCodigoErro());
        assertEquals(codigo.getMensagemPadrao(), exception.getMessage());
        assertNull(exception.getDetalhes());
    }

    @Test
    void construtorComCodigoEDetalhesDeveUsarMensagemPadrao() {
        CodigoErroNegocio codigo = CodigoErroNegocio.CLIENTE_NAO_ENCONTRADO;
        String detalhes = "Cliente 123 não encontrado na base.";

        NegocioException exception = new NegocioException(codigo, detalhes);

        assertEquals(codigo, exception.getCodigoErro());
        assertEquals(codigo.getMensagemPadrao(), exception.getMessage());
        assertEquals(detalhes, exception.getDetalhes());
    }

    @Test
    void construtorCompletoDeveUsarMensagemEDetalhesInformados() {
        CodigoErroNegocio codigo = CodigoErroNegocio.SIMULACAO_DADOS_INVALIDOS;
        String mensagem = "Dados da simulação estão inconsistentes.";
        String detalhes = "Valor negativo informado.";

        NegocioException exception = new NegocioException(codigo, mensagem, detalhes);

        assertEquals(codigo, exception.getCodigoErro());
        assertEquals(mensagem, exception.getMessage());
        assertEquals(detalhes, exception.getDetalhes());
    }
}