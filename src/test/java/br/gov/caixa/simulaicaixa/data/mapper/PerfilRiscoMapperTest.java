package br.gov.caixa.simulaicaixa.data.mapper;

import br.gov.caixa.simulaicaixa.data.entity.PerfilRiscoEntity;
import br.gov.caixa.simulaicaixa.domain.PerfilRisco;
import br.gov.caixa.simulaicaixa.domain.enums.TipoPerfilRiscoEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PerfilRiscoMapperTest {

    @Test
    void deveMapearDominioParaEntityComPerfil() {
        PerfilRisco perfil = new PerfilRisco();
        perfil.setClienteId(123L);
        perfil.setTipoPerfilRisco(TipoPerfilRiscoEnum.MODERADO);
        perfil.setPontuacao(70);
        perfil.setDescricao("Perfil moderado");

        PerfilRiscoEntity entity = PerfilRiscoMapper.mapearParaEntity(perfil);

        assertEquals(123L, entity.getClienteId());
        assertEquals(TipoPerfilRiscoEnum.MODERADO.getCodigo(), entity.getPerfil());
        assertEquals(70, entity.getPontuacao());
        assertEquals("Perfil moderado", entity.getDescricao());
    }

    @Test
    void deveMapearDominioParaEntityComPerfilNulo() {
        PerfilRisco perfil = new PerfilRisco();
        perfil.setClienteId(321L);
        perfil.setTipoPerfilRisco(null);
        perfil.setPontuacao(10);
        perfil.setDescricao("Sem perfil");

        PerfilRiscoEntity entity = PerfilRiscoMapper.mapearParaEntity(perfil);

        assertNull(entity.getPerfil());
    }

    @Test
    void deveMapearEntityParaDominioComPerfil() {
        PerfilRiscoEntity entity = new PerfilRiscoEntity();
        entity.setClienteId(999L);
        entity.setPerfil(TipoPerfilRiscoEnum.AGRESSIVO.getCodigo());
        entity.setPontuacao(90);
        entity.setDescricao("Perfil agressivo");

        PerfilRisco perfil = PerfilRiscoMapper.mapearParaDominio(entity);

        assertEquals(999L, perfil.getClienteId());
        assertEquals(TipoPerfilRiscoEnum.AGRESSIVO, perfil.getTipoPerfilRisco());
        assertEquals(90, perfil.getPontuacao());
        assertEquals("Perfil agressivo", perfil.getDescricao());
    }
}