package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.domain.enums.TipoPerfilRiscoEnum;
import br.gov.caixa.simulaicaixa.dto.ProdutoRecomendadoDto;
import br.gov.caixa.simulaicaixa.service.ProdutoService;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class ProdutoResourceTest {

    @InjectMock
    ProdutoService produtoService;

    @InjectMock
    ColetorTelemetria coletorTelemetria;

    @Test
    @TestSecurity(user = "12345678901", roles = {"cliente"})
    void deveListarProdutosPorPerfil() {
        String perfilDescricao = TipoPerfilRiscoEnum.MODERADO.getDescricao();

        ProdutoRecomendadoDto produto = new ProdutoRecomendadoDto(
                101L,
                "CDB Caixa 2026",
                "CDB",
                new BigDecimal("0.12"),
                "Baixo"
        );

        when(produtoService.listarProdutosRecomendadosPorPerfil(perfilDescricao))
                .thenReturn(List.of(produto));

        given()
                .accept(ContentType.JSON)
                .when()
                .get("/produtos-recomendados/" + perfilDescricao)
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", equalTo(101));

        verify(produtoService).listarProdutosRecomendadosPorPerfil(perfilDescricao);
        verify(coletorTelemetria).registrarChamada(eq("produtos-recomendados"), anyLong());
    }

    @Test
    @TestSecurity(user = "12345678901", roles = {"cliente"})
    void deveListarProdutosParaClienteAtual() {
        ProdutoRecomendadoDto produto = new ProdutoRecomendadoDto(
                101L,
                "CDB Caixa 2026",
                "CDB",
                new BigDecimal("0.12"),
                "Baixo"
        );

        when(produtoService.listarProdutosRecomendadosParaClienteAtual())
                .thenReturn(List.of(produto));

        given()
                .accept(ContentType.JSON)
                .when()
                .get("/produtos-recomendados/usuario-logado")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", equalTo(101));

        verify(produtoService).listarProdutosRecomendadosParaClienteAtual();
    }
}