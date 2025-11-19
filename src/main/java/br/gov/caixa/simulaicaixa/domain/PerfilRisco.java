package br.gov.caixa.simulaicaixa.domain;

import br.gov.caixa.simulaicaixa.domain.enums.TipoPerfilRiscoEnum;

public class PerfilRisco {

    private Long clienteId;
    private TipoPerfilRiscoEnum tipoPerfilRisco;
    private Integer pontuacao;
    private String descricao;

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getPerfil() {
        return tipoPerfilRisco != null ? tipoPerfilRisco.getDescricao() : null;
    }

    public void setPerfil(String perfil) {
        this.tipoPerfilRisco = TipoPerfilRiscoEnum.obterPorDescricao(perfil);
    }

    public TipoPerfilRiscoEnum getTipoPerfilRisco() {
        return tipoPerfilRisco;
    }

    public void setTipoPerfilRisco(TipoPerfilRiscoEnum tipoPerfilRisco) {
        this.tipoPerfilRisco = tipoPerfilRisco;
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