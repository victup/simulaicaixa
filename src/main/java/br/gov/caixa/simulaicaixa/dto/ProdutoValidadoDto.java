package br.gov.caixa.simulaicaixa.dto;

import java.math.BigDecimal;

/**
 * Detalhes do produto efetivamente utilizado em uma simulação.
 *
 * @param id identificador do produto
 * @param nome nome comercial do produto
 * @param tipo tipo de investimento (ex.: CDB, FUNDO)
 * @param rentabilidade rentabilidade anual considerada na simulação
 * @param risco nível de risco do produto
 */
public record ProdutoValidadoDto(
        Long id,
        String nome,
        String tipo,
        BigDecimal rentabilidade,
        String risco
) {
}