package br.gov.caixa.simulaicaixa.core.excecao;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;

/**
 * Exceção de domínio utilizada para representar erros de negócio conhecidos.
 * <p>
 * Cada instância está associada a um {@link CodigoErroNegocio}, que define:
 * o código interno, a mensagem padrão e o status HTTP sugerido.
 * <p>
 * É tratada pelos mapeadores REST da camada web para produzir respostas
 * padronizadas aos consumidores da API.
 */
public class NegocioException extends RuntimeException {

    private final CodigoErroNegocio codigoErro;
    private final String detalhes;

    /**
     * Cria uma exceção de negócio utilizando a mensagem padrão do código informado.
     *
     * @param codigoErro código de erro de negócio associado à exceção
     */
    public NegocioException(CodigoErroNegocio codigoErro) {
        this(codigoErro, codigoErro.getMensagemPadrao(), null);
    }

    /**
     * Cria uma exceção de negócio utilizando a mensagem padrão do código informado
     * e permitindo informar detalhes adicionais.
     *
     * @param codigoErro código de erro de negócio associado à exceção
     * @param detalhes   detalhes adicionais sobre o contexto do erro
     */
    public NegocioException(CodigoErroNegocio codigoErro, String detalhes) {
        this(codigoErro, codigoErro.getMensagemPadrao(), detalhes);
    }


    /**
     * Cria uma exceção de negócio com mensagem customizada e detalhes adicionais.
     *
     * @param codigoErro código de erro de negócio associado à exceção
     * @param mensagem   mensagem principal da exceção
     * @param detalhes   detalhes adicionais sobre o contexto do erro
     */
    public NegocioException(CodigoErroNegocio codigoErro, String mensagem, String detalhes) {
        super(mensagem);
        this.codigoErro = codigoErro;
        this.detalhes = detalhes;
    }

    /**
     * Retorna o código de erro de negócio associado a esta exceção.
     *
     * @return código de erro de negócio
     */
    public CodigoErroNegocio getCodigoErro() {
        return codigoErro;
    }

    /**
     * Retorna detalhes adicionais sobre o contexto do erro, quando informados.
     *
     * @return detalhes adicionais ou {@code null} se não houver
     */
    public String getDetalhes() {
        return detalhes;
    }
}