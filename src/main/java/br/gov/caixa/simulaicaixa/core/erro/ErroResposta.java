package br.gov.caixa.simulaicaixa.core.erro;

public class ErroResposta {

    private String codigo;
    private String mensagem;
    private String detalhes;
    private int status;
    private String codigoInterno;
    private String timestamp;

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

    public String getCodigo() {
        return codigo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public int getStatus() {
        return status;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public String getTimestamp() {
        return timestamp;
    }
}