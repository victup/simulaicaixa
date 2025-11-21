package br.gov.caixa.simulaicaixa.core.seguranca;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import br.gov.caixa.simulaicaixa.service.ContextoClienteService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
/**
 * Componente responsável por validar se o usuário autenticado
 * pode acessar/consultar dados de um determinado cliente.
 * <p>
 * Regra geral:
 * <ul>
 *     <li>Se o usuário tiver o papel {@code admin}, o acesso é permitido
 *         para qualquer {@code clienteId}.</li>
 *     <li>Se o usuário for um cliente, só é permitido acesso ao próprio
 *         {@code clienteId} vinculado ao token.</li>
 * </ul>
 * <p>
 * Em caso de violação de regra de acesso, é lançada uma
 * {@link br.gov.caixa.simulaicaixa.core.excecao.NegocioException}
 * com o {@link br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio}
 * informado pelo chamador.
 */
public class ValidadorAcessoCliente {

    private final ContextoClienteService contextoClienteService;
    private final JsonWebToken jwt;

    @Inject
    public ValidadorAcessoCliente(ContextoClienteService contextoClienteService,
                                  JsonWebToken jwt) {
        this.contextoClienteService = contextoClienteService;
        this.jwt = jwt;
    }

    /**
     * Valida se o usuário autenticado possui permissão para acessar os dados
     * do cliente identificado por {@code clienteId}.
     * <p>
     * Regras:
     * <ul>
     *     <li>Se o usuário atual for {@code admin}, o acesso é sempre permitido.</li>
     *     <li>Se o usuário não for {@code admin}, apenas o próprio cliente
     *         vinculado ao token pode ser acessado.</li>
     * </ul>
     *
     * @param clienteIdPath        identificador do cliente alvo da operação.
     * @param codigoErroNegocio código de negócio a ser utilizado caso o acesso
     *                          não seja permitido (por exemplo,
     *                          {@link CodigoErroNegocio#OPERACAO_NAO_PERMITIDA_PARA_INVESTIMENTOS}).
     * @throws br.gov.caixa.simulaicaixa.core.excecao.NegocioException
     *         se o usuário não tiver permissão para acessar o cliente informado.
     */

    public void validarClienteOuAdmin(Long clienteIdPath, CodigoErroNegocio codigoErroNegocio) {
        boolean ehAdmin = jwt.getGroups() != null && jwt.getGroups().contains("admin");

        if (ehAdmin) {
            return;
        }

        Long clienteIdToken = contextoClienteService.obterClienteIdUsuarioObrigatorio();

        if (!clienteIdToken.equals(clienteIdPath)) {
            throw new NegocioException(
                    codigoErroNegocio,
                    "Usuário autenticado não tem permissão para consultar dados do cliente " + clienteIdPath + "."
            );
        }
    }
}