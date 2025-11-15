package br.gov.caixa.simulaicaixa.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record SimulacaoHistoricoDto(
        Long id,
        Long clienteId,
        String produto,
        BigDecimal valorInvestido,
        BigDecimal valorFinal,
        Integer prazoMeses,
        OffsetDateTime dataSimulacao
) {
}