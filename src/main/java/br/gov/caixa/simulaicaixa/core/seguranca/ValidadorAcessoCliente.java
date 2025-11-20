package br.gov.caixa.simulaicaixa.core.seguranca;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import br.gov.caixa.simulaicaixa.service.ContextoClienteService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class ValidadorAcessoCliente {

    private final ContextoClienteService contextoClienteService;
    private final JsonWebToken jwt;

    @Inject
    public ValidadorAcessoCliente(ContextoClienteService contextoClienteService,
                                  JsonWebToken jwt) {
        this.contextoClienteService = contextoClienteService;
        this.jwt = jwt;
    }

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