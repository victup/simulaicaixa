package br.gov.caixa.simulaicaixa.domain.enums;

/**
 * Tipos de produtos de investimento suportados pelo sistema.
 * <p>
 * Cada tipo possui um código inteiro (usado na persistência) e uma
 * descrição utilizada em telas e respostas da API.
 */
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

    /**
     * Retorna o código numérico do tipo de investimento.
     *
     * @return código inteiro usado para persistência.
     */
    public Integer getCodigo() {
        return codigo;
    }

    /**
     * Retorna a descrição legível do tipo de investimento.
     *
     * @return descrição amigável para exibição e respostas da API.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Obtém o tipo de investimento a partir do código numérico.
     * <p>
     * Caso o código seja {@code null} ou não corresponda a nenhum valor
     * conhecido, retorna {@link #OUTRO}.
     *
     * @param codigo código inteiro do tipo de investimento.
     * @return enum correspondente ou {@link #OUTRO} se não houver correspondência.
     */
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

    /**
     * Obtém o tipo de investimento a partir do nome ou descrição.
     * <p>
     * Ignora diferenças de caixa e espaços em branco. Se não houver
     * correspondência, retorna {@link #OUTRO}.
     *
     * @param descricao texto contendo o nome do enum ou a descrição.
     * @return enum correspondente ou {@link #OUTRO} se não houver correspondência.
     */
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