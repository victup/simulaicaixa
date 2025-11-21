package br.gov.caixa.simulaicaixa.dto;

/**
 * Resposta da operação de autenticação com o token emitido.
 *
 * @param token token JWT gerado após autenticação bem-sucedida
 * @param tipoToken tipo do token (por padrão, "Bearer")
 * @param expiraEmSegundos tempo de expiração do token em segundos
 */
public record RespostaLoginDto(
        String token,
        String tipoToken,
        long expiraEmSegundos
) {
}