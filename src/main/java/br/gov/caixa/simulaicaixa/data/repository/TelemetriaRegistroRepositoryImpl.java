package br.gov.caixa.simulaicaixa.data.repository;

import br.gov.caixa.simulaicaixa.data.entity.TelemetriaRegistroEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@ApplicationScoped
public class TelemetriaRegistroRepositoryImpl implements TelemetriaRegistroRepository {

    private final EntityManager entityManager;

    @Inject
    public TelemetriaRegistroRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void salvar(TelemetriaRegistroEntity registro) {
        entityManager.persist(registro);
    }

    @Override
    public List<TelemetriaRegistroEntity> listarPorPeriodo(OffsetDateTime inicio, OffsetDateTime fim) {
        TypedQuery<TelemetriaRegistroEntity> query = entityManager.createQuery(
                "SELECT t FROM TelemetriaRegistroEntity t " +
                        "WHERE t.dataHoraChamada BETWEEN :inicio AND :fim",
                TelemetriaRegistroEntity.class
        );

        return query
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();
    }
}