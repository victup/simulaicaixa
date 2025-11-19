package br.gov.caixa.simulaicaixa.data.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "produto_investimento")
public class ProdutoInvestimentoEntity {

    @Id
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private Integer tipo;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal rentabilidade;

    @Column(nullable = false)
    private Integer risco;

    @Column(name = "perfil_recomendado", nullable = false)
    private Integer perfilRecomendado;

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

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getRentabilidade() {
        return rentabilidade;
    }

    public void setRentabilidade(BigDecimal rentabilidade) {
        this.rentabilidade = rentabilidade;
    }

    public Integer getRisco() {
        return risco;
    }

    public void setRisco(Integer risco) {
        this.risco = risco;
    }

    public Integer getPerfilRecomendado() {
        return perfilRecomendado;
    }

    public void setPerfilRecomendado(Integer perfilRecomendado) {
        this.perfilRecomendado = perfilRecomendado;
    }
}