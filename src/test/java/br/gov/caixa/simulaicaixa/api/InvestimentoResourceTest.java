package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.seguranca.ValidadorAcessoCliente;
import br.gov.caixa.simulaicaixa.dto.InvestimentoHistoricoDto;
import br.gov.caixa.simulaicaixa.service.ContextoClienteService;
import br.gov.caixa.simulaicaixa.service.InvestimentoService;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@QuarkusTest
class InvestimentoResourceTest {

    @InjectMock
    InvestimentoService investimentoService;

    @InjectMock
    ColetorTelemetria coletorTelemetria;

    @InjectMock
    ContextoClienteService contextoClienteService;

    @InjectMock
    ValidadorAcessoCliente validadorAcessoCliente;

    @Test
    @TestSecurity(user = "12345678901", roles = {"cliente"})
    void deveListarInvestimentosPorCliente() {
        Long clienteId = 123L;

        InvestimentoHistoricoDto investimento = new InvestimentoHistoricoDto(
                1L,
                "CDB",
                new BigDecimal("1000.00"),
                new BigDecimal("0.10"),
                LocalDate.parse("2025-01-01")
        );

        when(investimentoService.listarPorClienteId(clienteId))
                .thenReturn(List.of(investimento));

        given()
                .accept(ContentType.JSON)
                .when()
                .get("/investimentos/" + clienteId)
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", equalTo(1))
                .body("[0].tipo", equalTo("CDB"));

        verify(validadorAcessoCliente)
                .validarClienteOuAdmin(clienteId, CodigoErroNegocio.OPERACAO_NAO_PERMITIDA_PARA_INVESTIMENTOS);
        verify(investimentoService).listarPorClienteId(clienteId);
        verify(coletorTelemetria).registrarChamada(eq("investimentos-por-cliente"), anyLong());
    }

    @Test
    @TestSecurity(user = "12345678901", roles = {"cliente"})
    void deveListarInvestimentosDoClienteAutenticado() {
        Long clienteId = 123L;

        InvestimentoHistoricoDto investimento = new InvestimentoHistoricoDto(
                1L,
                "CDB",
                new BigDecimal("1000.00"),
                new BigDecimal("0.10"),
                LocalDate.parse("2025-01-01")
        );

        when(contextoClienteService.obterClienteIdUsuarioObrigatorio())
                .thenReturn(clienteId);
        when(investimentoService.listarPorClienteId(clienteId))
                .thenReturn(List.of(investimento));

        given()
                .accept(ContentType.JSON)
                .when()
                .get("/investimentos/usuario-logado")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", equalTo(1));

        verify(contextoClienteService).obterClienteIdUsuarioObrigatorio();
        verifyNoInteractions(validadorAcessoCliente);
    }
}