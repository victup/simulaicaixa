package br.gov.caixa.simulaicaixa.dto;

/**
 * Dados retornados após o cadastro de um usuário.
 *
 * @param id        Identificador gerado para o usuário.
 * @param cpf       CPF cadastrado.
 * @param grupos    Grupos de acesso associados ao usuário.
 * @param clienteId Identificador do cliente vinculado, quando houver.
 */
public record RespostaCadastroUsuarioDto(
        Long id,
        String cpf,
        String grupos,
        Long clienteId
) {
}