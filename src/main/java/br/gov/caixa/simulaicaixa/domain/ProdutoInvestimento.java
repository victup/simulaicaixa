package br.gov.caixa.simulaicaixa.domain;

import br.gov.caixa.simulaicaixa.domain.enums.NivelRiscoProdutoEnum;
import br.gov.caixa.simulaicaixa.domain.enums.TipoInvestimentoEnum;
import br.gov.caixa.simulaicaixa.domain.enums.TipoPerfilRiscoEnum;

import java.math.BigDecimal;

public class ProdutoInvestimento {

    private Long id;
    private String nome;
    private TipoInvestimentoEnum tipo;
    private BigDecimal rentabilidade;
    private NivelRiscoProdutoEnum risco;
    private TipoPerfilRiscoEnum perfilRecomendado;

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
        return tipo != null ? tipo.getDescricao() : null;
    }

    public TipoInvestimentoEnum getTipoEnum() {
        return tipo;
    }

    public void setTipoEnum(TipoInvestimentoEnum tipo) {
        this.tipo = tipo;
    }

    public void setTipo(String tipoDescricao) {
        this.tipo = TipoInvestimentoEnum.obterPorDescricao(tipoDescricao);
    }

    public BigDecimal getRentabilidade() {
        return rentabilidade;
    }

    public void setRentabilidade(BigDecimal rentabilidade) {
        this.rentabilidade = rentabilidade;
    }

    public String getRiscoDescricao() {
        return risco != null ? risco.getDescricao() : null;
    }

    public NivelRiscoProdutoEnum getRisco() {
        return risco;
    }

    public void setRisco(NivelRiscoProdutoEnum risco) {
        this.risco = risco;
    }

    public void setRisco(String riscoDescricao) {
        this.risco = NivelRiscoProdutoEnum.obterPorDescricao(riscoDescricao);
    }

    public TipoPerfilRiscoEnum getPerfilRecomendado() {
        return perfilRecomendado;
    }

    public void setPerfilRecomendado(TipoPerfilRiscoEnum perfilRecomendado) {
        this.perfilRecomendado = perfilRecomendado;
    }

    public void setPerfilRecomendado(String perfilDescricao) {
        this.perfilRecomendado = TipoPerfilRiscoEnum.obterPorDescricao(perfilDescricao);
    }

    public String getPerfilRecomendadoDescricao() {
        return perfilRecomendado != null ? perfilRecomendado.getDescricao() : null;
    }
}