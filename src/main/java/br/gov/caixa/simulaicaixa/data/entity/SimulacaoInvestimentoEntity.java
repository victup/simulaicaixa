package br.gov.caixa.simulaicaixa.data.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "simulacao_investimento")
public class SimulacaoInvestimentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "nome_produto", nullable = false, length = 200)
    private String nomeProduto;

    @Column(name = "tipo_produto", nullable = false, length = 50)
    private String tipoProduto;

    @Column(name = "valor_investido", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorInvestido;

    @Column(name = "valor_final", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorFinal;

    @Column(name = "prazo_meses", nullable = false)
    private Integer prazoMeses;

    @Column(name = "data_simulacao", nullable = false)
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