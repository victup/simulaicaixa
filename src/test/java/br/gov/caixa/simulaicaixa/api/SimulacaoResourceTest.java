package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.domain.enums.NivelRiscoProdutoEnum;
import br.gov.caixa.simulaicaixa.domain.enums.TipoInvestimentoEnum;
import br.gov.caixa.simulaicaixa.dto.ProdutoValidadoDto;
import br.gov.caixa.simulaicaixa.dto.RespostaSimulacaoDto;
import br.gov.caixa.simulaicaixa.dto.ResultadoSimulacaoDto;
import br.gov.caixa.simulaicaixa.dto.SimulacaoHistoricoDto;
import br.gov.caixa.simulaicaixa.dto.SimulacaoPorProdutoDiaDto;
import br.gov.caixa.simulaicaixa.dto.SolicitacaoSimulacaoDto;
import br.gov.caixa.simulaicaixa.service.SimulacaoService;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class SimulacaoResourceTest {

    @InjectMock
    SimulacaoService simulacaoService;

    @InjectMock
    ColetorTelemetria coletorTelemetria;

    @Test
    @TestSecurity(user = "12345678901", roles = {"cliente"})
    void deveSimularInvestimentoComSucesso() {
        ProdutoValidadoDto produtoValidado = new ProdutoValidadoDto(
                1L,
                "Produto " + TipoInvestimentoEnum.CDB.getDescricao(),
                TipoInvestimentoEnum.CDB.getDescricao(),
                new BigDecimal("0.12"),
                NivelRiscoProdutoEnum.BAIXO.getDescricao()
        );

        ResultadoSimulacaoDto resultadoSimulacao = new ResultadoSimulacaoDto(
                new BigDecimal("11200.00"),
                new BigDecimal("0.12"),
                12
        );

        RespostaSimulacaoDto resposta = new RespostaSimulacaoDto(
                produtoValidado,
                resultadoSimulacao,
                OffsetDateTime.parse("2025-01-01T10:00:00Z")
        );

        when(simulacaoService.simularInvestimento(any(SolicitacaoSimulacaoDto.class)))
                .thenReturn(resposta);

        SolicitacaoSimulacaoDto solicitacao = new SolicitacaoSimulacaoDto(
                123L,
                new BigDecimal("10000.00"),
                12,
                TipoInvestimentoEnum.CDB.getDescricao()
        );

        given()
                .contentType(ContentType.JSON)
                .body(solicitacao)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(200)
                .body("produtoValidado.id", equalTo(1))
                .body("produtoValidado.nome", equalTo("Produto " + TipoInvestimentoEnum.CDB.getDescricao()))
                .body("resultadoSimulacao.prazoMeses", equalTo(12));
    }

    @Test
    @TestSecurity(user = "12345678901", roles = {"cliente"})
    void deveListarSimulacoes() {
        SimulacaoHistoricoDto simulacao = new SimulacaoHistoricoDto(
                1L,
                123L,
                "Produto " + TipoInvestimentoEnum.CDB.getDescricao(),
                new BigDecimal("10000.00"),
                new BigDecimal("11200.00"),
                12,
                OffsetDateTime.parse("2025-01-01T10:00:00Z")
        );

        when(simulacaoService.listarSimulacoes())
                .thenReturn(List.of(simulacao));

        given()
                .when()
                .get("/simulacoes")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].clienteId", equalTo(123));
    }

    @Test
    @TestSecurity(user = "12345678901", roles = {"cliente"})
    void deveListarSimulacoesAgrupadasPorProdutoEDia() {
        SimulacaoPorProdutoDiaDto agrupado = new SimulacaoPorProdutoDiaDto(
                "Produto " + TipoInvestimentoEnum.CDB.getDescricao(),
                LocalDate.parse("2025-01-01"),
                3L,
                new BigDecimal("10500.00")
        );

        when(simulacaoService.listarSimulacoesPorProdutoEDia())
                .thenReturn(List.of(agrupado));

        given()
                .when()
                .get("/simulacoes/por-produto-dia")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].produto", equalTo("Produto " + TipoInvestimentoEnum.CDB.getDescricao()))
                .body("[0].quantidadeSimulacoes", equalTo(3));
    }
}