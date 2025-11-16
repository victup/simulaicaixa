package br.gov.caixa.simulaicaixa.data.mapper;

import br.gov.caixa.simulaicaixa.data.entity.PerfilRiscoEntity;
import br.gov.caixa.simulaicaixa.domain.PerfilRisco;

public final class PerfilRiscoMapper {

    private PerfilRiscoMapper() {
    }

    public static PerfilRiscoEntity mapearParaEntity(PerfilRiscoEntity perfil) {
        PerfilRiscoEntity entity = new PerfilRiscoEntity();

        entity.setClienteId(perfil.getClienteId());
        entity.setPerfil(perfil.getPerfil());
        entity.setPontuacao(perfil.getPontuacao());
        entity.setDescricao(perfil.getDescricao());

        return entity;
    }

    public static PerfilRisco mapearParaDominio(PerfilRiscoEntity entity) {
        PerfilRisco perfil = new PerfilRisco();

        perfil.setClienteId(entity.getClienteId());
        perfil.setPerfil(entity.getPerfil());
        perfil.setPontuacao(entity.getPontuacao());
        perfil.setDescricao(entity.getDescricao());

        return perfil;
    }
}