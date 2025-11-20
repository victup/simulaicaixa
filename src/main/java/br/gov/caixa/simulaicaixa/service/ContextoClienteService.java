package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.data.entity.UsuarioAutenticacaoEntity;
import br.gov.caixa.simulaicaixa.data.repository.UsuarioAutenticacaoRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

@RequestScoped
public class ContextoClienteService {

    @Inject
    JsonWebToken jwt;

    @Inject
    UsuarioAutenticacaoRepository usuarioAutenticacaoRepository;

    public Long obterClienteId() {
        String cpf = obterCpfDoToken();
        if (cpf == null || cpf.isBlank()) {
            return null;
        }

        UsuarioAutenticacaoEntity usuario = usuarioAutenticacaoRepository.buscarPorCpf(cpf);
        if (usuario == null) {
            return null;
        }

        return usuario.getClienteId();
    }

    public Long obterClienteIdUsuarioObrigatorio() {
        Long clienteId = obterClienteId();
        if (clienteId == null) {
            throw new IllegalStateException("Usuário autenticado não é um cliente no sistema.");
        }
        return clienteId;
    }

    private String obterCpfDoToken() {
        String cpf = jwt.getClaim("cpf");
        if (cpf == null || cpf.isBlank()) {
            cpf = jwt.getSubject();
        }
        return cpf;
    }
}