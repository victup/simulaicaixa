package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import br.gov.caixa.simulaicaixa.data.repository.InvestimentoRepository;
import br.gov.caixa.simulaicaixa.domain.Investimento;
import br.gov.caixa.simulaicaixa.domain.enums.TipoInvestimentoEnum;
import br.gov.caixa.simulaicaixa.dto.InvestimentoHistoricoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvestimentoServiceImplTest {

    private InvestimentoRepository investimentoRepository;
    private InvestimentoServiceImpl investimentoService;

    @BeforeEach
    void setUp() {
        investimentoRepository = mock(InvestimentoRepository.class);
        investimentoService = new InvestimentoServiceImpl(investimentoRepository);
    }

    @Test
    void deveLancarNegocioExceptionQuandoHistoricoForNulo() {
        Long clienteId = 123L;

        when(investimentoRepository.listarPorClienteId(clienteId)).thenReturn(null);

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> investimentoService.listarPorClienteId(clienteId)
        );

        assertEquals(
                CodigoErroNegocio.CLIENTE_SEM_HISTORICO_INVESTIMENTOS,
                excecao.getCodigoErro()
        );
    }

    @Test
    void deveLancarNegocioExceptionQuandoHistoricoForVazio() {
        Long clienteId = 123L;

        when(investimentoRepository.listarPorClienteId(clienteId)).thenReturn(List.of());

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> investimentoService.listarPorClienteId(clienteId)
        );

        assertEquals(
                CodigoErroNegocio.CLIENTE_SEM_HISTORICO_INVESTIMENTOS,
                excecao.getCodigoErro()
        );
    }

    @Test
    void deveMapearInvestimentosParaDtoCorretamente() {
        Long clienteId = 123L;

        Investimento investimento1 = mock(Investimento.class);
        when(investimento1.getId()).thenReturn(1L);
        when(investimento1.getTipo()).thenReturn(TipoInvestimentoEnum.CDB);
        when(investimento1.getValor()).thenReturn(new BigDecimal("1000.00"));
        when(investimento1.getRentabilidade()).thenReturn(new BigDecimal("0.12"));
        when(investimento1.getData()).thenReturn(LocalDate.parse("2025-01-01"));

        Investimento investimento2 = mock(Investimento.class);
        when(investimento2.getId()).thenReturn(2L);
        when(investimento2.getTipo()).thenReturn(TipoInvestimentoEnum.FUNDO);
        when(investimento2.getValor()).thenReturn(new BigDecimal("2000.00"));
        when(investimento2.getRentabilidade()).thenReturn(new BigDecimal("0.18"));
        when(investimento2.getData()).thenReturn(LocalDate.parse("2025-01-02"));

        when(investimentoRepository.listarPorClienteId(clienteId))
                .thenReturn(List.of(investimento1, investimento2));

        List<InvestimentoHistoricoDto> resultado =
                investimentoService.listarPorClienteId(clienteId);

        verify(investimentoRepository).listarPorClienteId(clienteId);
        assertEquals(2, resultado.size());

        InvestimentoHistoricoDto dto1 = resultado.getFirst();
        assertEquals(1L, dto1.id());
        assertEquals(TipoInvestimentoEnum.CDB.getDescricao(), dto1.tipo());
        assertEquals(new BigDecimal("1000.00"), dto1.valor());
        assertEquals(new BigDecimal("0.12"), dto1.rentabilidade());
        assertEquals(LocalDate.parse("2025-01-01"), dto1.data());

        InvestimentoHistoricoDto dto2 = resultado.get(1);
        assertEquals(2L, dto2.id());
        assertEquals(TipoInvestimentoEnum.FUNDO.getDescricao(), dto2.tipo());
        assertEquals(new BigDecimal("2000.00"), dto2.valor());
        assertEquals(new BigDecimal("0.18"), dto2.rentabilidade());
        assertEquals(LocalDate.parse("2025-01-02"), dto2.data());
    }
}