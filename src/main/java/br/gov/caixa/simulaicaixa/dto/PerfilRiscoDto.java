package br.gov.caixa.simulaicaixa.dto;

/**
 * Dados do perfil de risco de um cliente.
 *
 * @param clienteId identificador do cliente
 * @param perfil descrição do perfil (ex.: Conservador, Moderado, Agressivo)
 * @param pontuacao pontuação obtida na avaliação de risco
 * @param descricao descrição complementar do perfil
 */
public record PerfilRiscoDto(
        Long clienteId,
        String perfil,
        Integer pontuacao,
        String descricao
) {
}