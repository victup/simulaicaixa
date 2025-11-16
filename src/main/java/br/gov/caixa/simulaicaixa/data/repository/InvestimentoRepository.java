package br.gov.caixa.simulaicaixa.data.repository;

import br.gov.caixa.simulaicaixa.domain.Investimento;

import java.util.List;

public interface InvestimentoRepository {

    List<Investimento> listarPorClienteId(Long clienteId);
}
