package br.gov.caixa.simulaicaixa.data.repository;

import br.gov.caixa.simulaicaixa.data.entity.PerfilRiscoEntity;
import br.gov.caixa.simulaicaixa.domain.PerfilRisco;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import static br.gov.caixa.simulaicaixa.data.mapper.PerfilRiscoMapper.mapearParaDominio;

@ApplicationScoped
public class PerfilRiscoRepositoryImpl implements PerfilRiscoRepository {

    private final EntityManager entityManager;

    @Inject
    public PerfilRiscoRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public PerfilRisco obterPorClienteId(Long clienteId) {
        PerfilRiscoEntity entity = entityManager.find(PerfilRiscoEntity.class, clienteId);

        if (entity == null) {
            return null;
        }

        return mapearParaDominio(entity);
    }
}