package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.dto.TelemetriaDto;

import java.time.LocalDate;

public interface TelemetriaService {

    TelemetriaDto obterTelemetriaGeral();

    TelemetriaDto obterResumoPorPeriodo(LocalDate inicio, LocalDate fim);
}
