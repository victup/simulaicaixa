package br.gov.caixa.simulaicaixa.dto;

import java.util.List;

/**
 * Visão consolidada da telemetria da aplicação.
 *
 * @param servicos lista de métricas por serviço monitorado
 * @param periodo  período de tempo coberto pelas métricas
 */
public record TelemetriaDto(
        List<TelemetriaServicoDto> servicos,
        TelemetriaPeriodoDto periodo
) {
}