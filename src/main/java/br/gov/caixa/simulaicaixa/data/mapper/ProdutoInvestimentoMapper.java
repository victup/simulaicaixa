package br.gov.caixa.simulaicaixa.data.mapper;

import br.gov.caixa.simulaicaixa.data.entity.ProdutoInvestimentoEntity;
import br.gov.caixa.simulaicaixa.domain.ProdutoInvestimento;
import br.gov.caixa.simulaicaixa.domain.enums.NivelRiscoProdutoEnum;
import br.gov.caixa.simulaicaixa.domain.enums.TipoInvestimentoEnum;
import br.gov.caixa.simulaicaixa.domain.enums.TipoPerfilRiscoEnum;

public final class ProdutoInvestimentoMapper {

    private ProdutoInvestimentoMapper() {
    }

    public static ProdutoInvestimentoEntity mapearParaEntity(ProdutoInvestimento produto) {
        ProdutoInvestimentoEntity entity = new ProdutoInvestimentoEntity();

        entity.setId(produto.getId());
        entity.setNome(produto.getNome());

        TipoInvestimentoEnum tipoEnum = produto.getTipoEnum();
        entity.setTipo(tipoEnum != null ? tipoEnum.getCodigo() : null);

        entity.setRentabilidade(produto.getRentabilidade());

        NivelRiscoProdutoEnum riscoEnum = produto.getRisco();
        entity.setRisco(riscoEnum != null ? riscoEnum.getCodigo() : null);

        TipoPerfilRiscoEnum perfilEnum = produto.getPerfilRecomendado();
        entity.setPerfilRecomendado(perfilEnum != null ? perfilEnum.getCodigo() : null);

        return entity;
    }

    public static ProdutoInvestimento mapearParaDominio(ProdutoInvestimentoEntity entity) {
        ProdutoInvestimento produto = new ProdutoInvestimento();

        produto.setId(entity.getId());
        produto.setNome(entity.getNome());
        produto.setTipoEnum(TipoInvestimentoEnum.obterPorCodigo(entity.getTipo()));
        produto.setRentabilidade(entity.getRentabilidade());
        produto.setRisco(NivelRiscoProdutoEnum.obterPorCodigo(entity.getRisco()));
        produto.setPerfilRecomendado(TipoPerfilRiscoEnum.obterPorCodigo(entity.getPerfilRecomendado()));

        return produto;
    }
}