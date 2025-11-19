package br.gov.caixa.simulaicaixa.domain.enums;

public enum TipoPerfilRiscoEnum {

    CONSERVADOR(1, "Conservador"),
    MODERADO(2, "Moderado"),
    AGRESSIVO(3, "Agressivo");

    private final Integer codigo;
    private final String descricao;

    TipoPerfilRiscoEnum(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoPerfilRiscoEnum obterPorCodigo(Integer codigo) {
        if (codigo == null) {
            return null;
        }

        for (TipoPerfilRiscoEnum valor : values()) {
            if (valor.getCodigo().equals(codigo)) {
                return valor;
            }
        }

        return null;
    }

    public static TipoPerfilRiscoEnum obterPorDescricao(String descricao) {
        if (descricao == null) {
            return null;
        }

        String normalizado = descricao.trim().toUpperCase();

        for (TipoPerfilRiscoEnum valor : values()) {
            if (valor.getDescricao().toUpperCase().equals(normalizado)
                    || valor.name().equals(normalizado)) {
                return valor;
            }
        }

        return null;
    }
}