package br.gov.caixa.simulaicaixa.telemetria;

import br.gov.caixa.simulaicaixa.data.entity.TelemetriaRegistroEntity;
import br.gov.caixa.simulaicaixa.domain.Telemetria;
import br.gov.caixa.simulaicaixa.domain.TelemetriaPeriodo;
import br.gov.caixa.simulaicaixa.domain.TelemetriaServico;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ColetorTelemetriaEmMemoria implements ColetorTelemetria {

    private final Map<String, EstatisticaServico> estatisticasPorServico = new ConcurrentHashMap<>();

    private final EntityManager entityManager;

    @Inject
    public ColetorTelemetriaEmMemoria(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void registrarChamada(String nomeServico, long tempoRespostaMs) {
        LocalDateTime agora = LocalDateTime.now();

        estatisticasPorServico.compute(nomeServico, (nome, atual) -> {
            if (atual == null) {
                atual = new EstatisticaServico();
            }

            atual.registrarChamada(tempoRespostaMs, agora);
            return atual;
        });

        TelemetriaRegistroEntity registro = new TelemetriaRegistroEntity();
        registro.setNomeServico(nomeServico);
        registro.setTempoRespostaMs(tempoRespostaMs);
        registro.setDataHoraChamada(agora);

        entityManager.persist(registro);
    }

    @Override
    public Telemetria obterTelemetriaGeral() {
        Telemetria telemetria = new Telemetria();

        List<TelemetriaServico> servicos = new ArrayList<>();
        LocalDateTime menorInicio = null;
        LocalDateTime maiorFim = null;

        for (Map.Entry<String, EstatisticaServico> entry : estatisticasPorServico.entrySet()) {
            String nomeServico = entry.getKey();
            EstatisticaServico estatistica = entry.getValue();

            TelemetriaServico servico = new TelemetriaServico();
            servico.setNome(nomeServico);
            servico.setQuantidadeChamadas(estatistica.getQuantidadeChamadas());
            servico.setMediaTempoRespostaMs(estatistica.calcularMediaTempoRespostaMs());

            servicos.add(servico);

            if (estatistica.getPrimeiraChamada() != null) {
                if (menorInicio == null || estatistica.getPrimeiraChamada().isBefore(menorInicio)) {
                    menorInicio = estatistica.getPrimeiraChamada();
                }
            }

            if (estatistica.getUltimaChamada() != null) {
                if (maiorFim == null || estatistica.getUltimaChamada().isAfter(maiorFim)) {
                    maiorFim = estatistica.getUltimaChamada();
                }
            }
        }

        telemetria.setServicos(servicos);

        if (menorInicio != null && maiorFim != null) {
            TelemetriaPeriodo periodo = new TelemetriaPeriodo();
            periodo.setInicio(menorInicio.toLocalDate());
            periodo.setFim(maiorFim.toLocalDate());
            telemetria.setPeriodo(periodo);
        }

        return telemetria;
    }

    private static final class EstatisticaServico {

        private long quantidadeChamadas;
        private long somaTempoRespostaMs;
        private LocalDateTime primeiraChamada;
        private LocalDateTime ultimaChamada;

        synchronized void registrarChamada(long tempoRespostaMs, LocalDateTime momento) {
            quantidadeChamadas++;
            somaTempoRespostaMs += tempoRespostaMs;

            if (primeiraChamada == null || momento.isBefore(primeiraChamada)) {
                primeiraChamada = momento;
            }

            if (ultimaChamada == null || momento.isAfter(ultimaChamada)) {
                ultimaChamada = momento;
            }
        }

        long getQuantidadeChamadas() {
            return quantidadeChamadas;
        }

        long calcularMediaTempoRespostaMs() {
            if (quantidadeChamadas == 0) {
                return 0L;
            }

            return somaTempoRespostaMs / quantidadeChamadas;
        }

        LocalDateTime getPrimeiraChamada() {
            return primeiraChamada;
        }

        LocalDateTime getUltimaChamada() {
            return ultimaChamada;
        }
    }
}