package br.gov.caixa.simulaicaixa.core.erro;

/**
 * Representa o corpo padronizado de respostas de erro retornadas pela API.
 * <p>
 * Esse modelo é utilizado pelos mapeadores de exceção para fornecer
 * informações consistentes sobre falhas de negócio ou erros internos.
 */
public class ErroResposta {

    private String codigo;
    private String mensagem;
    private String detalhes;
    private int status;
    private String codigoInterno;
    private String timestamp;

    /**
     * Cria uma nova resposta de erro padronizada.
     *
     * @param codigo        código simbólico do erro (por exemplo, nome do {@link CodigoErroNegocio} ou "ERRO_INTERNO")
     * @param mensagem      mensagem principal a ser exibida ao consumidor
     * @param detalhes      detalhes adicionais sobre o contexto do erro (pode ser {@code null})
     * @param status        status HTTP retornado na resposta
     * @param codigoInterno código interno de rastreio do erro (por exemplo, {@code CLI-0001}, {@code GEN-0001})
     * @param timestamp     data e hora em que o erro foi registrado, em formato textual
     */
    public ErroResposta(String codigo,
                        String mensagem,
                        String detalhes,
                        int status,
                        String codigoInterno,
                        String timestamp) {
        this.codigo = codigo;
        this.mensagem = mensagem;
        this.detalhes = detalhes;
        this.status = status;
        this.codigoInterno = codigoInterno;
        this.timestamp = timestamp;
    }

    /**
     * Código simbólico do erro (nome lógico, por exemplo {@code CLIENTE_NAO_ENCONTRADO} ou {@code ERRO_INTERNO}).
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Mensagem principal do erro, voltada ao consumidor da API.
     */
    public String getMensagem() {
        return mensagem;
    }

    /**
     * Detalhes adicionais sobre o contexto do erro, quando disponíveis.
     */
    public String getDetalhes() {
        return detalhes;
    }

    /**
     * Status HTTP retornado na respotsa.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Código interno utilizado para rastrear e categorizar o erro
     * (normalmente o código definido em {@link CodigoErroNegocio} ou um código genérico).
     */
    public String getCodigoInterno() {
        return codigoInterno;
    }

    /**
     * Momento em que o erro foi registrado, em formato textual.
     */
    public String getTimestamp() {
        return timestamp;
    }
}