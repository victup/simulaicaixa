package br.gov.caixa.simulaicaixa.data.repository;

import br.gov.caixa.simulaicaixa.domain.ProdutoInvestimento;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProdutoRepositoryImpl implements ProdutoRepository {

    private final List<ProdutoInvestimento> produtos;

    public ProdutoRepositoryImpl() {
        this.produtos = criarProdutosEmMemoria();
    }

    @Override
    public List<ProdutoInvestimento> listarPorPerfil(String perfil) {
        if (perfil == null || perfil.isBlank()) {
            return produtos;
        }

        String perfilNormalizado = perfil.toUpperCase(Locale.ROOT);

        return produtos.stream()
                .filter(produto ->
                        produto.getPerfilRecomendado() != null
                                && produto.getPerfilRecomendado().toUpperCase(Locale.ROOT).equals(perfilNormalizado))
                .collect(Collectors.toList());
    }

    private List<ProdutoInvestimento> criarProdutosEmMemoria() {
        ProdutoInvestimento cdbConservador = new ProdutoInvestimento();
        cdbConservador.setId(101L);
        cdbConservador.setNome("CDB Caixa 2026");
        cdbConservador.setTipo("CDB");
        cdbConservador.setRentabilidade(new BigDecimal("0.12"));
        cdbConservador.setRisco("Baixo");
        cdbConservador.setPerfilRecomendado("CONSERVADOR");

        ProdutoInvestimento fundoAgressivo = new ProdutoInvestimento();
        fundoAgressivo.setId(102L);
        fundoAgressivo.setNome("Fundo XPTO");
        fundoAgressivo.setTipo("Fundo");
        fundoAgressivo.setRentabilidade(new BigDecimal("0.18"));
        fundoAgressivo.setRisco("Alto");
        fundoAgressivo.setPerfilRecomendado("AGRESSIVO");

        ProdutoInvestimento cdbModerado = new ProdutoInvestimento();
        cdbModerado.setId(103L);
        cdbModerado.setNome("CDB Caixa Liquidez Diária");
        cdbModerado.setTipo("CDB");
        cdbModerado.setRentabilidade(new BigDecimal("0.10"));
        cdbModerado.setRisco("Baixo");
        cdbModerado.setPerfilRecomendado("MODERADO");

        return List.of(
                cdbConservador,
                fundoAgressivo,
                cdbModerado
        );
    }
}
