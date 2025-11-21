package br.gov.caixa.simulaicaixa.dto;

import java.math.BigDecimal;

/**
 * Representa um produto de investimento recomendado ao cliente.
 *
 * @param id identificador do produto
 * @param nome nome comercial do produto
 * @param tipo tipo de investimento (ex.: CDB, FUNDO)
 * @param rentabilidade rentabilidade esperada do produto
 * @param risco nível de risco associado ao produtso
 */
public record ProdutoRecomendadoDto(
        Long id,
        String nome,
        String tipo,
        BigDecimal rentabilidade,
        String risco
) {
}