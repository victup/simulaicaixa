package br.gov.caixa.simulaicaixa.domain;

import br.gov.caixa.simulaicaixa.domain.enums.TipoInvestimentoEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Investimento {

    private Long id;
    private Long clienteId;
    private TipoInvestimentoEnum tipo;
    private BigDecimal valor;
    private BigDecimal rentabilidade;
    private LocalDate data;

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

    public TipoInvestimentoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoInvestimentoEnum tipo) {
        this.tipo = tipo;
    }

    public Integer getTipoCodigo() {
        return tipo != null ? tipo.getCodigo() : null;
    }

    public String getTipoDescricao() {
        return tipo != null ? tipo.getDescricao() : null;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getRentabilidade() {
        return rentabilidade;
    }

    public void setRentabilidade(BigDecimal rentabilidade) {
        this.rentabilidade = rentabilidade;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}