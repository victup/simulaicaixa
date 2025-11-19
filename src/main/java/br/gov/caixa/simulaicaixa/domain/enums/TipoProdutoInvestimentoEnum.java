package br.gov.caixa.simulaicaixa.domain.enums;

import java.util.Locale;

public enum TipoProdutoInvestimentoEnum {

    CDB("CDB"),
    LCI("LCI"),
    LCA("LCA"),
    TESOURO_DIRETO("Tesouro Direto"),
    FUNDO("Fundo"),
    OUTRO("Outro");

    private final String descricao;

    TipoProdutoInvestimentoEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoProdutoInvestimentoEnum obterPorDescricao(String descricao) {
        if (descricao == null) {
            return null;
        }

        String valor = descricao.trim().toUpperCase(Locale.ROOT);

        return switch (valor) {
            case "CDB" -> CDB;
            case "LCI" -> LCI;
            case "LCA" -> LCA;
            case "TESOURO", "TESOURO_DIRETO", "TESOURO DIRETO" -> TESOURO_DIRETO;
            case "FUNDO", "FUNDO MULTIMERCADO" -> FUNDO;
            default -> OUTRO;
        };
    }
}