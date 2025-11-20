package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import br.gov.caixa.simulaicaixa.data.entity.TelemetriaRegistroEntity;
import br.gov.caixa.simulaicaixa.data.repository.TelemetriaRegistroRepository;
import br.gov.caixa.simulaicaixa.domain.Telemetria;
import br.gov.caixa.simulaicaixa.domain.TelemetriaPeriodo;
import br.gov.caixa.simulaicaixa.domain.TelemetriaServico;
import br.gov.caixa.simulaicaixa.dto.TelemetriaDto;
import br.gov.caixa.simulaicaixa.dto.TelemetriaServicoDto;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TelemetriaServiceImplTest {

    private ColetorTelemetria coletorTelemetria;
    private TelemetriaRegistroRepository telemetriaRegistroRepository;
    private TelemetriaServiceImpl telemetriaService;

    @BeforeEach
    void setUp() {
        coletorTelemetria = mock(ColetorTelemetria.class);
        telemetriaRegistroRepository = mock(TelemetriaRegistroRepository.class);
        telemetriaService = new TelemetriaServiceImpl(coletorTelemetria, telemetriaRegistroRepository);
    }

    @Test
    void deveObterTelemetriaGeralComPeriodo() {
        TelemetriaServico servico1 = mock(TelemetriaServico.class);
        when(servico1.getNome()).thenReturn("simular-investimento");
        when(servico1.getQuantidadeChamadas()).thenReturn(10L);
        when(servico1.getMediaTempoRespostaMs()).thenReturn(120L);

        TelemetriaServico servico2 = mock(TelemetriaServico.class);
        when(servico2.getNome()).thenReturn("listar-simulacoes");
        when(servico2.getQuantidadeChamadas()).thenReturn(5L);
        when(servico2.getMediaTempoRespostaMs()).thenReturn(80L);

        TelemetriaPeriodo periodo = mock(TelemetriaPeriodo.class);
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        LocalDate fim = LocalDate.of(2025, 1, 31);
        when(periodo.getInicio()).thenReturn(inicio);
        when(periodo.getFim()).thenReturn(fim);

        Telemetria telemetria = mock(Telemetria.class);
        when(telemetria.getServicos()).thenReturn(List.of(servico1, servico2));
        when(telemetria.getPeriodo()).thenReturn(periodo);

        when(coletorTelemetria.obterTelemetriaGeral()).thenReturn(telemetria);

        TelemetriaDto dto = telemetriaService.obterTelemetriaGeral();

        assertNotNull(dto);
        assertNotNull(dto.periodo());
        assertEquals(inicio, dto.periodo().inicio());
        assertEquals(fim, dto.periodo().fim());

        assertEquals(2, dto.servicos().size());

        TelemetriaServicoDto dto1 = dto.servicos().getFirst();
        assertEquals("simular-investimento", dto1.nome());
        assertEquals(10L, dto1.quantidadeChamadas());
        assertEquals(120L, dto1.mediaTempoRespostaMs());

        TelemetriaServicoDto dto2 = dto.servicos().get(1);
        assertEquals("listar-simulacoes", dto2.nome());
        assertEquals(5L, dto2.quantidadeChamadas());
        assertEquals(80L, dto2.mediaTempoRespostaMs());

        verify(coletorTelemetria).obterTelemetriaGeral();
        verifyNoInteractions(telemetriaRegistroRepository);
    }

    @Test
    void deveObterTelemetriaGeralSemPeriodo() {
        TelemetriaServico servico = mock(TelemetriaServico.class);
        when(servico.getNome()).thenReturn("simular-investimento");
        when(servico.getQuantidadeChamadas()).thenReturn(3L);
        when(servico.getMediaTempoRespostaMs()).thenReturn(100L);

        Telemetria telemetria = mock(Telemetria.class);
        when(telemetria.getServicos()).thenReturn(List.of(servico));
        when(telemetria.getPeriodo()).thenReturn(null);

        when(coletorTelemetria.obterTelemetriaGeral()).thenReturn(telemetria);

        TelemetriaDto dto = telemetriaService.obterTelemetriaGeral();

        assertNotNull(dto);
        assertNull(dto.periodo());
        assertEquals(1, dto.servicos().size());

        TelemetriaServicoDto servicoDto = dto.servicos().getFirst();
        assertEquals("simular-investimento", servicoDto.nome());
        assertEquals(3L, servicoDto.quantidadeChamadas());
        assertEquals(100L, servicoDto.mediaTempoRespostaMs());

        verify(coletorTelemetria).obterTelemetriaGeral();
        verifyNoInteractions(telemetriaRegistroRepository);
    }

    @Test
    void deveLancarErroQuandoInicioForMaiorQueFim() {
        LocalDate inicio = LocalDate.of(2025, 2, 1);
        LocalDate fim = LocalDate.of(2025, 1, 31);

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> telemetriaService.obterResumoPorPeriodo(inicio, fim)
        );

        assertEquals(CodigoErroNegocio.TELEMETRIA_PERIODO_INVALIDO, excecao.getCodigoErro());
        verifyNoInteractions(telemetriaRegistroRepository);
    }

    @Test
    void deveObterResumoPorPeriodoAgrupandoPorServico() {
        TelemetriaRegistroEntity r1 = mock(TelemetriaRegistroEntity.class);
        when(r1.getNomeServico()).thenReturn("simular-investimento");
        when(r1.getTempoRespostaMs()).thenReturn(100L);

        TelemetriaRegistroEntity r2 = mock(TelemetriaRegistroEntity.class);
        when(r2.getNomeServico()).thenReturn("simular-investimento");
        when(r2.getTempoRespostaMs()).thenReturn(200L);

        TelemetriaRegistroEntity r3 = mock(TelemetriaRegistroEntity.class);
        when(r3.getNomeServico()).thenReturn("listar-simulacoes");
        when(r3.getTempoRespostaMs()).thenReturn(50L);

        when(telemetriaRegistroRepository.listarPorPeriodo(any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(List.of(r1, r2, r3));

        LocalDate inicio = LocalDate.of(2025, 1, 1);
        LocalDate fim = LocalDate.of(2025, 1, 31);

        TelemetriaDto dto = telemetriaService.obterResumoPorPeriodo(inicio, fim);

        assertNotNull(dto.periodo());
        assertEquals(inicio, dto.periodo().inicio());
        assertEquals(fim, dto.periodo().fim());

        assertEquals(2, dto.servicos().size());

        TelemetriaServicoDto primeiro = dto.servicos().getFirst();
        assertEquals("listar-simulacoes", primeiro.nome());
        assertEquals(1L, primeiro.quantidadeChamadas());
        assertEquals(50L, primeiro.mediaTempoRespostaMs());

        TelemetriaServicoDto segundo = dto.servicos().get(1);
        assertEquals("simular-investimento", segundo.nome());
        assertEquals(2L, segundo.quantidadeChamadas());
        assertEquals(150L, segundo.mediaTempoRespostaMs());

        verify(telemetriaRegistroRepository).listarPorPeriodo(any(OffsetDateTime.class), any(OffsetDateTime.class));
        verifyNoInteractions(coletorTelemetria);
    }

    @Test
    void deveObterResumoPorPeriodoComListaVazia() {
        when(telemetriaRegistroRepository.listarPorPeriodo(any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(List.of());

        LocalDate inicio = LocalDate.of(2025, 1, 1);
        LocalDate fim = LocalDate.of(2025, 1, 31);

        TelemetriaDto dto = telemetriaService.obterResumoPorPeriodo(inicio, fim);

        assertNotNull(dto);
        assertNotNull(dto.periodo());
        assertTrue(dto.servicos().isEmpty());

        verify(telemetriaRegistroRepository).listarPorPeriodo(any(OffsetDateTime.class), any(OffsetDateTime.class));
        verifyNoInteractions(coletorTelemetria);
    }
}