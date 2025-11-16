package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.dto.PerfilRiscoDto;

public interface PerfilRiscoService {

    PerfilRiscoDto obterPorClienteId(Long clienteId);
}
