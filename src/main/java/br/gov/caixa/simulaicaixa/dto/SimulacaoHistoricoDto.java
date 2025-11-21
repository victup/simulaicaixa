package br.gov.caixa.simulaicaixa.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Registro de uma simulação já realizada, utilizado em histórico.
 *
 * @param id identificador da simulação
 * @param clienteId identificador do cliente associado
 * @param produto nome do produto simulado
 * @param valorInvestido valor originalmente investido
 * @param valorFinal valorr projetado ao final do prazo
 * @param prazoMeses prazo considerado, em meses
 * @param dataSimulacao data e hora em que a simulação foi executada
 */
public record SimulacaoHistoricoDto(
        Long id,
        Long clienteId,
        String produto,
        BigDecimal valorInvestido,
        BigDecimal valorFinal,
        Integer prazoMeses,
        OffsetDateTime dataSimulacao
) {
}