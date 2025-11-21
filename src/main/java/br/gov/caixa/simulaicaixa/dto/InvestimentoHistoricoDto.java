package br.gov.caixa.simulaicaixa.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Item do histórico de investimentos de um cliente.
 *
 * @param id identificador do investimento
 * @param tipo tipo de investimento (ex.: CDB, FUNDO, TESOURO)
 * @param valor valor investido
 * @param rentabilidade rentabilidade associada ao investimento
 * @param data  data de realização do investimento
 */
public record InvestimentoHistoricoDto(
        Long id,
        String tipo,
        BigDecimal valor,
        BigDecimal rentabilidade,
        LocalDate data
) {
}