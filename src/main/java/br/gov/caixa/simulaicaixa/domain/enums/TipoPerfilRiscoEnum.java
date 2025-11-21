package br.gov.caixa.simulaicaixa.domain.enums;

/**
 * Perfis de risco utilizados para classificar investidores.
 * <p>
 * O perfil orienta a adequação de produtos de investimento
 * às características e tolerância a risco do cliente.
 */
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

    /**
     * Retorna o código numérico do perfil de risco.
     *
     * @return código inteiro usado para persistência.
     */
    public Integer getCodigo() {
        return codigo;
    }

    /**
     * Retorna a descrição do perfil de risco.
     *
     * @return descrição amigável para exibição e respostas da API.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Obtém o perfil de risco a partir do código numérico.
     *
     * @param codigo código inteiro do perfil.
     * @return enum correspondente ou {@code null} se o código for nulo
     *         ou não corresponder a nenhum valor conhecido.
     */
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

    /**
     * Obtém o perfil de risco a partir do nome ou descrição.
     * <p>
     * Ignora diferenças de caixa e espaços em branco.
     *
     * @param descricao texto contendo o nome do enum ou a descrição.
     * @return enum correspondente ou {@code null} se não houver correspondência.
     */
    public static TipoPerfilRiscoEnum obterPorDescricao(String descricao) {
        if (descricao == null) {
            return null;
        }

        String normalizado = descricao.trim().toUpperCase();

        for (TipoPerfilRiscoEnum valor : values()) {
            String descEnum = valor.getDescricao().toUpperCase();
            String nomeEnum = valor.name().toUpperCase();

            if (descEnum.equals(normalizado) || nomeEnum.equals(normalizado)) {
                return valor;
            }
        }

        return null;
    }
}