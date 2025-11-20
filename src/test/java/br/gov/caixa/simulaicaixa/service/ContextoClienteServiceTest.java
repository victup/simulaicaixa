package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import br.gov.caixa.simulaicaixa.data.entity.UsuarioAutenticacaoEntity;
import br.gov.caixa.simulaicaixa.data.repository.UsuarioAutenticacaoRepository;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContextoClienteServiceTest {

    @Mock
    JsonWebToken jwt;

    @Mock
    UsuarioAutenticacaoRepository usuarioAutenticacaoRepository;

    @InjectMocks
    ContextoClienteService contextoClienteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveRetornarClienteIdQuandoUsuarioForValido() {
        when(jwt.getClaim("cpf")).thenReturn("12345678901");

        UsuarioAutenticacaoEntity usuario = mock(UsuarioAutenticacaoEntity.class);
        when(usuario.getClienteId()).thenReturn(123L);
        when(usuarioAutenticacaoRepository.buscarPorCpf("12345678901"))
                .thenReturn(usuario);

        Long clienteId = contextoClienteService.obterClienteId();

        assertEquals(123L, clienteId);
        verify(usuarioAutenticacaoRepository).buscarPorCpf("12345678901");
    }

    @Test
    void deveRetornarNullQuandoCpfNaoEstiverNoToken() {
        when(jwt.getClaim("cpf")).thenReturn(null);
        when(jwt.getSubject()).thenReturn(null);

        Long clienteId = contextoClienteService.obterClienteId();

        assertNull(clienteId);
        verifyNoInteractions(usuarioAutenticacaoRepository);
    }

    @Test
    void deveRetornarNullQuandoUsuarioNaoForEncontrado() {
        when(jwt.getClaim("cpf")).thenReturn("12345678901");
        when(usuarioAutenticacaoRepository.buscarPorCpf("12345678901"))
                .thenReturn(null);

        Long clienteId = contextoClienteService.obterClienteId();

        assertNull(clienteId);
        verify(usuarioAutenticacaoRepository).buscarPorCpf("12345678901");
    }

    @Test
    void deveLancarNegocioExceptionQuandoClienteNaoEstiverVinculadoAoUsuario() {
        when(jwt.getClaim("cpf")).thenReturn("12345678901");
        when(usuarioAutenticacaoRepository.buscarPorCpf("12345678901"))
                .thenReturn(null);

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> contextoClienteService.obterClienteIdUsuarioObrigatorio()
        );

        assertEquals(CodigoErroNegocio.CLIENTE_NAO_ENCONTRADO, excecao.getCodigoErro());
        verify(usuarioAutenticacaoRepository).buscarPorCpf("12345678901");
    }

    @Test
    void deveIdentificarUsuarioAdminQuandoGrupoContemAdmin() {
        when(jwt.getGroups()).thenReturn(Set.of("cliente", "admin"));

        boolean resultado = contextoClienteService.usuarioAtualEhAdmin();

        assertTrue(resultado);
    }

    @Test
    void naoDeveIdentificarUsuarioAdminQuandoGrupoNaoContemAdmin() {
        when(jwt.getGroups()).thenReturn(Set.of("cliente"));

        boolean resultado = contextoClienteService.usuarioAtualEhAdmin();

        assertFalse(resultado);
    }

    @Test
    void deveObterCpfAPartirDoSubjectQuandoClaimCpfEstiverVazio() {
        when(jwt.getClaim("cpf")).thenReturn(" ");
        when(jwt.getSubject()).thenReturn("99999999999");

        String cpf = contextoClienteService.obterCpfUsuarioAtual();

        assertEquals("99999999999", cpf);
    }
}