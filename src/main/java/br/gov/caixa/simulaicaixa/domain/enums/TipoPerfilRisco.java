package br.gov.caixa.simulaicaixa.domain.enums;

public enum TipoPerfilRisco {

    CONSERVADOR,
    MODERADO,
    AGRESSIVO;

    public static TipoPerfilRisco fromDescricao(String texto) {
        if (texto == null) {
            return MODERADO;
        }

        String normalizado = texto.trim().toUpperCase();

        return switch (normalizado) {
            case "CONSERVADOR" -> CONSERVADOR;
            case "AGRESSIVO" -> AGRESSIVO;
            case "MODERADO" -> MODERADO;
            default -> MODERADO;
        };
    }
}