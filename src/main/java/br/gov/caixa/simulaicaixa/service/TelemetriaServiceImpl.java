package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.data.repository.TelemetriaRepository;
import br.gov.caixa.simulaicaixa.domain.Telemetria;
import br.gov.caixa.simulaicaixa.domain.TelemetriaPeriodo;
import br.gov.caixa.simulaicaixa.domain.TelemetriaServico;
import br.gov.caixa.simulaicaixa.dto.TelemetriaDto;
import br.gov.caixa.simulaicaixa.dto.TelemetriaPeriodoDto;
import br.gov.caixa.simulaicaixa.dto.TelemetriaServicoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TelemetriaServiceImpl implements TelemetriaService {

    private final TelemetriaRepository telemetriaRepository;

    @Inject
    public TelemetriaServiceImpl(TelemetriaRepository telemetriaRepository) {
        this.telemetriaRepository = telemetriaRepository;
    }

    @Override
    public TelemetriaDto obterTelemetriaGeral() {
        Telemetria telemetria = telemetriaRepository.obterTelemetriaGeral();

        List<TelemetriaServicoDto> servicosDto = telemetria.getServicos().stream()
                .map(this::converterParaDto)
                .collect(Collectors.toList());

        TelemetriaPeriodo periodo = telemetria.getPeriodo();

        TelemetriaPeriodoDto periodoDto = new TelemetriaPeriodoDto(
                periodo.getInicio(),
                periodo.getFim()
        );

        return new TelemetriaDto(servicosDto, periodoDto);
    }

    private TelemetriaServicoDto converterParaDto(TelemetriaServico servico) {
        return new TelemetriaServicoDto(
                servico.getNome(),
                servico.getQuantidadeChamadas(),
                servico.getMediaTempoRespostaMs()
        );
    }
}