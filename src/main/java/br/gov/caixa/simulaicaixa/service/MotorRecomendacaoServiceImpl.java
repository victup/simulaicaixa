package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.domain.Investimento;
import br.gov.caixa.simulaicaixa.domain.PerfilRisco;
import br.gov.caixa.simulaicaixa.domain.ProdutoInvestimento;
import br.gov.caixa.simulaicaixa.domain.enums.NivelRiscoProdutoEnum;
import br.gov.caixa.simulaicaixa.domain.enums.TipoInvestimentoEnum;
import br.gov.caixa.simulaicaixa.domain.enums.TipoPerfilRiscoEnum;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

        TipoPerfilRiscoEnum perfilCliente = perfilRisco != null
                ? perfilRisco.getTipoPerfilRisco()
                : null;

        TipoPerfilRiscoEnum perfilProduto = produto.getPerfilRecomendado();
        NivelRiscoProdutoEnum riscoProduto = produto.getRisco();

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

    private boolean ehAdjacente(TipoPerfilRiscoEnum perfilCliente, TipoPerfilRiscoEnum perfilProduto) {
        if (perfilCliente == TipoPerfilRiscoEnum.CONSERVADOR && perfilProduto == TipoPerfilRiscoEnum.MODERADO) {
            return true;
        }

        if (perfilCliente == TipoPerfilRiscoEnum.MODERADO
                && (perfilProduto == TipoPerfilRiscoEnum.CONSERVADOR || perfilProduto == TipoPerfilRiscoEnum.AGRESSIVO)) {
            return true;
        }

        if (perfilCliente == TipoPerfilRiscoEnum.AGRESSIVO && perfilProduto == TipoPerfilRiscoEnum.MODERADO) {
            return true;
        }

        return false;
    }

    private double calcularAjustePorRisco(TipoPerfilRiscoEnum perfilCliente, NivelRiscoProdutoEnum riscoProduto) {
        if (perfilCliente == TipoPerfilRiscoEnum.CONSERVADOR) {
            return switch (riscoProduto) {
                case BAIXO -> 10.0;
                case MEDIO -> -2.0;
                case ALTO -> -8.0;
            };
        }

        if (perfilCliente == TipoPerfilRiscoEnum.MODERADO) {
            return switch (riscoProduto) {
                case BAIXO -> 3.0;
                case MEDIO -> 6.0;
                case ALTO -> 2.0;
            };
        }

        if (perfilCliente == TipoPerfilRiscoEnum.AGRESSIVO) {
            return switch (riscoProduto) {
                case ALTO -> 8.0;
                case MEDIO -> 5.0;
                case BAIXO -> 1.0;
            };
        }

        return 0.0;
    }

    private double calcularBonusPorHistorico(ProdutoInvestimento produto,
                                             List<Investimento> historicoCliente) {

        Map<TipoInvestimentoEnum, Long> quantidadePorTipo =
                historicoCliente.stream()
                        .map(Investimento::getTipo)
                        .filter(Objects::nonNull)
                        .collect(Collectors.groupingBy(
                                tipo -> tipo,
                                Collectors.counting()
                        ));

        TipoInvestimentoEnum tipoProduto = produto.getTipoEnum();

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