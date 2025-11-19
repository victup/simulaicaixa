package br.gov.caixa.simulaicaixa.data.repository;

import br.gov.caixa.simulaicaixa.data.entity.ProdutoInvestimentoEntity;
import br.gov.caixa.simulaicaixa.domain.ProdutoInvestimento;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

import static br.gov.caixa.simulaicaixa.data.mapper.ProdutoInvestimentoMapper.mapearParaDominio;

@ApplicationScoped
public class ProdutoRepositoryImpl implements ProdutoRepository {

    private final EntityManager entityManager;

    @Inject
    public ProdutoRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<ProdutoInvestimento> listarPorPerfil(Integer perfil) {
        List<ProdutoInvestimentoEntity> entidades;

        if (perfil == null) {
            entidades = listarTodos();
        } else {
            entidades = listarPorPerfilInterno(perfil);
        }

        return entidades.stream()
                .map(ProdutoRepositoryImpl::converterParaDominio)
                .toList();
    }

    private List<ProdutoInvestimentoEntity> listarTodos() {
        String jpql = "SELECT p FROM ProdutoInvestimentoEntity p";
        TypedQuery<ProdutoInvestimentoEntity> query =
                entityManager.createQuery(jpql, ProdutoInvestimentoEntity.class);

        return query.getResultList();
    }

    private List<ProdutoInvestimentoEntity> listarPorPerfilInterno(Integer perfil) {
        String jpql = "SELECT p FROM ProdutoInvestimentoEntity p " +
                "WHERE p.perfilRecomendado = :perfil";

        TypedQuery<ProdutoInvestimentoEntity> query =
                entityManager.createQuery(jpql, ProdutoInvestimentoEntity.class);

        return query
                .setParameter("perfil", perfil)
                .getResultList();
    }

    private static ProdutoInvestimento converterParaDominio(ProdutoInvestimentoEntity entity) {
        return mapearParaDominio(entity);
    }
}