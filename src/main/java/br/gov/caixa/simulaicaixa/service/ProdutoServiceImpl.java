package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.data.repository.ProdutoRepository;
import br.gov.caixa.simulaicaixa.domain.ProdutoInvestimento;
import br.gov.caixa.simulaicaixa.dto.ProdutoRecomendadoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository produtoRepository;

    @Inject
    public ProdutoServiceImpl(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Override
    public List<ProdutoRecomendadoDto> listarProdutosRecomendadosPorPerfil(String perfil) {
        List<ProdutoInvestimento> produtos = produtoRepository.listarPorPerfil(perfil);

        return produtos.stream()
                .map(produto -> new ProdutoRecomendadoDto(
                        produto.getId(),
                        produto.getNome(),
                        produto.getTipo(),
                        produto.getRentabilidade(),
                        produto.getRisco()
                ))
                .collect(Collectors.toList());
    }
}
