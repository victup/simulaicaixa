package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.domain.Investimento;
import br.gov.caixa.simulaicaixa.domain.PerfilRisco;
import br.gov.caixa.simulaicaixa.domain.ProdutoInvestimento;
import br.gov.caixa.simulaicaixa.domain.enums.TipoPerfilRisco;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class MotorRecomendacaoServiceImpl implements MotorRecomendacaoService {

    @Override
    public List<ProdutoInvestimento> recomendarPorPerfil(PerfilRisco perfilRisco,
                                                         List<ProdutoInvestimento> produtos) {
        return produtos.stream()
                .sorted(Comparator.comparingDouble(
                        p -> -calcularScoreProduto(p, perfilRisco, null)
                ))
                .toList();
    }

    @Override
    public List<ProdutoInvestimento> recomendarPorPerfilEHistorico(PerfilRisco perfilRisco,
                                                                   List<ProdutoInvestimento> produtos,
                                                                   List<Investimento> historicoCliente) {
        return produtos.stream()
                .sorted(Comparator.comparingDouble(
                        p -> -calcularScoreProduto(p, perfilRisco, historicoCliente)
                ))
                .toList();
    }

    private double calcularScoreProduto(ProdutoInvestimento produto,
                                        PerfilRisco perfilRisco,
                                        List<Investimento> historicoCliente) {

        double score = 0.0;

        TipoPerfilRisco perfilCliente = perfilRisco != null
                ? perfilRisco.getTipoPerfilRisco()
                : null;

        TipoPerfilRisco perfilProduto = TipoPerfilRisco.fromDescricao(produto.getPerfilRecomendado());

        String riscoProduto = normalizar(produto.getRisco());

        if (perfilCliente != null && perfilProduto != null) {
            if (perfilCliente == perfilProduto) {
                score += 50.0;
            } else if (ehAdjacente(perfilCliente, perfilProduto)) {
                score += 25.0;
            } else {
                score -= 10.0;
            }
        }

        if (riscoProduto != null && perfilCliente != null) {
            double ajusteRisco = calcularAjustePorRisco(perfilCliente, riscoProduto);
            score += ajusteRisco;
        }

        if (produto.getRentabilidade() != null) {
            BigDecimal rent = produto.getRentabilidade();
            if (rent.compareTo(new BigDecimal("0.20")) >= 0) {
                score += 10.0;
            } else if (rent.compareTo(new BigDecimal("0.12")) >= 0) {
                score += 6.0;
            } else if (rent.compareTo(new BigDecimal("0.08")) >= 0) {
                score += 3.0;
            }
        }

        if (historicoCliente != null && !historicoCliente.isEmpty()) {
            score += calcularBonusPorHistorico(produto, historicoCliente);
        }

        return score;
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return null;
        }

        return valor.trim().toUpperCase(Locale.ROOT);
    }

    private boolean ehAdjacente(TipoPerfilRisco perfilCliente, TipoPerfilRisco perfilProduto) {
        if (perfilCliente == TipoPerfilRisco.CONSERVADOR && perfilProduto == TipoPerfilRisco.MODERADO) {
            return true;
        }

        if (perfilCliente == TipoPerfilRisco.MODERADO
                && (perfilProduto == TipoPerfilRisco.CONSERVADOR || perfilProduto == TipoPerfilRisco.AGRESSIVO)) {
            return true;
        }

        if (perfilCliente == TipoPerfilRisco.AGRESSIVO && perfilProduto == TipoPerfilRisco.MODERADO) {
            return true;
        }

        return false;
    }

    private double calcularAjustePorRisco(TipoPerfilRisco perfilCliente, String riscoProduto) {
        if (perfilCliente == TipoPerfilRisco.CONSERVADOR) {
            if ("BAIXO".equals(riscoProduto)) {
                return 10.0;
            }
            if ("MEDIO".equals(riscoProduto)) {
                return -2.0;
            }
            return -8.0;
        }

        if (perfilCliente == TipoPerfilRisco.MODERADO) {
            if ("BAIXO".equals(riscoProduto)) {
                return 3.0;
            }
            if ("MEDIO".equals(riscoProduto)) {
                return 6.0;
            }
            if ("ALTO".equals(riscoProduto)) {
                return 2.0;
            }
        }

        if (perfilCliente == TipoPerfilRisco.AGRESSIVO) {
            if ("ALTO".equals(riscoProduto)) {
                return 8.0;
            }
            if ("MEDIO".equals(riscoProduto)) {
                return 5.0;
            }
            return 1.0;
        }

        return 0.0;
    }

    private double calcularBonusPorHistorico(ProdutoInvestimento produto,
                                             List<Investimento> historicoCliente) {

        Map<String, Long> quantidadePorTipo =
                historicoCliente.stream()
                        .collect(Collectors.groupingBy(
                                i -> normalizar(i.getTipo()),
                                Collectors.counting()
                        ));

        String tipoProduto = normalizar(produto.getTipo());

        if (tipoProduto == null) {
            return 0.0;
        }

        long quantidadeNoHistorico = quantidadePorTipo.getOrDefault(tipoProduto, 0L);

        if (quantidadeNoHistorico == 0) {
            return 0.0;
        }

        if (quantidadeNoHistorico == 1) {
            return 2.0;
        }

        if (quantidadeNoHistorico <= 3) {
            return 4.0;
        }

        return 6.0;
    }
}