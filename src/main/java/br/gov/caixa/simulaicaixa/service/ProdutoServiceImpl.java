package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import br.gov.caixa.simulaicaixa.data.repository.InvestimentoRepository;
import br.gov.caixa.simulaicaixa.data.repository.PerfilRiscoRepository;
import br.gov.caixa.simulaicaixa.data.repository.ProdutoRepository;
import br.gov.caixa.simulaicaixa.domain.Investimento;
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
    private final InvestimentoRepository investimentoRepository;
    private final PerfilRiscoRepository perfilRiscoRepository;
    private final ContextoClienteService contextoClienteService;
    private final MotorRecomendacaoService motorRecomendacaoService;

    @Inject
    public ProdutoServiceImpl(ProdutoRepository produtoRepository,
                              InvestimentoRepository investimentoRepository,
                              PerfilRiscoRepository perfilRiscoRepository,
                              ContextoClienteService contextoClienteService,
                              MotorRecomendacaoService motorRecomendacaoService) {
        this.produtoRepository = produtoRepository;
        this.investimentoRepository = investimentoRepository;
        this.perfilRiscoRepository = perfilRiscoRepository;
        this.contextoClienteService = contextoClienteService;
        this.motorRecomendacaoService = motorRecomendacaoService;
    }

    @Override
    public List<ProdutoRecomendadoDto> listarProdutosRecomendadosPorPerfil(String perfil) {
        TipoPerfilRiscoEnum tipoPerfil = TipoPerfilRiscoEnum.obterPorDescricao(perfil);

        if (tipoPerfil == null) {
            throw new NegocioException(
                    CodigoErroNegocio.PERFIL_INVALIDO,
                    "Perfil informado \"" + perfil + "\" é inválido."
            );
        }

        Integer codigoPerfil = tipoPerfil.getCodigo();

        List<ProdutoInvestimento> produtos = produtoRepository.listarPorPerfil(codigoPerfil);

        if (produtos == null || produtos.isEmpty()) {
            throw new NegocioException(
                    CodigoErroNegocio.PRODUTOS_NAO_ENCONTRADOS_PARA_PERFIL,
                    "Não há produtos compatíveis com o perfil " + tipoPerfil.getDescricao() + "."
            );
        }

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

    @Override
    public List<ProdutoRecomendadoDto> listarProdutosRecomendadosParaClienteAtual() {
        Long clienteId = contextoClienteService.obterClienteIdUsuarioObrigatorio();

        PerfilRisco perfilRisco = perfilRiscoRepository.obterPorClienteId(clienteId);

        if (perfilRisco == null || perfilRisco.getTipoPerfilRisco() == null) {
            throw new NegocioException(
                    CodigoErroNegocio.CLIENTE_SEM_PERFIL_RISCO,
                    "Cliente " + clienteId + " não possui perfil de risco cadastrado."
            );
        }

        List<Investimento> historico = investimentoRepository.listarPorClienteId(clienteId);

        Integer codigoPerfil = perfilRisco.getTipoPerfilRisco().getCodigo();

        List<ProdutoInvestimento> produtosBase = produtoRepository.listarPorPerfil(codigoPerfil);

        if (produtosBase == null || produtosBase.isEmpty()) {
            throw new NegocioException(
                    CodigoErroNegocio.PRODUTOS_NAO_ENCONTRADOS_PARA_PERFIL,
                    "Não há produtos compatíveis com o perfil " + perfilRisco.getTipoPerfilRisco().getDescricao() + "."
            );
        }

        List<ProdutoInvestimento> produtosOrdenados =
                motorRecomendacaoService.recomendarPorPerfilEHistorico(
                        perfilRisco,
                        produtosBase,
                        historico
                );

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