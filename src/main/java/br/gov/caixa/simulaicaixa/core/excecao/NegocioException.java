package br.gov.caixa.simulaicaixa.core.excecao;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;

public class NegocioException extends RuntimeException {

    private final CodigoErroNegocio codigoErro;
    private final String detalhes;

    public NegocioException(CodigoErroNegocio codigoErro) {
        this(codigoErro, codigoErro.getMensagemPadrao(), null);
    }

    public NegocioException(CodigoErroNegocio codigoErro, String detalhes) {
        this(codigoErro, codigoErro.getMensagemPadrao(), detalhes);
    }

    public NegocioException(CodigoErroNegocio codigoErro, String mensagem, String detalhes) {
        super(mensagem);
        this.codigoErro = codigoErro;
        this.detalhes = detalhes;
    }

    public CodigoErroNegocio getCodigoErro() {
        return codigoErro;
    }

    public String getDetalhes() {
        return detalhes;
    }
}