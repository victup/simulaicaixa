package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.dto.RequisicaoLoginDto;
import br.gov.caixa.simulaicaixa.dto.RespostaLoginDto;
import br.gov.caixa.simulaicaixa.service.AutenticacaoService;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class AutenticacaoResourceTest {

    @InjectMock
    AutenticacaoService autenticacaoService;

    @InjectMock
    ColetorTelemetria coletorTelemetria;

    @Test
    void deveRealizarLoginComSucesso() {
        RequisicaoLoginDto requisicao = new RequisicaoLoginDto("12345678901", "123456");
        RespostaLoginDto resposta = new RespostaLoginDto("token-jwt", "Bearer", 3600L);

        when(autenticacaoService.autenticar(any(RequisicaoLoginDto.class)))
                .thenReturn(resposta);

        given()
                .contentType(ContentType.JSON)
                .body(requisicao)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("tipoToken", equalTo("Bearer"));

        verify(autenticacaoService).autenticar(any(RequisicaoLoginDto.class));
        verify(coletorTelemetria).registrarChamada(eq("auth-login"), anyLong());
    }
}