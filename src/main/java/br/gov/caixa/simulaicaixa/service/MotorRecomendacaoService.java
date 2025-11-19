package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.domain.Investimento;
import br.gov.caixa.simulaicaixa.domain.PerfilRisco;
import br.gov.caixa.simulaicaixa.domain.ProdutoInvestimento;

import java.util.List;

public interface MotorRecomendacaoService {

    List<ProdutoInvestimento> recomendarPorPerfil(
            PerfilRisco perfilRisco,
            List<ProdutoInvestimento> produtos
    );

    List<ProdutoInvestimento> recomendarPorPerfilEHistorico(
            PerfilRisco perfilRisco,
            List<ProdutoInvestimento> produtos,
            List<Investimento> historicoCliente
    );
}