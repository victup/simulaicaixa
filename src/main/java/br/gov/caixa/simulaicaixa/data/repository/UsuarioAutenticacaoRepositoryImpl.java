package br.gov.caixa.simulaicaixa.data.repository;

import br.gov.caixa.simulaicaixa.data.entity.UsuarioAutenticacaoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class UsuarioAutenticacaoRepositoryImpl implements UsuarioAutenticacaoRepository {

    private final EntityManager entityManager;

    @Inject
    public UsuarioAutenticacaoRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public UsuarioAutenticacaoEntity buscarPorCpf(String cpf) {
        try {
            TypedQuery<UsuarioAutenticacaoEntity> query =
                    entityManager.createQuery(
                            "SELECT u FROM UsuarioAutenticacaoEntity u WHERE u.cpf = :cpf",
                            UsuarioAutenticacaoEntity.class
                    );
            return query.setParameter("cpf", cpf).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}