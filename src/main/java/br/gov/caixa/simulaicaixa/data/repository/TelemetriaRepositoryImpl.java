package br.gov.caixa.simulaicaixa.data.repository;

import br.gov.caixa.simulaicaixa.domain.Telemetria;
import br.gov.caixa.simulaicaixa.domain.TelemetriaPeriodo;
import br.gov.caixa.simulaicaixa.domain.TelemetriaServico;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class TelemetriaRepositoryImpl implements TelemetriaRepository {

    @Override
    public Telemetria obterTelemetriaGeral() {
        TelemetriaServico simulacaoServico = new TelemetriaServico();
        simulacaoServico.setNome("simular-investimento");
        simulacaoServico.setQuantidadeChamadas(120L);
        simulacaoServico.setMediaTempoRespostaMs(250L);

        TelemetriaServico perfilRiscoServico = new TelemetriaServico();
        perfilRiscoServico.setNome("perfil-risco");
        perfilRiscoServico.setQuantidadeChamadas(80L);
        perfilRiscoServico.setMediaTempoRespostaMs(180L);

        TelemetriaPeriodo periodo = new TelemetriaPeriodo();
        periodo.setInicio(LocalDate.of(2025, 10, 1));
        periodo.setFim(LocalDate.of(2025, 10, 31));

        Telemetria telemetria = new Telemetria();
        telemetria.setServicos(List.of(simulacaoServico, perfilRiscoServico));
        telemetria.setPeriodo(periodo);

        return telemetria;
    }
}
