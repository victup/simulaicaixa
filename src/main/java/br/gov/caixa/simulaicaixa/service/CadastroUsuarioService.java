package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.dto.RequisicaoCadastroUsuarioDto;
import br.gov.caixa.simulaicaixa.dto.RespostaCadastroUsuarioDto;

public interface CadastroUsuarioService {
    RespostaCadastroUsuarioDto cadastrarUsuario(RequisicaoCadastroUsuarioDto requisicao);
}