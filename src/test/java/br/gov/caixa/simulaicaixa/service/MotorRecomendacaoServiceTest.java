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

    @Test
    void deveAplicarBonusPorVolumeEmTodasAsFaixas() {
        PerfilRisco perfilRisco = new PerfilRisco();
        perfilRisco.setTipoPerfilRisco(TipoPerfilRiscoEnum.MODERADO);

        ProdutoInvestimento produtoVolumeBaixo = new ProdutoInvestimento();
        produtoVolumeBaixo.setPerfilRecomendado(TipoPerfilRiscoEnum.MODERADO);
        produtoVolumeBaixo.setRisco(NivelRiscoProdutoEnum.MEDIO);
        produtoVolumeBaixo.setRentabilidade(new BigDecimal("0.10"));
        produtoVolumeBaixo.setTipoEnum(TipoInvestimentoEnum.CDB);

        ProdutoInvestimento produtoVolumeMedio = new ProdutoInvestimento();
        produtoVolumeMedio.setPerfilRecomendado(TipoPerfilRiscoEnum.MODERADO);
        produtoVolumeMedio.setRisco(NivelRiscoProdutoEnum.MEDIO);
        produtoVolumeMedio.setRentabilidade(new BigDecimal("0.10"));
        produtoVolumeMedio.setTipoEnum(TipoInvestimentoEnum.FUNDO);

        ProdutoInvestimento produtoVolumeAlto = new ProdutoInvestimento();
        produtoVolumeAlto.setPerfilRecomendado(TipoPerfilRiscoEnum.MODERADO);
        produtoVolumeAlto.setRisco(NivelRiscoProdutoEnum.MEDIO);
        produtoVolumeAlto.setRentabilidade(new BigDecimal("0.10"));
        produtoVolumeAlto.setTipoEnum(TipoInvestimentoEnum.LCI);

        ProdutoInvestimento produtoVolumeMuitoAlto = new ProdutoInvestimento();
        produtoVolumeMuitoAlto.setPerfilRecomendado(TipoPerfilRiscoEnum.MODERADO);
        produtoVolumeMuitoAlto.setRisco(NivelRiscoProdutoEnum.MEDIO);
        produtoVolumeMuitoAlto.setRentabilidade(new BigDecimal("0.10"));
        produtoVolumeMuitoAlto.setTipoEnum(TipoInvestimentoEnum.LCA);

        Investimento cdb1 = new Investimento();
        cdb1.setTipo(TipoInvestimentoEnum.CDB);
        cdb1.setValor(new BigDecimal("250"));
        cdb1.setRentabilidade(new BigDecimal("0.10"));
        cdb1.setData(LocalDate.now().minusDays(1));

        Investimento cdb2 = new Investimento();
        cdb2.setTipo(TipoInvestimentoEnum.CDB);
        cdb2.setValor(new BigDecimal("250"));
        cdb2.setRentabilidade(new BigDecimal("0.10"));
        cdb2.setData(LocalDate.now().minusDays(1));

        Investimento fundo1 = new Investimento();
        fundo1.setTipo(TipoInvestimentoEnum.FUNDO);
        fundo1.setValor(new BigDecimal("2500"));
        fundo1.setRentabilidade(new BigDecimal("0.10"));
        fundo1.setData(LocalDate.now().minusDays(1));

        Investimento fundo2 = new Investimento();
        fundo2.setTipo(TipoInvestimentoEnum.FUNDO);
        fundo2.setValor(new BigDecimal("2500"));
        fundo2.setRentabilidade(new BigDecimal("0.10"));
        fundo2.setData(LocalDate.now().minusDays(1));

        Investimento lci1 = new Investimento();
        lci1.setTipo(TipoInvestimentoEnum.LCI);
        lci1.setValor(new BigDecimal("10000"));
        lci1.setRentabilidade(new BigDecimal("0.10"));
        lci1.setData(LocalDate.now().minusDays(1));

        Investimento lci2 = new Investimento();
        lci2.setTipo(TipoInvestimentoEnum.LCI);
        lci2.setValor(new BigDecimal("10000"));
        lci2.setRentabilidade(new BigDecimal("0.10"));
        lci2.setData(LocalDate.now().minusDays(1));

        Investimento lca1 = new Investimento();
        lca1.setTipo(TipoInvestimentoEnum.LCA);
        lca1.setValor(new BigDecimal("30000"));
        lca1.setRentabilidade(new BigDecimal("0.10"));
        lca1.setData(LocalDate.now().minusDays(1));

        Investimento lca2 = new Investimento();
        lca2.setTipo(TipoInvestimentoEnum.LCA);
        lca2.setValor(new BigDecimal("30000"));
        lca2.setRentabilidade(new BigDecimal("0.10"));
        lca2.setData(LocalDate.now().minusDays(1));

        List<Investimento> historico = List.of(
                cdb1, cdb2,
                fundo1, fundo2,
                lci1, lci2,
                lca1, lca2
        );

        List<ProdutoInvestimento> produtos = List.of(
                produtoVolumeBaixo,
                produtoVolumeMedio,
                produtoVolumeAlto,
                produtoVolumeMuitoAlto
        );

        List<ProdutoInvestimento> ordenados =
                motor.recomendarPorPerfilEHistorico(perfilRisco, produtos, historico);

        assertEquals(produtoVolumeMuitoAlto, ordenados.getFirst());
        assertEquals(produtoVolumeBaixo, ordenados.getLast());
    }

    @Test
    void deveAplicarFatorPreferenciaPorLiquidezQuandoMediaRentabilidadeBaixa() {
        PerfilRisco perfilRisco = new PerfilRisco();
        perfilRisco.setTipoPerfilRisco(TipoPerfilRiscoEnum.CONSERVADOR);

        ProdutoInvestimento produtoRentMaior = new ProdutoInvestimento();
        produtoRentMaior.setPerfilRecomendado(TipoPerfilRiscoEnum.CONSERVADOR);
        produtoRentMaior.setRisco(NivelRiscoProdutoEnum.BAIXO);
        produtoRentMaior.setRentabilidade(new BigDecimal("0.12"));
        produtoRentMaior.setTipoEnum(TipoInvestimentoEnum.CDB);

        ProdutoInvestimento produtoRentMenor = new ProdutoInvestimento();
        produtoRentMenor.setPerfilRecomendado(TipoPerfilRiscoEnum.CONSERVADOR);
        produtoRentMenor.setRisco(NivelRiscoProdutoEnum.BAIXO);
        produtoRentMenor.setRentabilidade(new BigDecimal("0.08"));
        produtoRentMenor.setTipoEnum(TipoInvestimentoEnum.CDB);

        Investimento inv1 = new Investimento();
        inv1.setRentabilidade(new BigDecimal("0.05"));

        Investimento inv2 = new Investimento();
        inv2.setRentabilidade(new BigDecimal("0.06"));

        List<Investimento> historico = List.of(inv1, inv2);
        List<ProdutoInvestimento> produtos = List.of(produtoRentMenor, produtoRentMaior);

        List<ProdutoInvestimento> ordenados =
                motor.recomendarPorPerfilEHistorico(perfilRisco, produtos, historico);

        assertEquals(produtoRentMaior, ordenados.getFirst());
    }

    @Test
    void deveManterPreferenciaNeutraQuandoHistoricoNaoPossuirRentabilidade() {
        PerfilRisco perfilRisco = new PerfilRisco();
        perfilRisco.setTipoPerfilRisco(TipoPerfilRiscoEnum.MODERADO);

        ProdutoInvestimento produtoRentMaior = new ProdutoInvestimento();
        produtoRentMaior.setPerfilRecomendado(TipoPerfilRiscoEnum.MODERADO);
        produtoRentMaior.setRisco(NivelRiscoProdutoEnum.MEDIO);
        produtoRentMaior.setRentabilidade(new BigDecimal("0.15"));
        produtoRentMaior.setTipoEnum(TipoInvestimentoEnum.CDB);

        ProdutoInvestimento produtoRentMenor = new ProdutoInvestimento();
        produtoRentMenor.setPerfilRecomendado(TipoPerfilRiscoEnum.MODERADO);
        produtoRentMenor.setRisco(NivelRiscoProdutoEnum.MEDIO);
        produtoRentMenor.setRentabilidade(new BigDecimal("0.10"));
        produtoRentMenor.setTipoEnum(TipoInvestimentoEnum.CDB);

        Investimento inv1 = new Investimento();
        inv1.setRentabilidade(null);
        inv1.setTipo(null);
        inv1.setValor(new BigDecimal("1000"));

        Investimento inv2 = new Investimento();
        inv2.setRentabilidade(null);
        inv2.setTipo(null);
        inv2.setValor(new BigDecimal("2000"));

        List<Investimento> historico = List.of(inv1, inv2);
        List<ProdutoInvestimento> produtos = List.of(produtoRentMenor, produtoRentMaior);

        List<ProdutoInvestimento> ordenados =
                motor.recomendarPorPerfilEHistorico(perfilRisco, produtos, historico);

        assertEquals(produtoRentMaior, ordenados.getFirst());
    }
}