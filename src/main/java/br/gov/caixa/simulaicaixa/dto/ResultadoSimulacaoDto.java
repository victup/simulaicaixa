package br.gov.caixa.simulaicaixa.dto;

import java.math.BigDecimal;

/**
 * Resultado numérico da simulação de investimento.
 *
 * @param valorFinal valor projetado ao final do prazo
 * @param rentabilidadeEfetiva rentabilidade efetiva aplicada na simulação
 * @param prazoMeses prazo considerado, em meses
 */
public record ResultadoSimulacaoDto(
        BigDecimal valorFinal,
        BigDecimal rentabilidadeEfetiva,
        Integer prazoMeses
) {
}