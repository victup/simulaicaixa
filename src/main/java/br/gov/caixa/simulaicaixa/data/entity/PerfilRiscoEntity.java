package br.gov.caixa.simulaicaixa.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "perfil_risco")
public class PerfilRiscoEntity {

    @Id
    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(nullable = false)
    private Integer perfil;

    @Column(nullable = false)
    private Integer pontuacao;

    @Column(nullable = false, length = 255)
    private String descricao;

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getPerfil() {
        return perfil;
    }

    public void setPerfil(Integer perfil) {
        this.perfil = perfil;
    }

    public Integer getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(Integer pontuacao) {
        this.pontuacao = pontuacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}