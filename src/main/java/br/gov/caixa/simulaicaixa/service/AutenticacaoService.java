package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.dto.RequisicaoLoginDto;
import br.gov.caixa.simulaicaixa.dto.RespostaLoginDto;

public interface AutenticacaoService {

    RespostaLoginDto autenticar(RequisicaoLoginDto requisicao);
}