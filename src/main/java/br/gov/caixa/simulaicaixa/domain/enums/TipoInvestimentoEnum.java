package br.gov.caixa.simulaicaixa.domain.enums;

public enum TipoInvestimentoEnum {

    CDB(1, "CDB"),
    FUNDO(2, "Fundo"),
    FUNDO_MULTIMERCADO(3, "Fundo Multimercado");

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
            return null;
        }

        for (TipoInvestimentoEnum valor : values()) {
            if (valor.getCodigo().equals(codigo)) {
                return valor;
            }
        }

        return null;
    }

    public static TipoInvestimentoEnum obterPorDescricao(String descricao) {
        if (descricao == null) {
            return null;
        }

        String normalizado = descricao.trim().toUpperCase();

        for (TipoInvestimentoEnum valor : values()) {
            if (valor.getDescricao().toUpperCase().equals(normalizado)
                    || valor.name().equals(normalizado)) {
                return valor;
            }
        }

        return null;
    }
}