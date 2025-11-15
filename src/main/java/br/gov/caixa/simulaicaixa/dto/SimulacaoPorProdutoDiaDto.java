package br.gov.caixa.simulaicaixa.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SimulacaoPorProdutoDiaDto(
        String produto,
        LocalDate data,
        Long quantidadeSimulacoes,
        BigDecimal mediaValorFinal
) {
}