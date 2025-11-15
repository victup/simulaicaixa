package br.gov.caixa.simulaicaixa.dto;

import java.math.BigDecimal;

public record ResultadoSimulacaoDto(
        BigDecimal valorFinal,
        BigDecimal rentabilidadeEfetiva,
        Integer prazoMeses
) {
}