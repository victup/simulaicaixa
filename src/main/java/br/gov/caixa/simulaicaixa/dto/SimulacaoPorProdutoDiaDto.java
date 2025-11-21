package br.gov.caixa.simulaicaixa.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Agregação de simulações por produto e dia,
 * usada em relatórios e painéis de acompanhamento.
 *
 * @param produto nome do produto de investimento
 * @param data dia em que as simulações ocorreram
 * @param quantidadeSimulacoes quantidade de simulações realizadas no dia para o produto
 * @param mediaValorFinal média do valor final projetadso nas simulações
 */
public record SimulacaoPorProdutoDiaDto(
        String produto,
        LocalDate data,
        Long quantidadeSimulacoes,
        BigDecimal mediaValorFinal
) {
}