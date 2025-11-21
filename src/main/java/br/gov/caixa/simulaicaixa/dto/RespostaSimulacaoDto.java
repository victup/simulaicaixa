package br.gov.caixa.simulaicaixa.dto;

import java.time.OffsetDateTime;

/**
 * Resultado completo de uma simulação de investimento.
 *
 * @param produtoValidado produto utilizado na simulação, já validado
 * @param resultadoSimulacao valores calculados da simulação
 * @param dataSimulacao data e hora em que a simulação foi efetuada
 */
public record RespostaSimulacaoDto(
        ProdutoValidadoDto produtoValidado,
        ResultadoSimulacaoDto resultadoSimulacao,
        OffsetDateTime dataSimulacao
) {
}