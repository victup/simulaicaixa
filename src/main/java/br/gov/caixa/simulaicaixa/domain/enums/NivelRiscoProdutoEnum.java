package br.gov.caixa.simulaicaixa.domain.enums;

public enum NivelRiscoProdutoEnum {

    BAIXO(1, "Baixo"),
    MEDIO(2, "Médio"),
    ALTO(3, "Alto");

    private final Integer codigo;
    private final String descricao;

    NivelRiscoProdutoEnum(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static NivelRiscoProdutoEnum obterPorCodigo(Integer codigo) {
        if (codigo == null) {
            return null;
        }

        for (NivelRiscoProdutoEnum valor : values()) {
            if (valor.getCodigo().equals(codigo)) {
                return valor;
            }
        }
        return null;
    }

    public static NivelRiscoProdutoEnum obterPorDescricao(String descricao) {
        if (descricao == null) {
            return null;
        }

        String normalizado = descricao.trim().toUpperCase();

        for (NivelRiscoProdutoEnum valor : values()) {
            String descEnum = valor.getDescricao().toUpperCase();
            String nomeEnum = valor.name().toUpperCase();

            if (descEnum.equals(normalizado) || nomeEnum.equals(normalizado)) {
                return valor;
            }
        }
        return null;
    }
}