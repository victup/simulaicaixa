package br.gov.caixa.simulaicaixa.telemetria;

import br.gov.caixa.simulaicaixa.data.entity.TelemetriaRegistroEntity;
import br.gov.caixa.simulaicaixa.data.repository.TelemetriaRegistroRepository;
import br.gov.caixa.simulaicaixa.domain.Telemetria;
import br.gov.caixa.simulaicaixa.domain.TelemetriaPeriodo;
import br.gov.caixa.simulaicaixa.domain.TelemetriaServico;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementação em memória do {@link ColetorTelemetria}.
 * <p>
 * Mantém estatísticas agregadas de tempo de resposta por serviço em memória
 * e persiste cada chamada de forma individual no banco de dados para
 * consultas históricas de telemetria.
 * </p>
 */
@ApplicationScoped
public class ColetorTelemetriaEmMemoria implements ColetorTelemetria {

    private final Map<String, EstatisticaServico> estatisticasPorServico = new ConcurrentHashMap<>();

    private final TelemetriaRegistroRepository telemetriaRegistroRepository;

    @Inject
    public ColetorTelemetriaEmMemoria(TelemetriaRegistroRepository telemetriaRegistroRepository) {
        this.telemetriaRegistroRepository = telemetriaRegistroRepository;
    }

    /**
     * Registra uma chamada de serviço na telemetria.
     * <p>
     * Atualiza as estatísticas em memória do serviço informado (quantidade de
     * chamadas, soma de tempos e intervalo entre primeira e última chamada) e
     * grava um registro persistente via {@link TelemetriaRegistroRepository}.
     * </p>
     *
     * @param nomeServico    identificador lógico do serviço (por exemplo, nome do endpoint)
     * @param tempoRespostaMs tempo de resposta da chamada em milissegundos
     */
    @Override
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

        telemetriaRegistroRepository.salvar(registro);
    }

    /**
     * Obtém um snapshot da telemetria agregada da aplicação.
     * <p>
     * A consolidação é feita apenas com base no estado em memória:
     * para cada serviço são retornadas quantidade de chamadas e
     * média de tempo de resposta, além do período entre a primeira
     * e a última chamada registradas em qualquer serviço.
     * </p>
     *
     * @return objeto {@link Telemetria} com métricas agregadas por serviço
     * e período total observado
     */
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

    /**
     * Estrutura interna de apoio para consolidação das estatísticas
     * de telemetria por serviço.
     */
    private static final class EstatisticaServico {

        private long quantidadeChamadas;
        private long somaTempoRespostaMs;
        private LocalDateTime primeiraChamada;
        private LocalDateTime ultimaChamada;

        /**
         * Atualiza as métricas internas com uma nova chamada registrada.
         *
         * @param tempoRespostaMs tempo de resposta da chamada em milissegundos
         * @param momento         instante em que a chamada foi registrada
         */
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

        /**
         * Calcula a média de tempo de resposta baseada em todas
         * as chamadas registradas para o serviço.
         *
         * @return média em milissegundos, ou {@code 0} se não houver chamadas
         */
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