package br.gov.caixa.simulaicaixa.dto;

public record PerfilRiscoDto(
        Long clienteId,
        String perfil,
        Integer pontuacao,
        String descricao
) {
}