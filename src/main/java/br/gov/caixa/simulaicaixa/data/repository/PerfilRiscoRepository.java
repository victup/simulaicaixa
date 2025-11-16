package br.gov.caixa.simulaicaixa.data.repository;

import br.gov.caixa.simulaicaixa.domain.PerfilRisco;

public interface PerfilRiscoRepository {

    PerfilRisco obterPorClienteId(Long clienteId);
}