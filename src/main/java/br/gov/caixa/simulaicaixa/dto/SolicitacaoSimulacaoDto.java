package br.gov.caixa.simulaicaixa.dto;

import java.math.BigDecimal;

/**
 * Dados de entrada para solicitação de simulação de investimento.
 *
 * @param clienteId   identificador do cliente alvo da simulação
 * @param valor valor a ser investido
 * @param prazoMeses  prazo da simulação em meses
 * @param tipoProduto tipo de produto informado (ex.: CDB, FUNDO, LCI)
 */
public record SolicitacaoSimulacaoDto(
        Long clienteId,
        BigDecimal valor,
        Integer prazoMeses,
        String tipoProduto
) {
}