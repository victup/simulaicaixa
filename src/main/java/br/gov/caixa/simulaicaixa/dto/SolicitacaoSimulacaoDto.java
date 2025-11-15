package br.gov.caixa.simulaicaixa.dto;

import java.math.BigDecimal;

public record SolicitacaoSimulacaoDto(
        Long clienteId,
        BigDecimal valor,
        Integer prazoMeses,
        String tipoProduto
) {
}
