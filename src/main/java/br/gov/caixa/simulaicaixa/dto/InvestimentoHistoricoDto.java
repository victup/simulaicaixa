package br.gov.caixa.simulaicaixa.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestimentoHistoricoDto(
        Long id,
        String tipo,
        BigDecimal valor,
        BigDecimal rentabilidade,
        LocalDate data
) {
}
