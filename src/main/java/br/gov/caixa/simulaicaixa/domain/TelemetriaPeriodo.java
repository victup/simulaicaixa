package br.gov.caixa.simulaicaixa.domain;

import java.time.LocalDate;

public class TelemetriaPeriodo {

    private LocalDate inicio;
    private LocalDate fim;

    public LocalDate getInicio() {
        return inicio;
    }

    public void setInicio(LocalDate inicio) {
        this.inicio = inicio;
    }

    public LocalDate getFim() {
        return fim;
    }

    public void setFim(LocalDate fim) {
        this.fim = fim;
    }
}