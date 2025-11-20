package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.domain.Investimento;
import br.gov.caixa.simulaicaixa.domain.PerfilRisco;
import br.gov.caixa.simulaicaixa.domain.ProdutoInvestimento;
import br.gov.caixa.simulaicaixa.domain.enums.NivelRiscoProdutoEnum;
import br.gov.caixa.simulaicaixa.domain.enums.TipoInvestimentoEnum;
import br.gov.caixa.simulaicaixa.domain.enums.TipoPerfilRiscoEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MotorRecomendacaoServiceTest {

    private final MotorRecomendacaoServiceImpl motor = new MotorRecomendacaoServiceImpl();

    @Test
    void deveOrdenarProdutosPorScoreParaPerfilModeradoSemHistorico() {
        PerfilRisco perfilRisco = new PerfilRisco();
        perfilRisco.setTipoPerfilRisco(TipoPerfilRiscoEnum.MODERADO);

        ProdutoInvestimento produtoConservador = new ProdutoInvestimento();
        produtoConservador.setPerfilRecomendado(TipoPerfilRiscoEnum.CONSERVADOR);
        produtoConservador.setRisco(NivelRiscoProdutoEnum.BAIXO);
        produtoConservador.setRentabilidade(new BigDecimal("0.09"));
        produtoConservador.setTipoEnum(TipoInvestimentoEnum.CDB);

        ProdutoInvestimento produtoAgressivo = new ProdutoInvestimento();
        produtoAgressivo.setPerfilRecomendado(TipoPerfilRiscoEnum.AGRESSIVO);
        produtoAgressivo.setRisco(NivelRiscoProdutoEnum.ALTO);
        produtoAgressivo.setRentabilidade(new BigDecimal("0.18"));
        produtoAgressivo.setTipoEnum(TipoInvestimentoEnum.FUNDO);

        List<ProdutoInvestimento> produtos = List.of(produtoConservador, produtoAgressivo);

        List<ProdutoInvestimento> ordenados =
                motor.recomendarPorPerfil(perfilRisco, produtos);

        assertEquals(produtoAgressivo, ordenados.getFirst());
        assertEquals(produtoConservador, ordenados.getLast());
    }

    @Test
    void devePriorizarProdutoComMesmoPerfilDoCliente() {
        PerfilRisco perfilRisco = new PerfilRisco();
        perfilRisco.setTipoPerfilRisco(TipoPerfilRiscoEnum.CONSERVADOR);

        ProdutoInvestimento produtoMesmoPerfil = new ProdutoInvestimento();
        produtoMesmoPerfil.setPerfilRecomendado(TipoPerfilRiscoEnum.CONSERVADOR);
        produtoMesmoPerfil.setRisco(NivelRiscoProdutoEnum.BAIXO);
        produtoMesmoPerfil.setRentabilidade(new BigDecimal("0.10"));
        produtoMesmoPerfil.setTipoEnum(TipoInvestimentoEnum.CDB);

        ProdutoInvestimento produtoPerfilDiferente = new ProdutoInvestimento();
        produtoPerfilDiferente.setPerfilRecomendado(TipoPerfilRiscoEnum.AGRESSIVO);
        produtoPerfilDiferente.setRisco(NivelRiscoProdutoEnum.ALTO);
        produtoPerfilDiferente.setRentabilidade(new BigDecimal("0.10"));
        produtoPerfilDiferente.setTipoEnum(TipoInvestimentoEnum.FUNDO);

        List<ProdutoInvestimento> produtos = List.of(produtoPerfilDiferente, produtoMesmoPerfil);

        List<ProdutoInvestimento> ordenados =
                motor.recomendarPorPerfil(perfilRisco, produtos);

        assertEquals(produtoMesmoPerfil, ordenados.getFirst());
    }

    @Test
    void deveFavorecerProdutoComMesmoTipoDoHistorico() {
        PerfilRisco perfilRisco = new PerfilRisco();
        perfilRisco.setTipoPerfilRisco(TipoPerfilRiscoEnum.MODERADO);

        ProdutoInvestimento produtoCdb = new ProdutoInvestimento();
        produtoCdb.setPerfilRecomendado(TipoPerfilRiscoEnum.MODERADO);
        produtoCdb.setRisco(NivelRiscoProdutoEnum.MEDIO);
        produtoCdb.setRentabilidade(new BigDecimal("0.12"));
        produtoCdb.setTipoEnum(TipoInvestimentoEnum.CDB);

        ProdutoInvestimento produtoFundo = new ProdutoInvestimento();
        produtoFundo.setPerfilRecomendado(TipoPerfilRiscoEnum.MODERADO);
        produtoFundo.setRisco(NivelRiscoProdutoEnum.MEDIO);
        produtoFundo.setRentabilidade(new BigDecimal("0.12"));
        produtoFundo.setTipoEnum(TipoInvestimentoEnum.FUNDO);

        Investimento inv1 = new Investimento();
        inv1.setTipo(TipoInvestimentoEnum.CDB);
        inv1.setValor(new BigDecimal("5000"));
        inv1.setRentabilidade(new BigDecimal("0.11"));
        inv1.setData(LocalDate.now().minusDays(30));

        Investimento inv2 = new Investimento();
        inv2.setTipo(TipoInvestimentoEnum.CDB);
        inv2.setValor(new BigDecimal("6000"));
        inv2.setRentabilidade(new BigDecimal("0.12"));
        inv2.setData(LocalDate.now().minusDays(10));

        List<Investimento> historico = List.of(inv1, inv2);
        List<ProdutoInvestimento> produtos = List.of(produtoFundo, produtoCdb);

        List<ProdutoInvestimento> ordenados =
                motor.recomendarPorPerfilEHistorico(perfilRisco, produtos, historico);

        assertEquals(produtoCdb, ordenados.getFirst());
    }

    @Test
    void deveAplicarFatorPreferenciaPorRentabilidadeAlta() {
        PerfilRisco perfilRisco = new PerfilRisco();
        perfilRisco.setTipoPerfilRisco(TipoPerfilRiscoEnum.AGRESSIVO);

        ProdutoInvestimento produtoRentabilidadeAlta = new ProdutoInvestimento();
        produtoRentabilidadeAlta.setPerfilRecomendado(TipoPerfilRiscoEnum.AGRESSIVO);
        produtoRentabilidadeAlta.setRisco(NivelRiscoProdutoEnum.ALTO);
        produtoRentabilidadeAlta.setRentabilidade(new BigDecimal("0.20"));
        produtoRentabilidadeAlta.setTipoEnum(TipoInvestimentoEnum.FUNDO);

        ProdutoInvestimento produtoRentabilidadeBaixa = new ProdutoInvestimento();
        produtoRentabilidadeBaixa.setPerfilRecomendado(TipoPerfilRiscoEnum.AGRESSIVO);
        produtoRentabilidadeBaixa.setRisco(NivelRiscoProdutoEnum.ALTO);
        produtoRentabilidadeBaixa.setRentabilidade(new BigDecimal("0.08"));
        produtoRentabilidadeBaixa.setTipoEnum(TipoInvestimentoEnum.FUNDO);

        Investimento inv1 = new Investimento();
        inv1.setRentabilidade(new BigDecimal("0.15"));

        Investimento inv2 = new Investimento();
        inv2.setRentabilidade(new BigDecimal("0.16"));

        List<Investimento> historico = List.of(inv1, inv2);
        List<ProdutoInvestimento> produtos = List.of(produtoRentabilidadeBaixa, produtoRentabilidadeAlta);

        List<ProdutoInvestimento> ordenados =
                motor.recomendarPorPerfilEHistorico(perfilRisco, produtos, historico);

        assertEquals(produtoRentabilidadeAlta, ordenados.getFirst());
    }
}