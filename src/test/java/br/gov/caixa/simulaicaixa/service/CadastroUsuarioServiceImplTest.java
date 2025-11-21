package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import br.gov.caixa.simulaicaixa.data.entity.UsuarioAutenticacaoEntity;
import br.gov.caixa.simulaicaixa.data.repository.UsuarioAutenticacaoRepository;
import br.gov.caixa.simulaicaixa.dto.RequisicaoCadastroUsuarioDto;
import br.gov.caixa.simulaicaixa.dto.RespostaCadastroUsuarioDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CadastroUsuarioServiceImplTest {

    private UsuarioAutenticacaoRepository usuarioAutenticacaoRepository;
    private CadastroUsuarioServiceImpl cadastroUsuarioService;

    @BeforeEach
    void setUp() {
        usuarioAutenticacaoRepository = mock(UsuarioAutenticacaoRepository.class);
        cadastroUsuarioService = new CadastroUsuarioServiceImpl(usuarioAutenticacaoRepository);
    }

    @Test
    void deveCadastrarUsuarioComSucessoUsandoPerfilPadraoCliente() {
        RequisicaoCadastroUsuarioDto requisicao = new RequisicaoCadastroUsuarioDto(
                "12345678910",
                "senha-segura",
                null,
                123L
        );

        when(usuarioAutenticacaoRepository.buscarPorCpf("12345678910"))
                .thenReturn(null);

        when(usuarioAutenticacaoRepository.salvar(any(UsuarioAutenticacaoEntity.class)))
                .thenAnswer(invocation -> {
                    UsuarioAutenticacaoEntity u = invocation.getArgument(0);
                    u.setId(10L);
                    return u;
                });

        RespostaCadastroUsuarioDto resposta = cadastroUsuarioService.cadastrarUsuario(requisicao);

        ArgumentCaptor<UsuarioAutenticacaoEntity> captor =
                ArgumentCaptor.forClass(UsuarioAutenticacaoEntity.class);
        verify(usuarioAutenticacaoRepository).salvar(captor.capture());
        UsuarioAutenticacaoEntity salvo = captor.getValue();

        assertEquals("12345678910", salvo.getCpf());
        assertEquals("cliente", salvo.getGrupos());
        assertEquals(123L, salvo.getClienteId());
        assertNotNull(salvo.getSenhaHash());
        assertNotEquals("senha-segura", salvo.getSenhaHash());

        assertEquals(10L, resposta.id());
        assertEquals("12345678910", resposta.cpf());
        assertEquals("cliente", resposta.grupos());
        assertEquals(123L, resposta.clienteId());
    }

    @Test
    void deveLancarErroQuandoRequisicaoForNula() {
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> cadastroUsuarioService.cadastrarUsuario(null)
        );

        assertEquals(CodigoErroNegocio.USUARIO_DADOS_INVALIDOS, ex.getCodigoErro());
    }

    @Test
    void deveLancarErroQuandoCpfOuSenhaForemInvalidos() {
        RequisicaoCadastroUsuarioDto requisicao = new RequisicaoCadastroUsuarioDto(
                "   ",
                "",
                "cliente",
                null
        );

        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> cadastroUsuarioService.cadastrarUsuario(requisicao)
        );

        assertEquals(CodigoErroNegocio.USUARIO_DADOS_INVALIDOS, ex.getCodigoErro());
    }

    @Test
    void deveLancarErroQuandoUsuarioJaExistirParaCpf() {
        RequisicaoCadastroUsuarioDto requisicao = new RequisicaoCadastroUsuarioDto(
                "12345678910",
                "senha",
                "cliente",
                null
        );

        when(usuarioAutenticacaoRepository.buscarPorCpf("12345678910"))
                .thenReturn(new UsuarioAutenticacaoEntity());

        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> cadastroUsuarioService.cadastrarUsuario(requisicao)
        );

        assertEquals(CodigoErroNegocio.USUARIO_JA_EXISTE, ex.getCodigoErro());
    }

    @Test
    void deveRespeitarGruposQuandoInformados() {
        RequisicaoCadastroUsuarioDto requisicao = new RequisicaoCadastroUsuarioDto(
                "12345678910",
                "senha",
                "cliente,admin",
                null
        );

        when(usuarioAutenticacaoRepository.buscarPorCpf("12345678910"))
                .thenReturn(null);

        when(usuarioAutenticacaoRepository.salvar(any(UsuarioAutenticacaoEntity.class)))
                .thenAnswer(invocation -> {
                    UsuarioAutenticacaoEntity u = invocation.getArgument(0);
                    u.setId(20L);
                    return u;
                });

        RespostaCadastroUsuarioDto resposta = cadastroUsuarioService.cadastrarUsuario(requisicao);

        ArgumentCaptor<UsuarioAutenticacaoEntity> captor =
                ArgumentCaptor.forClass(UsuarioAutenticacaoEntity.class);
        verify(usuarioAutenticacaoRepository).salvar(captor.capture());
        UsuarioAutenticacaoEntity salvo = captor.getValue();

        assertEquals("cliente,admin", salvo.getGrupos());
        assertEquals(20L, resposta.id());
    }
}