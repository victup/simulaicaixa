package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.seguranca.ValidadorAcessoCliente;
import br.gov.caixa.simulaicaixa.dto.PerfilRiscoDto;
import br.gov.caixa.simulaicaixa.service.ContextoClienteService;
import br.gov.caixa.simulaicaixa.service.PerfilRiscoService;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class PerfilRiscoResourceTest {

    @InjectMock
    PerfilRiscoService perfilRiscoService;

    @InjectMock
    ColetorTelemetria coletorTelemetria;

    @InjectMock
    ContextoClienteService contextoClienteService;

    @InjectMock
    ValidadorAcessoCliente validadorAcessoCliente;

    @Test
    @TestSecurity(user = "12345678901", roles = {"cliente"})
    void deveObterPerfilRiscoPorCliente() {
        Long clienteId = 123L;

        PerfilRiscoDto perfil = new PerfilRiscoDto(
                clienteId,
                "Moderado",
                65,
                "Perfil equilibrado"
        );

        when(perfilRiscoService.obterPorClienteId(clienteId))
                .thenReturn(perfil);

        given()
                .accept(ContentType.JSON)
                .when()
                .get("/perfil-risco/" + clienteId)
                .then()
                .statusCode(200)
                .body("clienteId", equalTo(123))
                .body("perfil", equalTo("Moderado"));

        verify(validadorAcessoCliente)
                .validarClienteOuAdmin(clienteId, CodigoErroNegocio.OPERACAO_NAO_PERMITIDA_PARA_PERFIL);
        verify(perfilRiscoService).obterPorClienteId(clienteId);
        verify(coletorTelemetria).registrarChamada(eq("perfil-risco"), anyLong());
    }

    @Test
    @TestSecurity(user = "12345678901", roles = {"cliente"})
    void deveObterPerfilRiscoDoClienteAutenticado() {
        Long clienteId = 123L;

        PerfilRiscoDto perfil = new PerfilRiscoDto(
                clienteId,
                "Moderado",
                65,
                "Perfil equilibrado"
        );

        when(contextoClienteService.obterClienteIdUsuarioObrigatorio())
                .thenReturn(clienteId);
        when(perfilRiscoService.obterPorClienteId(clienteId))
                .thenReturn(perfil);

        given()
                .accept(ContentType.JSON)
                .when()
                .get("/perfil-risco/usuario-logado")
                .then()
                .statusCode(200)
                .body("clienteId", equalTo(123));

        verify(contextoClienteService).obterClienteIdUsuarioObrigatorio();
        verify(perfilRiscoService).obterPorClienteId(clienteId);
    }
}