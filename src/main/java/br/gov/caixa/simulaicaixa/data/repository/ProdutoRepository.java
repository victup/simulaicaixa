package br.gov.caixa.simulaicaixa.data.repository;

import br.gov.caixa.simulaicaixa.domain.ProdutoInvestimento;

import java.util.List;

public interface ProdutoRepository {

    List<ProdutoInvestimento> listarPorPerfil(Integer codigoPerfil);
}