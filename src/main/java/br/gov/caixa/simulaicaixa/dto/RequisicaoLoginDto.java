package br.gov.caixa.simulaicaixa.dto;

/**
 * Dados de entrada para a operação de login.
 *
 * @param cpf CPF do usuário que está tentando autenticar
 * @param senha senha em texto simples enviada para validação
 */
public record RequisicaoLoginDto(
        String cpf,
        String senha
) {
}