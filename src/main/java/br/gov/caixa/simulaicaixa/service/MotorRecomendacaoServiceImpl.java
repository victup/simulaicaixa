package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.domain.Investimento;
import br.gov.caixa.simulaicaixa.domain.PerfilRisco;
import br.gov.caixa.simulaicaixa.domain.ProdutoInvestimento;
import br.gov.caixa.simulaicaixa.domain.enums.NivelRiscoProdutoEnum;
import br.gov.caixa.simulaicaixa.domain.enums.TipoInvestimentoEnum;
import br.gov.caixa.simulaicaixa.domain.enums.TipoPerfilRiscoEnum;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        double fatorPreferencia = calcularFatorPreferenciaRentabilidade(historicoCliente);

        if (produto.getRentabilidade() != null) {
            BigDecimal rent = produto.getRentabilidade();
            double scoreRentabilidade = 0.0;

            if (rent.compareTo(new BigDecimal("0.20")) >= 0) {
                scoreRentabilidade = 10.0;
            } else if (rent.compareTo(new BigDecimal("0.12")) >= 0) {
                scoreRentabilidade = 6.0;
            } else if (rent.compareTo(new BigDecimal("0.08")) >= 0) {
                scoreRentabilidade = 3.0;
            }

            score += scoreRentabilidade * fatorPreferencia;
        }

        if (historicoCliente != null && !historicoCliente.isEmpty()) {
            score += calcularBonusPorHistorico(produto, historicoCliente);
            score += calcularBonusPorVolume(produto, historicoCliente);
            score += calcularBonusPorFrequencia(historicoCliente, perfilCliente);
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

    private double calcularBonusPorVolume(ProdutoInvestimento produto,
                                          List<Investimento> historicoCliente) {
        if (historicoCliente == null || historicoCliente.isEmpty()) {
            return 0.0;
        }

        TipoInvestimentoEnum tipoProduto = produto.getTipoEnum();
        if (tipoProduto == null) {
            return 0.0;
        }

        BigDecimal total = historicoCliente.stream()
                .filter(i -> tipoProduto.equals(i.getTipo()))
                .map(Investimento::getValor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.compareTo(new BigDecimal("1000")) <= 0) {
            return 0.0;
        }

        if (total.compareTo(new BigDecimal("10000")) <= 0) {
            return 2.0;
        }

        if (total.compareTo(new BigDecimal("50000")) <= 0) {
            return 4.0;
        }

        return 6.0;
    }

    private double calcularBonusPorFrequencia(List<Investimento> historicoCliente,
                                              TipoPerfilRiscoEnum perfilCliente) {
        if (historicoCliente == null || historicoCliente.isEmpty() || perfilCliente == null) {
            return 0.0;
        }

        int quantidade = historicoCliente.size();

        return switch (perfilCliente) {
            case CONSERVADOR -> {
                if (quantidade <= 2) {
                    yield 4.0;
                }
                if (quantidade <= 5) {
                    yield 1.0;
                }
                yield -3.0;
            }
            case MODERADO -> {
                if (quantidade <= 2) {
                    yield 1.0;
                }
                if (quantidade <= 5) {
                    yield 3.0;
                }
                yield 4.0;
            }
            case AGRESSIVO -> {
                if (quantidade <= 2) {
                    yield -1.0;
                }
                if (quantidade <= 5) {
                    yield 3.0;
                }
                yield 6.0;
            }
        };
    }

    private double calcularFatorPreferenciaRentabilidade(List<Investimento> historicoCliente) {
        if (historicoCliente == null || historicoCliente.isEmpty()) {
            return 1.0;
        }

        BigDecimal soma = historicoCliente.stream()
                .map(Investimento::getRentabilidade)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long quantidade = historicoCliente.stream()
                .map(Investimento::getRentabilidade)
                .filter(Objects::nonNull)
                .count();

        if (quantidade == 0) {
            return 1.0;
        }

        BigDecimal media = soma.divide(BigDecimal.valueOf(quantidade), 4, RoundingMode.HALF_UP);

        if (media.compareTo(new BigDecimal("0.09")) < 0) {
            return 0.8;
        }

        if (media.compareTo(new BigDecimal("0.13")) > 0) {
            return 1.2;
        }

        return 1.0;
    }
}