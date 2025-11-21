package br.gov.caixa.simulaicaixa.core.erro;

/**
 * Enum que centraliza os códigos de erro de negócio da aplicação.
 * <p>
 * Cada constante define:
 * <ul>
 *   <li>Um código interno (por exemplo, {@code CLI-0001}),</li>
 *   <li>Uma mensagem padrão amigável,</li>
 *   <li>O status HTTP a ser utilizado na resposta.</li>
 * </ul>
 * <p>
 * Esses valores são utilizados principalmente por {@link br.gov.caixa.simulaicaixa.core.excecao.NegocioException}
 * e pelos mapeadores de exceção REST para compor o corpo padronizado de erro.
 */
public enum CodigoErroNegocio {

    CLIENTE_NAO_ENCONTRADO("CLI-0001", "Cliente não encontrado.", 404),
    CLIENTE_SEM_HISTORICO_INVESTIMENTOS("INV-0001", "Cliente não possui histórico de investimentos suficiente para recomendação.", 404),
    OPERACAO_NAO_PERMITIDA_PARA_INVESTIMENTOS("INV-0002", "Operação não permitida para o investimetnos do cliente.", 422),
    OPERACAO_NAO_PERMITIDA_PARA_PERFIL("PRF-0003", "Operação não permitida para o perfil de risco do cliente.", 422),
    PERFIL_INVALIDO("PRF-0001", "Perfil informado é inválido.", 400),
    CLIENTE_SEM_PERFIL_RISCO("PRF-0002", "O cliente não possui perfil de risco cadastrado.", 404),
    PRODUTOS_NAO_ENCONTRADOS_PARA_PERFIL("PRD-0001", "Não há produtos compatíveis com o perfil de risco informado.", 404),
    CREDENCIAIS_INVALIDAS("AUTH-0001", "CPF ou senha inválidos.", 401),
    USUARIO_SEM_GRUPO_ACESSO("AUTH-0002", "Usuário sem grupo de acesso válido.", 403),
    SIMULACAO_DADOS_INVALIDOS("SIM-0001", "Parâmetros da simulação são inválidos.", 400),
    TELEMETRIA_PERIODO_INVALIDO("TEL-0001","Período de telemetria informado é inválido.",400);

    private final String codigoInterno;
    private final String mensagemPadrao;
    private final int statusHttp;

    CodigoErroNegocio(String codigoInterno, String mensagemPadrao, int statusHttp) {
        this.codigoInterno = codigoInterno;
        this.mensagemPadrao = mensagemPadrao;
        this.statusHttp = statusHttp;
    }

    /**
     * Retorna o código interno que identifica o erro de negócio
     * (por exemplo, {@code CLI-0001}, {@code AUTH-0001}).
     */
    public String getCodigoInterno() {
        return codigoInterno;
    }

    /**
     * Retorna a mensagem padrão associada ao erro de negócio.
     * <p>
     * Essa mensagem é normalmente utilizada como mensagem principal
     * em {@link br.gov.caixa.simulaicaixa.core.excecao.NegocioException}.
     */
    public String getMensagemPadrao() {
        return mensagemPadrao;
    }

    /**
     * Retorna o status HTTP sugerido para ser utilizado na resposta
     * quando este erro de negócio ocorre.
     */
    public int getStatusHttp() {
        return statusHttp;
    }
}