package br.gov.caixa.simulaicaixa.dto;

/**
 * Métricas de telemetria para um serviço específico da aplicação.
 *
 * @param nome nome lógico do serviço monitorado
 * @param quantidadeChamadas   quantidade total de chamadas registradas
 * @param mediaTempoRespostaMs tempo médio de resposta em milissegundos
 */
public record TelemetriaServicoDto(
        String nome,
        Long quantidadeChamadas,
        Long mediaTempoRespostaMs
) {
}