package br.gov.caixa.simulaicaixa.data.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "telemetria_registro")
public class TelemetriaRegistroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_servico", nullable = false, length = 100)
    private String nomeServico;

    @Column(name = "tempo_resposta_ms", nullable = false)
    private Long tempoRespostaMs;

    @Column(name = "data_hora_chamada", nullable = false)
    private LocalDateTime dataHoraChamada;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public Long getTempoRespostaMs() {
        return tempoRespostaMs;
    }

    public void setTempoRespostaMs(Long tempoRespostaMs) {
        this.tempoRespostaMs = tempoRespostaMs;
    }

    public LocalDateTime getDataHoraChamada() {
        return dataHoraChamada;
    }

    public void setDataHoraChamada(LocalDateTime dataHoraChamada) {
        this.dataHoraChamada = dataHoraChamada;
    }
}