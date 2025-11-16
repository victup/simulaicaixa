package br.gov.caixa.simulaicaixa.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class SimulacaoInvestimento {
    private Long id;
    private Long clienteId;
    private String nomeProduto;
    private String tipoProduto;
    private BigDecimal valorInvestido;
    private BigDecimal valorFinal;
    private Integer prazoMeses;
    private OffsetDateTime dataSimulacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(String tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public BigDecimal getValorInvestido() {
        return valorInvestido;
    }

    public void setValorInvestido(BigDecimal valorInvestido) {
        this.valorInvestido = valorInvestido;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public Integer getPrazoMeses() {
        return prazoMeses;
    }

    public void setPrazoMeses(Integer prazoMeses) {
        this.prazoMeses = prazoMeses;
    }

    public OffsetDateTime getDataSimulacao() {
        return dataSimulacao;
    }

    public void setDataSimulacao(OffsetDateTime dataSimulacao) {
        this.dataSimulacao = dataSimulacao;
    }
}