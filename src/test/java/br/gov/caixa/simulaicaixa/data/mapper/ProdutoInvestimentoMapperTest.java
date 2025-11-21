package br.gov.caixa.simulaicaixa.data.mapper;

import br.gov.caixa.simulaicaixa.data.entity.ProdutoInvestimentoEntity;
import br.gov.caixa.simulaicaixa.domain.ProdutoInvestimento;
import br.gov.caixa.simulaicaixa.domain.enums.NivelRiscoProdutoEnum;
import br.gov.caixa.simulaicaixa.domain.enums.TipoInvestimentoEnum;
import br.gov.caixa.simulaicaixa.domain.enums.TipoPerfilRiscoEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoInvestimentoMapperTest {

    @Test
    void deveMapearDominioParaEntityComEnums() {
        ProdutoInvestimento produto = new ProdutoInvestimento();
        produto.setId(100L);
        produto.setNome("CDB Caixa 2026");
        produto.setTipoEnum(TipoInvestimentoEnum.CDB);
        produto.setRentabilidade(new BigDecimal("0.12"));
        produto.setRisco(NivelRiscoProdutoEnum.BAIXO);
        produto.setPerfilRecomendado(TipoPerfilRiscoEnum.CONSERVADOR);

        ProdutoInvestimentoEntity entity = ProdutoInvestimentoMapper.mapearParaEntity(produto);

        assertEquals(100L, entity.getId());
        assertEquals("CDB Caixa 2026", entity.getNome());
        assertEquals(TipoInvestimentoEnum.CDB.getCodigo(), entity.getTipo());
        assertEquals(new BigDecimal("0.12"), entity.getRentabilidade());
        assertEquals(NivelRiscoProdutoEnum.BAIXO.getCodigo(), entity.getRisco());
        assertEquals(TipoPerfilRiscoEnum.CONSERVADOR.getCodigo(), entity.getPerfilRecomendado());
    }

    @Test
    void deveMapearDominioParaEntityComEnumsNulos() {
        ProdutoInvestimento produto = new ProdutoInvestimento();
        produto.setId(200L);
        produto.setNome("Produto sem enums");
        produto.setTipoEnum(null);
        produto.setRentabilidade(new BigDecimal("0.05"));

        ProdutoInvestimentoEntity entity = ProdutoInvestimentoMapper.mapearParaEntity(produto);

        assertNull(entity.getTipo());
        assertNull(entity.getRisco());
        assertNull(entity.getPerfilRecomendado());
    }

    @Test
    void deveMapearEntityParaDominioComEnums() {
        ProdutoInvestimentoEntity entity = new ProdutoInvestimentoEntity();
        entity.setId(300L);
        entity.setNome("Fundo XPTO");
        entity.setTipo(TipoInvestimentoEnum.FUNDO.getCodigo());
        entity.setRentabilidade(new BigDecimal("0.18"));
        entity.setRisco(NivelRiscoProdutoEnum.ALTO.getCodigo());
        entity.setPerfilRecomendado(TipoPerfilRiscoEnum.AGRESSIVO.getCodigo());

        ProdutoInvestimento produto = ProdutoInvestimentoMapper.mapearParaDominio(entity);

        assertEquals(300L, produto.getId());
        assertEquals("Fundo XPTO", produto.getNome());
        assertEquals(TipoInvestimentoEnum.FUNDO, produto.getTipoEnum());
        assertEquals(new BigDecimal("0.18"), produto.getRentabilidade());
        assertEquals(NivelRiscoProdutoEnum.ALTO, produto.getRisco());
        assertEquals(TipoPerfilRiscoEnum.AGRESSIVO, produto.getPerfilRecomendado());
    }
}