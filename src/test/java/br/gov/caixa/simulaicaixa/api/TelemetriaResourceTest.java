package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.dto.TelemetriaDto;
import br.gov.caixa.simulaicaixa.dto.TelemetriaPeriodoDto;
import br.gov.caixa.simulaicaixa.dto.TelemetriaServicoDto;
import br.gov.caixa.simulaicaixa.service.TelemetriaService;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class TelemetriaResourceTest {

    @InjectMock
    TelemetriaService telemetriaService;

    @InjectMock
    ColetorTelemetria coletorTelemetria;

    @Test
    @TestSecurity(user = "99999999999", roles = {"admin"})
    void deveObterTelemetriaGeral() {
        TelemetriaServicoDto servico = new TelemetriaServicoDto(
                "simular-investimento",
                10L,
                120L
        );

        TelemetriaPeriodoDto periodo = new TelemetriaPeriodoDto(
                LocalDate.parse("2025-01-01"),
                LocalDate.parse("2025-01-31")
        );

        TelemetriaDto telemetria = new TelemetriaDto(
                List.of(servico),
                periodo
        );

        when(telemetriaService.obterTelemetriaGeral())
                .thenReturn(telemetria);

        given()
                .accept(ContentType.JSON)
                .when()
                .get("/telemetria")
                .then()
                .statusCode(200)
                .body("servicos", hasSize(1))
                .body("servicos[0].nome", equalTo("simular-investimento"))
                .body("periodo.inicio", equalTo("2025-01-01"));

        verify(telemetriaService).obterTelemetriaGeral();
        verify(coletorTelemetria).registrarChamada(eq("telemetria"), anyLong());
    }

    @Test
    @TestSecurity(user = "99999999999", roles = {"admin"})
    void deveObterResumoPorPeriodo() {
        LocalDate inicio = LocalDate.parse("2025-01-01");
        LocalDate fim = LocalDate.parse("2025-01-31");

        TelemetriaServicoDto servico = new TelemetriaServicoDto(
                "simular-investimento",
                5L,
                130L
        );

        TelemetriaPeriodoDto periodo = new TelemetriaPeriodoDto(inicio, fim);

        TelemetriaDto telemetria = new TelemetriaDto(
                List.of(servico),
                periodo
        );

        when(telemetriaService.obterResumoPorPeriodo(inicio, fim))
                .thenReturn(telemetria);

        given()
                .accept(ContentType.JSON)
                .when()
                .get("/telemetria/resumo?inicio=2025-01-01&fim=2025-01-31")
                .then()
                .statusCode(200)
                .body("servicos", hasSize(1))
                .body("servicos[0].nome", equalTo("simular-investimento"))
                .body("periodo.fim", equalTo("2025-01-31"));

        verify(telemetriaService).obterResumoPorPeriodo(inicio, fim);
        verify(coletorTelemetria).registrarChamada(eq("telemetria-resumo"), anyLong());
    }
}