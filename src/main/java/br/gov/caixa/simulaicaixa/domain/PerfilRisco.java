package br.gov.caixa.simulaicaixa.domain;

import br.gov.caixa.simulaicaixa.domain.enums.TipoPerfilRisco;

public class PerfilRisco {

    private Long clienteId;
    private String perfil;
    private Integer pontuacao;
    private String descricao;
    private TipoPerfilRisco tipoPerfilRisco;

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
        this.tipoPerfilRisco = TipoPerfilRisco.fromDescricao(perfil);
    }

    public TipoPerfilRisco getTipoPerfilRisco() {
        if (tipoPerfilRisco == null) {
            tipoPerfilRisco = TipoPerfilRisco.fromDescricao(perfil);
        }
        return tipoPerfilRisco;
    }

    public void setTipoPerfilRisco(TipoPerfilRisco tipoPerfilRisco) {
        this.tipoPerfilRisco = tipoPerfilRisco;
        if (tipoPerfilRisco != null) {
            this.perfil = tipoPerfilRisco.name();
        }
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