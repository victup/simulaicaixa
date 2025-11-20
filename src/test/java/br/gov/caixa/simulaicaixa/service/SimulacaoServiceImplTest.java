package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import br.gov.caixa.simulaicaixa.data.repository.SimulacaoInvestimentoRepository;
import br.gov.caixa.simulaicaixa.domain.SimulacaoInvestimento;
import br.gov.caixa.simulaicaixa.domain.enums.NivelRiscoProdutoEnum;
import br.gov.caixa.simulaicaixa.domain.enums.TipoInvestimentoEnum;
import br.gov.caixa.simulaicaixa.dto.RespostaSimulacaoDto;
import br.gov.caixa.simulaicaixa.dto.SimulacaoHistoricoDto;
import br.gov.caixa.simulaicaixa.dto.SimulacaoPorProdutoDiaDto;
import br.gov.caixa.simulaicaixa.dto.SolicitacaoSimulacaoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimulacaoServiceImplTest {

    private SimulacaoInvestimentoRepository simulacaoInvestimentoRepository;
    private ContextoClienteService contextoClienteService;
    private SimulacaoServiceImpl simulacaoService;

    @BeforeEach
    void setUp() {
        simulacaoInvestimentoRepository = mock(SimulacaoInvestimentoRepository.class);
        contextoClienteService = mock(ContextoClienteService.class);
        simulacaoService = new SimulacaoServiceImpl(simulacaoInvestimentoRepository, contextoClienteService);
    }

    @Test
    void deveSimularInvestimentoParaClienteUsandoClienteDoToken() {
        when(contextoClienteService.usuarioAtualEhAdmin()).thenReturn(false);
        when(contextoClienteService.obterClienteIdUsuarioObrigatorio()).thenReturn(999L);

        when(simulacaoInvestimentoRepository.salvar(any(SimulacaoInvestimento.class)))
                .thenAnswer(invocation -> {
                    SimulacaoInvestimento simulacao = invocation.getArgument(0);
                    simulacao.setId(1L);
                    simulacao.setDataSimulacao(OffsetDateTime.parse("2025-01-01T10:00:00Z"));
                    return simulacao;
                });

        SolicitacaoSimulacaoDto solicitacao = new SolicitacaoSimulacaoDto(
                123L,
                new BigDecimal("10000.00"),
                12,
                TipoInvestimentoEnum.CDB.getDescricao()
        );

        RespostaSimulacaoDto resposta = simulacaoService.simularInvestimento(solicitacao);

        ArgumentCaptor<SimulacaoInvestimento> captor = ArgumentCaptor.forClass(SimulacaoInvestimento.class);
        verify(simulacaoInvestimentoRepository).salvar(captor.capture());
        SimulacaoInvestimento simulacaoEnviada = captor.getValue();

        assertEquals(999L, simulacaoEnviada.getClienteId());
        assertEquals(new BigDecimal("11200.00"), resposta.resultadoSimulacao().valorFinal());
        assertEquals(new BigDecimal("0.12"), resposta.resultadoSimulacao().rentabilidadeEfetiva());
        assertEquals(TipoInvestimentoEnum.CDB.getDescricao(), resposta.produtoValidado().tipo());
        assertEquals(NivelRiscoProdutoEnum.BAIXO.getDescricao(), resposta.produtoValidado().risco());
    }

    @Test
    void deveSimularInvestimentoParaAdminUsandoClienteDoPayload() {
        when(contextoClienteService.usuarioAtualEhAdmin()).thenReturn(true);

        when(simulacaoInvestimentoRepository.salvar(any(SimulacaoInvestimento.class)))
                .thenAnswer(invocation -> {
                    SimulacaoInvestimento simulacao = invocation.getArgument(0);
                    simulacao.setId(10L);
                    simulacao.setDataSimulacao(OffsetDateTime.parse("2025-02-01T10:00:00Z"));
                    return simulacao;
                });

        SolicitacaoSimulacaoDto solicitacao = new SolicitacaoSimulacaoDto(
                321L,
                new BigDecimal("5000.00"),
                6,
                TipoInvestimentoEnum.FUNDO.getDescricao()
        );

        RespostaSimulacaoDto resposta = simulacaoService.simularInvestimento(solicitacao);

        ArgumentCaptor<SimulacaoInvestimento> captor = ArgumentCaptor.forClass(SimulacaoInvestimento.class);
        verify(simulacaoInvestimentoRepository).salvar(captor.capture());
        SimulacaoInvestimento simulacaoEnviada = captor.getValue();

        assertEquals(321L, simulacaoEnviada.getClienteId());
        assertEquals(TipoInvestimentoEnum.FUNDO.getDescricao(), resposta.produtoValidado().tipo());
        verify(contextoClienteService, never()).obterClienteIdUsuarioObrigatorio();
    }

    @Test
    void deveLancarErroQuandoSolicitacaoForNula() {
        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> simulacaoService.simularInvestimento(null)
        );

        assertEquals(CodigoErroNegocio.SIMULACAO_DADOS_INVALIDOS, excecao.getCodigoErro());
    }

    @Test
    void deveLancarErroQuandoValorMenorOuIgualZero() {
        SolicitacaoSimulacaoDto solicitacao = new SolicitacaoSimulacaoDto(
                123L,
                BigDecimal.ZERO,
                12,
                TipoInvestimentoEnum.CDB.getDescricao()
        );

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> simulacaoService.simularInvestimento(solicitacao)
        );

        assertEquals(CodigoErroNegocio.SIMULACAO_DADOS_INVALIDOS, excecao.getCodigoErro());
    }

    @Test
    void deveLancarErroQuandoPrazoMenorOuIgualZero() {
        SolicitacaoSimulacaoDto solicitacao = new SolicitacaoSimulacaoDto(
                123L,
                new BigDecimal("1000.00"),
                0,
                TipoInvestimentoEnum.CDB.getDescricao()
        );

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> simulacaoService.simularInvestimento(solicitacao)
        );

        assertEquals(CodigoErroNegocio.SIMULACAO_DADOS_INVALIDOS, excecao.getCodigoErro());
    }

    @Test
    void deveLancarErroQuandoTipoProdutoNaoInformado() {
        SolicitacaoSimulacaoDto solicitacao = new SolicitacaoSimulacaoDto(
                123L,
                new BigDecimal("1000.00"),
                12,
                " "
        );

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> simulacaoService.simularInvestimento(solicitacao)
        );

        assertEquals(CodigoErroNegocio.SIMULACAO_DADOS_INVALIDOS, excecao.getCodigoErro());
    }

    @Test
    void deveListarSimulacoesMapeandoCamposCorretamente() {
        SimulacaoInvestimento simulacao = new SimulacaoInvestimento();
        simulacao.setId(1L);
        simulacao.setClienteId(123L);
        simulacao.setNomeProduto("Produto CDB");
        simulacao.setTipoProduto(TipoInvestimentoEnum.CDB.getDescricao());
        simulacao.setValorInvestido(new BigDecimal("10000.00"));
        simulacao.setValorFinal(new BigDecimal("11200.00"));
        simulacao.setPrazoMeses(12);
        simulacao.setDataSimulacao(OffsetDateTime.parse("2025-01-01T10:00:00Z"));

        when(simulacaoInvestimentoRepository.listarTodas())
                .thenReturn(List.of(simulacao));

        List<SimulacaoHistoricoDto> resposta = simulacaoService.listarSimulacoes();

        assertEquals(1, resposta.size());
        SimulacaoHistoricoDto dto = resposta.getFirst();
        assertEquals(1L, dto.id());
        assertEquals(123L, dto.clienteId());
        assertEquals("Produto CDB", dto.produto());
        assertEquals(new BigDecimal("10000.00"), dto.valorInvestido());
        assertEquals(new BigDecimal("11200.00"), dto.valorFinal());
        assertEquals(12, dto.prazoMeses());
    }

    @Test
    void deveListarSimulacoesAgrupadasPorProdutoEDia() {
        SimulacaoInvestimento s1 = new SimulacaoInvestimento();
        s1.setNomeProduto("Produto CDB");
        s1.setValorFinal(new BigDecimal("11000.00"));
        s1.setDataSimulacao(OffsetDateTime.parse("2025-01-01T10:00:00Z"));

        SimulacaoInvestimento s2 = new SimulacaoInvestimento();
        s2.setNomeProduto("Produto CDB");
        s2.setValorFinal(new BigDecimal("12000.00"));
        s2.setDataSimulacao(OffsetDateTime.parse("2025-01-01T15:00:00Z"));

        SimulacaoInvestimento s3 = new SimulacaoInvestimento();
        s3.setNomeProduto("Produto Fundo");
        s3.setValorFinal(new BigDecimal("9000.00"));
        s3.setDataSimulacao(OffsetDateTime.parse("2025-01-02T10:00:00Z"));

        SimulacaoInvestimento s4 = new SimulacaoInvestimento();
        s4.setNomeProduto("Produto Ignorado");
        s4.setValorFinal(new BigDecimal("5000.00"));
        s4.setDataSimulacao(null);

        when(simulacaoInvestimentoRepository.listarTodas())
                .thenReturn(List.of(s1, s2, s3, s4));

        List<SimulacaoPorProdutoDiaDto> resposta =
                simulacaoService.listarSimulacoesPorProdutoEDia();

        assertEquals(2, resposta.size());

        SimulacaoPorProdutoDiaDto cdbDia =
                resposta.stream()
                        .filter(r -> r.produto().equals("Produto CDB")
                                && r.data().equals(LocalDate.parse("2025-01-01")))
                        .findFirst()
                        .orElseThrow();

        assertEquals(2L, cdbDia.quantidadeSimulacoes());
        assertEquals(new BigDecimal("11500.00"), cdbDia.mediaValorFinal());

        boolean existeOutroProduto =
                resposta.stream().anyMatch(r -> r.produto().equals("Produto Fundo"));
        assertTrue(existeOutroProduto);
    }
}