package br.gov.caixa.simulaicaixa.domain;

import java.math.BigDecimal;

public class ProdutoInvestimento {

    private Long id;
    private String nome;
    private String tipo;
    private BigDecimal rentabilidade;
    private String risco;
    private String perfilRecomendado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getRentabilidade() {
        return rentabilidade;
    }

    public void setRentabilidade(BigDecimal rentabilidade) {
        this.rentabilidade = rentabilidade;
    }

    public String getRisco() {
        return risco;
    }

    public void setRisco(String risco) {
        this.risco = risco;
    }

    public String getPerfilRecomendado() {
        return perfilRecomendado;
    }

    public void setPerfilRecomendado(String perfilRecomendado) {
        this.perfilRecomendado = perfilRecomendado;
    }
}
