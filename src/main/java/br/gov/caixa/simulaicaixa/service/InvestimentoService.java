package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.dto.InvestimentoHistoricoDto;

import java.util.List;

public interface InvestimentoService {

    List<InvestimentoHistoricoDto> listarPorClienteId(Long clienteId);
}
