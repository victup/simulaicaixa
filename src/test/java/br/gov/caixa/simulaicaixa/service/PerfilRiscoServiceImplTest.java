package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import br.gov.caixa.simulaicaixa.data.repository.PerfilRiscoRepository;
import br.gov.caixa.simulaicaixa.domain.PerfilRisco;
import br.gov.caixa.simulaicaixa.domain.enums.TipoPerfilRiscoEnum;
import br.gov.caixa.simulaicaixa.dto.PerfilRiscoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PerfilRiscoServiceImplTest {

    private PerfilRiscoRepository perfilRiscoRepository;
    private PerfilRiscoServiceImpl perfilRiscoService;

    @BeforeEach
    void setUp() {
        perfilRiscoRepository = mock(PerfilRiscoRepository.class);
        perfilRiscoService = new PerfilRiscoServiceImpl(perfilRiscoRepository);
    }

    @Test
    void deveRetornarPerfilRiscoDtoQuandoPerfilForValido() {
        Long clienteId = 123L;

        PerfilRisco perfil = mock(PerfilRisco.class);
        when(perfil.getClienteId()).thenReturn(clienteId);
        when(perfil.getTipoPerfilRisco()).thenReturn(TipoPerfilRiscoEnum.MODERADO);
        when(perfil.getPerfil()).thenReturn("Moderado");
        when(perfil.getPontuacao()).thenReturn(60);
        when(perfil.getDescricao()).thenReturn("Perfil moderado de investimentos.");

        when(perfilRiscoRepository.obterPorClienteId(clienteId)).thenReturn(perfil);

        PerfilRiscoDto dto = perfilRiscoService.obterPorClienteId(clienteId);

        verify(perfilRiscoRepository).obterPorClienteId(clienteId);
        assertEquals(clienteId, dto.clienteId());
        assertEquals("Moderado", dto.perfil());
        assertEquals(60, dto.pontuacao());
        assertEquals("Perfil moderado de investimentos.", dto.descricao());
    }

    @Test
    void deveLancarNegocioExceptionQuandoPerfilNaoForEncontrado() {
        Long clienteId = 123L;

        when(perfilRiscoRepository.obterPorClienteId(clienteId)).thenReturn(null);

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> perfilRiscoService.obterPorClienteId(clienteId)
        );

        assertEquals(CodigoErroNegocio.CLIENTE_SEM_PERFIL_RISCO, excecao.getCodigoErro());
        verify(perfilRiscoRepository).obterPorClienteId(clienteId);
    }

    @Test
    void deveLancarNegocioExceptionQuandoTipoPerfilForNulo() {
        Long clienteId = 123L;

        PerfilRisco perfil = mock(PerfilRisco.class);
        when(perfil.getTipoPerfilRisco()).thenReturn(null);

        when(perfilRiscoRepository.obterPorClienteId(clienteId)).thenReturn(perfil);

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> perfilRiscoService.obterPorClienteId(clienteId)
        );

        assertEquals(CodigoErroNegocio.CLIENTE_SEM_PERFIL_RISCO, excecao.getCodigoErro());
        verify(perfilRiscoRepository).obterPorClienteId(clienteId);
    }
}