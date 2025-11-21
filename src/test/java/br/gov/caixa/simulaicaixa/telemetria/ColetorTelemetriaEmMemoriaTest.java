package br.gov.caixa.simulaicaixa.telemetria;

import br.gov.caixa.simulaicaixa.data.entity.TelemetriaRegistroEntity;
import br.gov.caixa.simulaicaixa.data.repository.TelemetriaRegistroRepository;
import br.gov.caixa.simulaicaixa.domain.Telemetria;
import br.gov.caixa.simulaicaixa.domain.TelemetriaPeriodo;
import br.gov.caixa.simulaicaixa.domain.TelemetriaServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ColetorTelemetriaEmMemoriaTest {

    private TelemetriaRegistroRepository telemetriaRegistroRepository;
    private ColetorTelemetriaEmMemoria coletor;

    @BeforeEach
    void setUp() {
        telemetriaRegistroRepository = mock(TelemetriaRegistroRepository.class);
        coletor = new ColetorTelemetriaEmMemoria(telemetriaRegistroRepository);
    }

    @Test
    void deveRegistrarChamadaESalvarNoRepositorio() {
        coletor.registrarChamada("servico-x", 150L);

        ArgumentCaptor<TelemetriaRegistroEntity> captor =
                ArgumentCaptor.forClass(TelemetriaRegistroEntity.class);

        verify(telemetriaRegistroRepository).salvar(captor.capture());

        TelemetriaRegistroEntity registro = captor.getValue();
        assertEquals("servico-x", registro.getNomeServico());
        assertEquals(150L, registro.getTempoRespostaMs());
        assertNotNull(registro.getDataHoraChamada());
    }

    @Test
    void deveRetornarTelemetriaVaziaQuandoNaoHouverRegistros() {
        Telemetria telemetria = coletor.obterTelemetriaGeral();

        assertNotNull(telemetria);
        assertNotNull(telemetria.getServicos());
        assertTrue(telemetria.getServicos().isEmpty());
        assertNull(telemetria.getPeriodo());

        verifyNoInteractions(telemetriaRegistroRepository);
    }

    @Test
    void deveCalcularMediaQuantidadeEPeriodoParaServico() {
        coletor.registrarChamada("servico-y", 100L);
        coletor.registrarChamada("servico-y", 300L);

        Telemetria telemetria = coletor.obterTelemetriaGeral();
        List<TelemetriaServico> servicos = telemetria.getServicos();

        assertEquals(1, servicos.size());

        TelemetriaServico servico = servicos.getFirst();
        assertEquals("servico-y", servico.getNome());
        assertEquals(2L, servico.getQuantidadeChamadas());
        assertEquals(200L, servico.getMediaTempoRespostaMs());

        TelemetriaPeriodo periodo = telemetria.getPeriodo();
        assertNotNull(periodo);
        LocalDate hoje = LocalDate.now();
        assertFalse(periodo.getInicio().isAfter(periodo.getFim()));
        assertTrue(
                (periodo.getInicio().isEqual(hoje) || periodo.getInicio().isBefore(hoje.plusDays(1)))
                        && (periodo.getFim().isEqual(hoje) || periodo.getFim().isAfter(hoje.minusDays(1)))
        );
    }
}