package br.gov.caixa.simulaicaixa.domain.enums;

/**
 * Nível de risco associado a um produto de investimento.
 * <p>
 * Usado para classificar a volatilidade/risco de perda do produto e
 * apoiar regras de recomendação e exibição na API.
 */
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

    /**
     * Retorna o código numérico utilizado para persistência
     * e integração com o banco de dados.
     *
     * @return código inteiro do nível de risco.
     */
    public Integer getCodigo() {
        return codigo;
    }

    /**
     * Retorna a descrição legível do nível de risco.
     *
     * @return descrição amigável para uso em respostas da API.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Obtém o nível de risco a partir do código numérico.
     *
     * @param codigo código inteiro do nível de risco.
     * @return enum correspondente ou {@code null} se o código for nulo
     *         ou não corresponder a nenhum valor conhecido.
     */
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

    /**
     * Obtém o nível de risco a partir do nome ou descrição.
     * <p>
     * Ignora diferenças de caixa e espaços em branco.
     *
     * @param descricao texto contendo o nome do enum ou a descrição.
     * @return enum correspondente ou {@code null} se não houver correspondência.
     */
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