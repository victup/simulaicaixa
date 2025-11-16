package br.gov.caixa.simulaicaixa.data.repository;

import br.gov.caixa.simulaicaixa.data.entity.SimulacaoInvestimentoEntity;
import br.gov.caixa.simulaicaixa.domain.SimulacaoInvestimento;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static br.gov.caixa.simulaicaixa.data.mapper.SimulacaoInvestimentoMapper.mapearParaDominio;
import static br.gov.caixa.simulaicaixa.data.mapper.SimulacaoInvestimentoMapper.mapearParaEntity;

@ApplicationScoped
public class SimulacaoInvestimentoRepositoryImpl implements SimulacaoInvestimentoRepository {

    private final EntityManager entityManager;

    @Inject
    public SimulacaoInvestimentoRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public SimulacaoInvestimento salvar(SimulacaoInvestimento simulacaoInvestimento) {
        SimulacaoInvestimentoEntity entity = mapearParaEntity(simulacaoInvestimento);

        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }

        entityManager.flush();

        return mapearParaDominio(entity);
    }

    @Override
    public List<SimulacaoInvestimento> listarTodas() {
        TypedQuery<SimulacaoInvestimentoEntity> query =
                entityManager.createQuery(
                        "SELECT s FROM SimulacaoInvestimentoEntity s",
                        SimulacaoInvestimentoEntity.class
                );

        return query.getResultList()
                .stream()
                .map(entidade -> mapearParaDominio(entidade))
                .collect(Collectors.toList());
    }

    @Override
    public List<SimulacaoInvestimento> listarPorProdutoEDia(String nomeProduto, LocalDate data) {
        TypedQuery<SimulacaoInvestimentoEntity> query =
                entityManager.createQuery(
                        "SELECT s FROM SimulacaoInvestimentoEntity s WHERE s.nomeProduto = :produto",
                        SimulacaoInvestimentoEntity.class
                );

        List<SimulacaoInvestimentoEntity> entidades = query
                .setParameter("produto", nomeProduto)
                .getResultList();

        return entidades.stream()
                .filter(entidade ->
                        entidade.getDataSimulacao() != null
                                && data.equals(entidade.getDataSimulacao().toLocalDate()))
                .map(entidade -> mapearParaDominio(entidade))
                .collect(Collectors.toList());
    }
}