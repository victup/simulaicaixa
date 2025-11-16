package br.gov.caixa.simulaicaixa.dto;

import java.time.OffsetDateTime;

public record RespostaSimulacaoDto(
        ProdutoValidadoDto produtoValidado,
        ResultadoSimulacaoDto resultadoSimulacao,
        OffsetDateTime dataSimulacao
) {
}