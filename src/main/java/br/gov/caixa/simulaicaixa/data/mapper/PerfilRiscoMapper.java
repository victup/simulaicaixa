package br.gov.caixa.simulaicaixa.data.mapper;

import br.gov.caixa.simulaicaixa.data.entity.PerfilRiscoEntity;
import br.gov.caixa.simulaicaixa.domain.PerfilRisco;
import br.gov.caixa.simulaicaixa.domain.enums.TipoPerfilRiscoEnum;

public final class PerfilRiscoMapper {

    private PerfilRiscoMapper() {
    }

    public static PerfilRiscoEntity mapearParaEntity(PerfilRisco perfil) {
        PerfilRiscoEntity entity = new PerfilRiscoEntity();

        entity.setClienteId(perfil.getClienteId());

        TipoPerfilRiscoEnum tipoPerfilRisco = perfil.getTipoPerfilRisco();
        entity.setPerfil(tipoPerfilRisco != null ? tipoPerfilRisco.getCodigo() : null);

        entity.setPontuacao(perfil.getPontuacao());
        entity.setDescricao(perfil.getDescricao());

        return entity;
    }

    public static PerfilRisco mapearParaDominio(PerfilRiscoEntity entity) {
        PerfilRisco perfil = new PerfilRisco();

        perfil.setClienteId(entity.getClienteId());
        perfil.setTipoPerfilRisco(TipoPerfilRiscoEnum.obterPorCodigo(entity.getPerfil()));
        perfil.setPontuacao(entity.getPontuacao());
        perfil.setDescricao(entity.getDescricao());

        return perfil;
    }
}