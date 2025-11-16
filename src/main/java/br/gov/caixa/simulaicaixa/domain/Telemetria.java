package br.gov.caixa.simulaicaixa.domain;

import java.util.List;

public class Telemetria {

    private List<TelemetriaServico> servicos;
    private TelemetriaPeriodo periodo;

    public List<TelemetriaServico> getServicos() {
        return servicos;
    }

    public void setServicos(List<TelemetriaServico> servicos) {
        this.servicos = servicos;
    }

    public TelemetriaPeriodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(TelemetriaPeriodo periodo) {
        this.periodo = periodo;
    }
}