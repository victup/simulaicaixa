package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import br.gov.caixa.simulaicaixa.data.entity.UsuarioAutenticacaoEntity;
import br.gov.caixa.simulaicaixa.data.repository.UsuarioAutenticacaoRepository;
import br.gov.caixa.simulaicaixa.dto.RequisicaoLoginDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AutenticacaoServiceImplTest {

    private UsuarioAutenticacaoRepository usuarioAutenticacaoRepository;
    private AutenticacaoServiceImpl autenticacaoService;

    @BeforeEach
    void setUp() {
        usuarioAutenticacaoRepository = mock(UsuarioAutenticacaoRepository.class);
        autenticacaoService = new AutenticacaoServiceImpl(usuarioAutenticacaoRepository);
    }

    @Test
    void deveLancarErroQuandoRequisicaoForNula() {
        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> autenticacaoService.autenticar(null)
        );

        assertEquals(CodigoErroNegocio.CREDENCIAIS_INVALIDAS, excecao.getCodigoErro());
        verifyNoInteractions(usuarioAutenticacaoRepository);
    }

    @Test
    void deveLancarErroQuandoCpfOuSenhaForemBrancos() {
        RequisicaoLoginDto requisicaoComCpfVazio = new RequisicaoLoginDto(" ", "123456");

        NegocioException excecaoCpf = assertThrows(
                NegocioException.class,
                () -> autenticacaoService.autenticar(requisicaoComCpfVazio)
        );
        assertEquals(CodigoErroNegocio.CREDENCIAIS_INVALIDAS, excecaoCpf.getCodigoErro());
        verifyNoInteractions(usuarioAutenticacaoRepository);

        RequisicaoLoginDto requisicaoComSenhaVazia = new RequisicaoLoginDto("12345678901", " ");

        NegocioException excecaoSenha = assertThrows(
                NegocioException.class,
                () -> autenticacaoService.autenticar(requisicaoComSenhaVazia)
        );
        assertEquals(CodigoErroNegocio.CREDENCIAIS_INVALIDAS, excecaoSenha.getCodigoErro());
        verifyNoInteractions(usuarioAutenticacaoRepository);
    }

    @Test
    void deveLancarErroQuandoUsuarioNaoForEncontrado() {
        RequisicaoLoginDto requisicao = new RequisicaoLoginDto("12345678901", "123456");

        when(usuarioAutenticacaoRepository.buscarPorCpf("12345678901"))
                .thenReturn(null);

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> autenticacaoService.autenticar(requisicao)
        );

        assertEquals(CodigoErroNegocio.CREDENCIAIS_INVALIDAS, excecao.getCodigoErro());
        verify(usuarioAutenticacaoRepository).buscarPorCpf("12345678901");
    }

    @Test
    void deveLancarErroQuandoSenhaForInvalida() {
        RequisicaoLoginDto requisicao = new RequisicaoLoginDto("12345678901", "senhaErrada");

        UsuarioAutenticacaoEntity usuario = mock(UsuarioAutenticacaoEntity.class);
        String hashSenhaValida = BCrypt.hashpw("senhaValida", BCrypt.gensalt());

        when(usuario.getSenhaHash()).thenReturn(hashSenhaValida);
        when(usuario.getGrupos()).thenReturn("cliente");
        when(usuarioAutenticacaoRepository.buscarPorCpf("12345678901"))
                .thenReturn(usuario);

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> autenticacaoService.autenticar(requisicao)
        );

        assertEquals(CodigoErroNegocio.CREDENCIAIS_INVALIDAS, excecao.getCodigoErro());
        verify(usuarioAutenticacaoRepository).buscarPorCpf("12345678901");
    }

    @Test
    void deveLancarErroQuandoUsuarioNaoTiverGrupoValido() {
        RequisicaoLoginDto requisicao = new RequisicaoLoginDto("12345678901", "senhaValida");

        UsuarioAutenticacaoEntity usuario = mock(UsuarioAutenticacaoEntity.class);
        String hashSenhaValida = BCrypt.hashpw("senhaValida", BCrypt.gensalt());

        when(usuario.getSenhaHash()).thenReturn(hashSenhaValida);
        when(usuario.getGrupos()).thenReturn("invalido1,invalido2");
        when(usuarioAutenticacaoRepository.buscarPorCpf("12345678901"))
                .thenReturn(usuario);

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> autenticacaoService.autenticar(requisicao)
        );

        assertEquals(CodigoErroNegocio.USUARIO_SEM_GRUPO_ACESSO, excecao.getCodigoErro());
        verify(usuarioAutenticacaoRepository).buscarPorCpf("12345678901");
    }
}