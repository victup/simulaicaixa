package br.gov.caixa.simulaicaixa.data.repository;

import br.gov.caixa.simulaicaixa.data.entity.UsuarioAutenticacaoEntity;

public interface UsuarioAutenticacaoRepository {

    UsuarioAutenticacaoEntity buscarPorCpf(String cpf);
    UsuarioAutenticacaoEntity salvar(UsuarioAutenticacaoEntity usuario);
}