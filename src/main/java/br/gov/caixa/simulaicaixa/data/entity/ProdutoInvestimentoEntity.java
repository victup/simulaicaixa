package br.gov.caixa.simulaicaixa.data.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "produto_investimento")
public class ProdutoInvestimentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal rentabilidade;

    @Column(nullable = false, length = 50)
    private String risco;

    @Column(name = "perfil_recomendado", nullable = false, length = 50)
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