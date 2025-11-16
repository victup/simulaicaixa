package br.gov.caixa.simulaicaixa.telemetria;

import br.gov.caixa.simulaicaixa.domain.Telemetria;

public interface ColetorTelemetria {

    void registrarChamada(String nomeServico, long tempoRespostaMs);

    Telemetria obterTelemetriaGeral();
}