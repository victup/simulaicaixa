package br.gov.caixa.simulaicaixa.dto;

import java.math.BigDecimal;

public record ProdutoValidadoDto(
        Long id,
        String nome,
        String tipo,
        BigDecimal rentabilidade,
        String risco
) {
}