package br.gov.caixa.simulaicaixa.core.erro;

public enum CodigoErroNegocio {

    CLIENTE_NAO_ENCONTRADO("CLI-0001", "Cliente não encontrado.", 404),
    CLIENTE_SEM_HISTORICO_INVESTIMENTOS("INV-0001", "Cliente não possui histórico de investimentos suficiente para recomendação.", 404),
    OPERACAO_NAO_PERMITIDA_PARA_INVESTIMENTOS("INV-0002", "Operação não permitida para o investimetnos do cliente.", 422),
    OPERACAO_NAO_PERMITIDA_PARA_PERFIL("PRF-0003", "Operação não permitida para o perfil de risco do cliente.", 422);

    private final String codigoInterno;
    private final String mensagemPadrao;
    private final int statusHttp;

    CodigoErroNegocio(String codigoInterno, String mensagemPadrao, int statusHttp) {
        this.codigoInterno = codigoInterno;
        this.mensagemPadrao = mensagemPadrao;
        this.statusHttp = statusHttp;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public String getMensagemPadrao() {
        return mensagemPadrao;
    }

    public int getStatusHttp() {
        return statusHttp;
    }
}