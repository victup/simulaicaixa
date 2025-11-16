package br.gov.caixa.simulaicaixa.data.repository;

import br.gov.caixa.simulaicaixa.data.entity.InvestimentoEntity;
import br.gov.caixa.simulaicaixa.domain.Investimento;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.stream.Collectors;

import static br.gov.caixa.simulaicaixa.data.mapper.InvestimentoMapper.mapearParaDominio;

@ApplicationScoped
public class InvestimentoRepositoryImpl implements InvestimentoRepository {

    private final EntityManager entityManager;

    @Inject
    public InvestimentoRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Investimento> listarPorClienteId(Long clienteId) {
        String jpql = "SELECT i FROM InvestimentoEntity i WHERE i.clienteId = :clienteId";

        TypedQuery<InvestimentoEntity> query =
                entityManager.createQuery(jpql, InvestimentoEntity.class)
                        .setParameter("clienteId", clienteId);

        List<InvestimentoEntity> entidades = query.getResultList();

        return entidades.stream()
                .map(InvestimentoRepositoryImpl::converterParaDominio)
                .collect(Collectors.toList());
    }

    private static Investimento converterParaDominio(InvestimentoEntity entity) {
        return mapearParaDominio(entity);
    }
}