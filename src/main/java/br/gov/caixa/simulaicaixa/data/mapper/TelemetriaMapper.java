package br.gov.caixa.simulaicaixa.data.mapper;

import br.gov.caixa.simulaicaixa.data.entity.TelemetriaServicoEntity;
import br.gov.caixa.simulaicaixa.domain.Telemetria;
import br.gov.caixa.simulaicaixa.domain.TelemetriaPeriodo;
import br.gov.caixa.simulaicaixa.domain.TelemetriaServico;

import java.util.List;
import java.util.stream.Collectors;

public final class TelemetriaMapper {

    private TelemetriaMapper() {
    }

    public static TelemetriaServico mapearParaServicoDominio(TelemetriaServicoEntity entity) {
        TelemetriaServico servico = new TelemetriaServico();

        servico.setNome(entity.getNome());
        servico.setQuantidadeChamadas(entity.getQuantidadeChamadas());
        servico.setMediaTempoRespostaMs(entity.getMediaTempoRespostaMs());

        return servico;
    }

    public static Telemetria mapearParaTelemetriaDominio(List<TelemetriaServicoEntity> entidades) {
        Telemetria telemetria = new Telemetria();

        List<TelemetriaServico> servicos = entidades.stream()
                .map(TelemetriaMapper::mapearParaServicoDominio)
                .collect(Collectors.toList());

        telemetria.setServicos(servicos);

        if (!entidades.isEmpty()) {
            TelemetriaServicoEntity primeira = entidades.get(0);

            TelemetriaPeriodo periodo = new TelemetriaPeriodo();
            periodo.setInicio(primeira.getPeriodoInicio());
            periodo.setFim(primeira.getPeriodoFim());

            telemetria.setPeriodo(periodo);
        }

        return telemetria;
    }
}