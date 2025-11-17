package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.data.entity.TelemetriaRegistroEntity;
import br.gov.caixa.simulaicaixa.data.repository.TelemetriaRegistroRepository;
import br.gov.caixa.simulaicaixa.domain.Telemetria;
import br.gov.caixa.simulaicaixa.domain.TelemetriaPeriodo;
import br.gov.caixa.simulaicaixa.domain.TelemetriaServico;
import br.gov.caixa.simulaicaixa.dto.TelemetriaDto;
import br.gov.caixa.simulaicaixa.dto.TelemetriaPeriodoDto;
import br.gov.caixa.simulaicaixa.dto.TelemetriaServicoDto;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class TelemetriaServiceImpl implements TelemetriaService {

    private final ColetorTelemetria coletorTelemetria;
    private final TelemetriaRegistroRepository telemetriaRegistroRepository;

    @Inject
    public TelemetriaServiceImpl(ColetorTelemetria coletorTelemetria,
                                 TelemetriaRegistroRepository telemetriaRegistroRepository) {
        this.coletorTelemetria = coletorTelemetria;
        this.telemetriaRegistroRepository = telemetriaRegistroRepository;
    }

    @Override
    public TelemetriaDto obterTelemetriaGeral() {
        Telemetria telemetria = coletorTelemetria.obterTelemetriaGeral();

        List<TelemetriaServicoDto> servicosDto = telemetria.getServicos().stream()
                .map(this::converterParaServicoDto)
                .collect(Collectors.toList());

        TelemetriaPeriodoDto periodoDto = null;

        TelemetriaPeriodo periodo = telemetria.getPeriodo();
        if (periodo != null) {
            periodoDto = new TelemetriaPeriodoDto(periodo.getInicio(), periodo.getFim());
        }

        return new TelemetriaDto(servicosDto, periodoDto);
    }

    @Override
    public TelemetriaDto obterResumoPorPeriodo(LocalDate inicio, LocalDate fim) {
        LocalDate inicioEfetivo = inicio != null ? inicio : LocalDate.now().minusDays(30);
        LocalDate fimEfetivo = fim != null ? fim : LocalDate.now();

        OffsetDateTime inicioDataHora = inicioEfetivo.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime fimDataHora = fimEfetivo.plusDays(1).atStartOfDay().minusNanos(1).atOffset(ZoneOffset.UTC);

        List<TelemetriaRegistroEntity> registros =
                telemetriaRegistroRepository.listarPorPeriodo(inicioDataHora, fimDataHora);

        Map<String, List<TelemetriaRegistroEntity>> agrupadoPorServico =
                registros.stream()
                        .collect(Collectors.groupingBy(TelemetriaRegistroEntity::getNomeServico));

        List<TelemetriaServicoDto> servicos = agrupadoPorServico.entrySet()
                .stream()
                .map(entry -> {
                    String nomeServico = entry.getKey();
                    List<TelemetriaRegistroEntity> lista = entry.getValue();

                    long quantidade = lista.size();

                    long soma = lista.stream()
                            .mapToLong(TelemetriaRegistroEntity::getTempoRespostaMs)
                            .sum();

                    long media = quantidade == 0
                            ? 0L
                            : BigDecimal.valueOf(soma)
                            .divide(BigDecimal.valueOf(quantidade), 0, RoundingMode.HALF_UP)
                            .longValue();

                    return new TelemetriaServicoDto(
                            nomeServico,
                            quantidade,
                            media
                    );
                })
                .sorted((a, b) -> a.nome().compareToIgnoreCase(b.nome()))
                .toList();

        TelemetriaPeriodoDto periodo = new TelemetriaPeriodoDto(inicioEfetivo, fimEfetivo);

        return new TelemetriaDto(servicos, periodo);
    }

    private TelemetriaServicoDto converterParaServicoDto(TelemetriaServico servico) {
        return new TelemetriaServicoDto(
                servico.getNome(),
                servico.getQuantidadeChamadas(),
                servico.getMediaTempoRespostaMs()
        );
    }
}