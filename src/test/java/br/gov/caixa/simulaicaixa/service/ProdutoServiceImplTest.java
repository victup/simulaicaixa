package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import br.gov.caixa.simulaicaixa.data.repository.InvestimentoRepository;
import br.gov.caixa.simulaicaixa.data.repository.PerfilRiscoRepository;
import br.gov.caixa.simulaicaixa.data.repository.ProdutoRepository;
import br.gov.caixa.simulaicaixa.domain.Investimento;
import br.gov.caixa.simulaicaixa.domain.PerfilRisco;
import br.gov.caixa.simulaicaixa.domain.ProdutoInvestimento;
import br.gov.caixa.simulaicaixa.domain.enums.TipoPerfilRiscoEnum;
import br.gov.caixa.simulaicaixa.dto.ProdutoRecomendadoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProdutoServiceImplTest {

    private ProdutoRepository produtoRepository;
    private InvestimentoRepository investimentoRepository;
    private PerfilRiscoRepository perfilRiscoRepository;
    private ContextoClienteService contextoClienteService;
    private MotorRecomendacaoService motorRecomendacaoService;
    private ProdutoServiceImpl produtoService;

    @BeforeEach
    void setUp() {
        produtoRepository = mock(ProdutoRepository.class);
        investimentoRepository = mock(InvestimentoRepository.class);
        perfilRiscoRepository = mock(PerfilRiscoRepository.class);
        contextoClienteService = mock(ContextoClienteService.class);
        motorRecomendacaoService = mock(MotorRecomendacaoService.class);

        produtoService = new ProdutoServiceImpl(
                produtoRepository,
                investimentoRepository,
                perfilRiscoRepository,
                contextoClienteService,
                motorRecomendacaoService
        );
    }

    @Test
    void deveLancarErroQuandoPerfilInformadoForInvalido() {
        String perfil = "Perfil inválido";

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> produtoService.listarProdutosRecomendadosPorPerfil(perfil)
        );

        assertEquals(CodigoErroNegocio.PERFIL_INVALIDO, excecao.getCodigoErro());
        verifyNoInteractions(produtoRepository, investimentoRepository, perfilRiscoRepository, contextoClienteService, motorRecomendacaoService);
    }

    @Test
    void deveLancarErroQuandoNaoExistiremProdutosParaPerfil() {
        TipoPerfilRiscoEnum tipoPerfil = TipoPerfilRiscoEnum.MODERADO;
        String perfil = tipoPerfil.getDescricao();

        when(produtoRepository.listarPorPerfil(tipoPerfil.getCodigo()))
                .thenReturn(List.of());

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> produtoService.listarProdutosRecomendadosPorPerfil(perfil)
        );

        assertEquals(CodigoErroNegocio.PRODUTOS_NAO_ENCONTRADOS_PARA_PERFIL, excecao.getCodigoErro());
        verify(produtoRepository).listarPorPerfil(tipoPerfil.getCodigo());
        verifyNoInteractions(investimentoRepository, perfilRiscoRepository, contextoClienteService, motorRecomendacaoService);
    }

    @Test
    void deveListarProdutosRecomendadosPorPerfilComSucesso() {
        TipoPerfilRiscoEnum tipoPerfil = TipoPerfilRiscoEnum.MODERADO;
        String perfil = tipoPerfil.getDescricao();

        ProdutoInvestimento produto1 = mock(ProdutoInvestimento.class);
        when(produto1.getId()).thenReturn(1L);
        when(produto1.getNome()).thenReturn("CDB Caixa 2026");
        when(produto1.getTipo()).thenReturn("CDB");
        when(produto1.getRentabilidade()).thenReturn(new BigDecimal("0.12"));
        when(produto1.getRiscoDescricao()).thenReturn("Baixo");

        ProdutoInvestimento produto2 = mock(ProdutoInvestimento.class);
        when(produto2.getId()).thenReturn(2L);
        when(produto2.getNome()).thenReturn("Fundo XPTO");
        when(produto2.getTipo()).thenReturn("Fundo");
        when(produto2.getRentabilidade()).thenReturn(new BigDecimal("0.18"));
        when(produto2.getRiscoDescricao()).thenReturn("Alto");

        List<ProdutoInvestimento> produtosBase = List.of(produto1, produto2);

        when(produtoRepository.listarPorPerfil(tipoPerfil.getCodigo()))
                .thenReturn(produtosBase);

        when(motorRecomendacaoService.recomendarPorPerfil(any(PerfilRisco.class), eq(produtosBase)))
                .thenReturn(List.of(produto2, produto1));

        List<ProdutoRecomendadoDto> recomendacoes =
                produtoService.listarProdutosRecomendadosPorPerfil(perfil);

        assertEquals(2, recomendacoes.size());

        ProdutoRecomendadoDto primeiro = recomendacoes.getFirst();
        assertEquals(2L, primeiro.id());
        assertEquals("Fundo XPTO", primeiro.nome());
        assertEquals("Fundo", primeiro.tipo());
        assertEquals(new BigDecimal("0.18"), primeiro.rentabilidade());
        assertEquals("Alto", primeiro.risco());

        ProdutoRecomendadoDto segundo = recomendacoes.get(1);
        assertEquals(1L, segundo.id());
        assertEquals("CDB Caixa 2026", segundo.nome());
        assertEquals("CDB", segundo.tipo());
        assertEquals(new BigDecimal("0.12"), segundo.rentabilidade());
        assertEquals("Baixo", segundo.risco());

        verify(produtoRepository).listarPorPerfil(tipoPerfil.getCodigo());
        verify(motorRecomendacaoService).recomendarPorPerfil(any(PerfilRisco.class), eq(produtosBase));
    }

    @Test
    void deveLancarErroQuandoClienteAtualNaoPossuirPerfilRisco() {
        Long clienteId = 123L;

        when(contextoClienteService.obterClienteIdUsuarioObrigatorio()).thenReturn(clienteId);
        when(perfilRiscoRepository.obterPorClienteId(clienteId)).thenReturn(null);

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> produtoService.listarProdutosRecomendadosParaClienteAtual()
        );

        assertEquals(CodigoErroNegocio.CLIENTE_SEM_PERFIL_RISCO, excecao.getCodigoErro());
        verify(contextoClienteService).obterClienteIdUsuarioObrigatorio();
        verify(perfilRiscoRepository).obterPorClienteId(clienteId);
        verifyNoInteractions(investimentoRepository, produtoRepository, motorRecomendacaoService);
    }

    @Test
    void deveLancarErroQuandoPerfilRiscoClienteNaoTiverTipoDefinido() {
        Long clienteId = 123L;

        PerfilRisco perfilRisco = mock(PerfilRisco.class);
        when(perfilRisco.getTipoPerfilRisco()).thenReturn(null);

        when(contextoClienteService.obterClienteIdUsuarioObrigatorio()).thenReturn(clienteId);
        when(perfilRiscoRepository.obterPorClienteId(clienteId)).thenReturn(perfilRisco);

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> produtoService.listarProdutosRecomendadosParaClienteAtual()
        );

        assertEquals(CodigoErroNegocio.CLIENTE_SEM_PERFIL_RISCO, excecao.getCodigoErro());
        verify(contextoClienteService).obterClienteIdUsuarioObrigatorio();
        verify(perfilRiscoRepository).obterPorClienteId(clienteId);
        verifyNoInteractions(investimentoRepository, produtoRepository, motorRecomendacaoService);
    }

    @Test
    void deveLancarErroQuandoNaoExistiremProdutosParaPerfilDoCliente() {
        Long clienteId = 123L;
        TipoPerfilRiscoEnum tipoPerfil = TipoPerfilRiscoEnum.CONSERVADOR;

        PerfilRisco perfilRisco = mock(PerfilRisco.class);
        when(perfilRisco.getTipoPerfilRisco()).thenReturn(tipoPerfil);

        when(contextoClienteService.obterClienteIdUsuarioObrigatorio()).thenReturn(clienteId);
        when(perfilRiscoRepository.obterPorClienteId(clienteId)).thenReturn(perfilRisco);
        when(investimentoRepository.listarPorClienteId(clienteId)).thenReturn(List.of());
        when(produtoRepository.listarPorPerfil(tipoPerfil.getCodigo())).thenReturn(List.of());

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> produtoService.listarProdutosRecomendadosParaClienteAtual()
        );

        assertEquals(CodigoErroNegocio.PRODUTOS_NAO_ENCONTRADOS_PARA_PERFIL, excecao.getCodigoErro());
        verify(produtoRepository).listarPorPerfil(tipoPerfil.getCodigo());
        verify(contextoClienteService).obterClienteIdUsuarioObrigatorio();
        verify(perfilRiscoRepository).obterPorClienteId(clienteId);
        verify(investimentoRepository).listarPorClienteId(clienteId);
        verifyNoInteractions(motorRecomendacaoService);
    }

    @Test
    void deveListarProdutosRecomendadosParaClienteAtualComSucesso() {
        Long clienteId = 123L;
        TipoPerfilRiscoEnum tipoPerfil = TipoPerfilRiscoEnum.MODERADO;

        PerfilRisco perfilRisco = mock(PerfilRisco.class);
        when(perfilRisco.getTipoPerfilRisco()).thenReturn(tipoPerfil);

        Investimento investimento = mock(Investimento.class);
        List<Investimento> historico = List.of(investimento);

        ProdutoInvestimento produto1 = mock(ProdutoInvestimento.class);
        when(produto1.getId()).thenReturn(1L);
        when(produto1.getNome()).thenReturn("CDB Caixa Liquidez");
        when(produto1.getTipo()).thenReturn("CDB");
        when(produto1.getRentabilidade()).thenReturn(new BigDecimal("0.10"));
        when(produto1.getRiscoDescricao()).thenReturn("Baixo");

        ProdutoInvestimento produto2 = mock(ProdutoInvestimento.class);
        when(produto2.getId()).thenReturn(2L);
        when(produto2.getNome()).thenReturn("Fundo Multimercado");
        when(produto2.getTipo()).thenReturn("Fundo");
        when(produto2.getRentabilidade()).thenReturn(new BigDecimal("0.15"));
        when(produto2.getRiscoDescricao()).thenReturn("Médio");

        List<ProdutoInvestimento> produtosBase = List.of(produto1, produto2);

        when(contextoClienteService.obterClienteIdUsuarioObrigatorio()).thenReturn(clienteId);
        when(perfilRiscoRepository.obterPorClienteId(clienteId)).thenReturn(perfilRisco);
        when(investimentoRepository.listarPorClienteId(clienteId)).thenReturn(historico);
        when(produtoRepository.listarPorPerfil(tipoPerfil.getCodigo())).thenReturn(produtosBase);

        when(motorRecomendacaoService.recomendarPorPerfilEHistorico(
                eq(perfilRisco),
                eq(produtosBase),
                eq(historico)
        )).thenReturn(List.of(produto2, produto1));

        List<ProdutoRecomendadoDto> recomendacoes =
                produtoService.listarProdutosRecomendadosParaClienteAtual();

        assertEquals(2, recomendacoes.size());

        ProdutoRecomendadoDto primeiro = recomendacoes.getFirst();
        assertEquals(2L, primeiro.id());
        assertEquals("Fundo Multimercado", primeiro.nome());
        assertEquals("Fundo", primeiro.tipo());
        assertEquals(new BigDecimal("0.15"), primeiro.rentabilidade());
        assertEquals("Médio", primeiro.risco());

        ProdutoRecomendadoDto segundo = recomendacoes.get(1);
        assertEquals(1L, segundo.id());
        assertEquals("CDB Caixa Liquidez", segundo.nome());
        assertEquals("CDB", segundo.tipo());
        assertEquals(new BigDecimal("0.10"), segundo.rentabilidade());
        assertEquals("Baixo", segundo.risco());

        verify(contextoClienteService).obterClienteIdUsuarioObrigatorio();
        verify(perfilRiscoRepository).obterPorClienteId(clienteId);
        verify(investimentoRepository).listarPorClienteId(clienteId);
        verify(produtoRepository).listarPorPerfil(tipoPerfil.getCodigo());
        verify(motorRecomendacaoService).recomendarPorPerfilEHistorico(perfilRisco, produtosBase, historico);
    }
}