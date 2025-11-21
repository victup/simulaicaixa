package br.gov.caixa.simulaicaixa.dto;

import java.time.LocalDate;

/**
 * Período de referência utilizado nos relatórios de telemetria.
 *
 * @param inicio data inicial do período (inclusive)
 * @param fim data final do período (inclusive)
 */
public record TelemetriaPeriodoDto(
        LocalDate inicio,
        LocalDate fim
) {
}