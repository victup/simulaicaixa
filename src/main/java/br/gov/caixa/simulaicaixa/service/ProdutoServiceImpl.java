package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.data.repository.ProdutoRepository;
import br.gov.caixa.simulaicaixa.domain.PerfilRisco;
import br.gov.caixa.simulaicaixa.domain.ProdutoInvestimento;
import br.gov.caixa.simulaicaixa.domain.enums.TipoPerfilRiscoEnum;
import br.gov.caixa.simulaicaixa.dto.ProdutoRecomendadoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final MotorRecomendacaoService motorRecomendacaoService;

    @Inject
    public ProdutoServiceImpl(ProdutoRepository produtoRepository,
                              MotorRecomendacaoService motorRecomendacaoService) {
        this.produtoRepository = produtoRepository;
        this.motorRecomendacaoService = motorRecomendacaoService;
    }

    @Override
    public List<ProdutoRecomendadoDto> listarProdutosRecomendadosPorPerfil(String perfil) {
        TipoPerfilRiscoEnum tipoPerfil = TipoPerfilRiscoEnum.obterPorDescricao(perfil);

        Integer codigoPerfil = tipoPerfil != null ? tipoPerfil.getCodigo() : null;

        List<ProdutoInvestimento> produtos =
                produtoRepository.listarPorPerfil(codigoPerfil);

        PerfilRisco perfilRisco = new PerfilRisco();
        perfilRisco.setTipoPerfilRisco(tipoPerfil);

        List<ProdutoInvestimento> produtosOrdenados =
                motorRecomendacaoService.recomendarPorPerfil(perfilRisco, produtos);

        return produtosOrdenados.stream()
                .map(produto -> new ProdutoRecomendadoDto(
                        produto.getId(),
                        produto.getNome(),
                        produto.getTipo(),
                        produto.getRentabilidade(),
                        produto.getRiscoDescricao()
                ))
                .toList();
    }
}