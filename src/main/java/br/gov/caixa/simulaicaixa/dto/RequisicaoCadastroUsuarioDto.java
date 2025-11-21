package br.gov.caixa.simulaicaixa.dto;

/**
 * Dados de entrada para cadastro de um novo usuário autenticável.
 *
 * @param cpf       CPF do usuário que será cadastrado.
 * @param senha     Senha em texto puro, que será armazenada como hash.
 * @param grupos    Grupos de acesso (por exemplo, {@code "cliente"} ou {@code "cliente,admin"}).
 * @param clienteId Identificador opcional do cliente vinculado a este usuário.
 */
public record RequisicaoCadastroUsuarioDto(
        String cpf,
        String senha,
        String grupos,
        Long clienteId
) {
}