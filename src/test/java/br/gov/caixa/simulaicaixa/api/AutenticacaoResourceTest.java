package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.dto.RequisicaoCadastroUsuarioDto;
import br.gov.caixa.simulaicaixa.dto.RequisicaoLoginDto;
import br.gov.caixa.simulaicaixa.dto.RespostaCadastroUsuarioDto;
import br.gov.caixa.simulaicaixa.dto.RespostaLoginDto;
import br.gov.caixa.simulaicaixa.service.AutenticacaoService;
import br.gov.caixa.simulaicaixa.service.CadastroUsuarioService;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class AutenticacaoResourceTest {

    @InjectMock
    AutenticacaoService autenticacaoService;

    @InjectMock
    ColetorTelemetria coletorTelemetria;

    @InjectMock
    CadastroUsuarioService cadastroUsuarioService;

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

    @Test
    @TestSecurity(user = "99999999999", roles = {"admin"})
    void deveCadastrarUsuarioNovoComSucesso() {
        RequisicaoCadastroUsuarioDto requisicao = new RequisicaoCadastroUsuarioDto(
                "12345678910",
                "senha-segura",
                "cliente",
                123L
        );

        RespostaCadastroUsuarioDto resposta = new RespostaCadastroUsuarioDto(
                10L,
                "12345678910",
                "cliente",
                123L
        );

        when(cadastroUsuarioService.cadastrarUsuario(any(RequisicaoCadastroUsuarioDto.class)))
                .thenReturn(resposta);

        given()
                .contentType(ContentType.JSON)
                .body(requisicao)
                .when()
                .post("/auth/usuarios/novo")
                .then()
                .statusCode(200)
                .body("id", equalTo(10))
                .body("cpf", equalTo("12345678910"))
                .body("grupos", equalTo("cliente"))
                .body("clienteId", equalTo(123));
    }

    @Test
    @TestSecurity(user = "12345678901", roles = {"cliente"})
    void deveNegarCadastroUsuarioParaUsuarioNaoAdmin() {
        RequisicaoCadastroUsuarioDto requisicao = new RequisicaoCadastroUsuarioDto(
                "12345678910",
                "senha",
                "cliente",
                null
        );

        given()
                .contentType(ContentType.JSON)
                .body(requisicao)
                .when()
                .post("/auth/usuarios/novo")
                .then()
                .statusCode(403);

        verifyNoInteractions(cadastroUsuarioService);
    }
}