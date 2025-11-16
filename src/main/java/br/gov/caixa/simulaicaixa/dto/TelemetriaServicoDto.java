package br.gov.caixa.simulaicaixa.dto;

public record TelemetriaServicoDto(
        String nome,
        Long quantidadeChamadas,
        Long mediaTempoRespostaMs
) {
}