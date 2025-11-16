package br.gov.caixa.simulaicaixa.data.mapper;

import br.gov.caixa.simulaicaixa.data.entity.ProdutoInvestimentoEntity;
import br.gov.caixa.simulaicaixa.domain.ProdutoInvestimento;

public final class ProdutoInvestimentoMapper {

    private ProdutoInvestimentoMapper() {
    }

    public static ProdutoInvestimentoEntity mapearParaEntity(ProdutoInvestimento produto) {
        ProdutoInvestimentoEntity entity = new ProdutoInvestimentoEntity();

        entity.setId(produto.getId());
        entity.setNome(produto.getNome());
        entity.setTipo(produto.getTipo());
        entity.setRentabilidade(produto.getRentabilidade());
        entity.setRisco(produto.getRisco());
        entity.setPerfilRecomendado(produto.getPerfilRecomendado());

        return entity;
    }

    public static ProdutoInvestimento mapearParaDominio(ProdutoInvestimentoEntity entity) {
        ProdutoInvestimento produto = new ProdutoInvestimento();

        produto.setId(entity.getId());
        produto.setNome(entity.getNome());
        produto.setTipo(entity.getTipo());
        produto.setRentabilidade(entity.getRentabilidade());
        produto.setRisco(entity.getRisco());
        produto.setPerfilRecomendado(entity.getPerfilRecomendado());

        return produto;
    }
}