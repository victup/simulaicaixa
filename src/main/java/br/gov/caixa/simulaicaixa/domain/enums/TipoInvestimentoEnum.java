package br.gov.caixa.simulaicaixa.domain.enums;

public enum TipoInvestimentoEnum {

    CDB(1, "CDB"),
    FUNDO(2, "Fundo"),
    LCI(3, "LCI"),
    LCA(4, "LCA"),
    TESOURO(5, "Tesouro Direto"),
    OUTRO(99, "Outro");

    private final Integer codigo;
    private final String descricao;

    TipoInvestimentoEnum(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoInvestimentoEnum obterPorCodigo(Integer codigo) {
        if (codigo == null) {
            return OUTRO;
        }

        for (TipoInvestimentoEnum valor : values()) {
            if (valor.getCodigo().equals(codigo)) {
                return valor;
            }
        }

        return OUTRO;
    }

    public static TipoInvestimentoEnum obterPorDescricao(String descricao) {
        if (descricao == null) {
            return OUTRO;
        }

        String normalizado = descricao.trim().toUpperCase();

        for (TipoInvestimentoEnum valor : values()) {
            String descEnum = valor.getDescricao().toUpperCase();
            String nomeEnum = valor.name().toUpperCase();

            if (descEnum.equals(normalizado) || nomeEnum.equals(normalizado)) {
                return valor;
            }
        }

        return OUTRO;
    }
}