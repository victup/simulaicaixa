package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.dto.ProdutoRecomendadoDto;

import java.util.List;

public interface ProdutoService {

    List<ProdutoRecomendadoDto> listarProdutosRecomendadosPorPerfil(String perfil);
}
