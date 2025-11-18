package br.gov.caixa.simulaicaixa.dto;

public record RespostaLoginDto(
        String token,
        String tipoToken,
        long expiraEmSegundos
) {
}