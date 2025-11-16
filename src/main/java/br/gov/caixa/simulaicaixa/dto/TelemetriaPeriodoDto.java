package br.gov.caixa.simulaicaixa.dto;

import java.time.LocalDate;

public record TelemetriaPeriodoDto(
        LocalDate inicio,
        LocalDate fim
) {
}
