package br.gov.caixa.simulaicaixa.dto;

import java.util.List;

public record TelemetriaDto(
        List<TelemetriaServicoDto> servicos,
        TelemetriaPeriodoDto periodo
) {
}