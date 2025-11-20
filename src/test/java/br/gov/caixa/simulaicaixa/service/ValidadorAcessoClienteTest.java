package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import br.gov.caixa.simulaicaixa.core.seguranca.ValidadorAcessoCliente;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidadorAcessoClienteTest {

    private ContextoClienteService contextoClienteService;
    private JsonWebToken jwt;
    private ValidadorAcessoCliente validadorAcessoCliente;

    @BeforeEach
    void setUp() {
        contextoClienteService = mock(ContextoClienteService.class);
        jwt = mock(JsonWebToken.class);
        validadorAcessoCliente = new ValidadorAcessoCliente(contextoClienteService, jwt);
    }

    @Test
    void devePermitirAcessoQuandoUsuarioForAdmin() {
        when(jwt.getGroups()).thenReturn(Set.of("admin"));

        validadorAcessoCliente.validarClienteOuAdmin(
                123L,
                CodigoErroNegocio.OPERACAO_NAO_PERMITIDA_PARA_INVESTIMENTOS
        );

        verifyNoInteractions(contextoClienteService);
    }

    @Test
    void devePermitirAcessoQuandoClienteIdDoTokenForIgualAoPath() {
        when(jwt.getGroups()).thenReturn(Set.of("cliente"));
        when(contextoClienteService.obterClienteIdUsuarioObrigatorio())
                .thenReturn(123L);

        validadorAcessoCliente.validarClienteOuAdmin(
                123L,
                CodigoErroNegocio.OPERACAO_NAO_PERMITIDA_PARA_INVESTIMENTOS
        );

        verify(contextoClienteService).obterClienteIdUsuarioObrigatorio();
    }

    @Test
    void deveLancarNegocioExceptionQuandoClienteIdForDiferente() {
        when(jwt.getGroups()).thenReturn(Set.of("cliente"));
        when(contextoClienteService.obterClienteIdUsuarioObrigatorio())
                .thenReturn(999L);

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> validadorAcessoCliente.validarClienteOuAdmin(
                        123L,
                        CodigoErroNegocio.OPERACAO_NAO_PERMITIDA_PARA_INVESTIMENTOS
                )
        );

        assertEquals(
                CodigoErroNegocio.OPERACAO_NAO_PERMITIDA_PARA_INVESTIMENTOS,
                excecao.getCodigoErro()
        );
        verify(contextoClienteService).obterClienteIdUsuarioObrigatorio();
    }
}