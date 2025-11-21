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

/**
 * Serviço de recomendação e consulta de produtos de investimento.
 * <p>
 * Conecta o repositório de produtos ao motor de recomendação,
 * aplicando regras de perfil de risco e histórico de investimentos do cliente.
 */
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

    /**
     * Lista produtos de investimento recomendados para um perfil informado.
     *
     * @param perfil descrição do perfil de risco (por exemplo, "Conservador", "Moderado", "Agressivo").
     * @return lista de produtos recomendados ordenada por relevância.
     * @throws br.gov.caixa.simulaicaixa.core.excecao.NegocioException
     *         quando o perfil informado é inválido ou não há produtos compatíveis.
     */
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

    /**
     * Lista produtos recomendados para o cliente associado ao usuário autenticado.
     * <p>
     * A recomendação considera:
     * <ul>
     *     <li>Perfil de risco do cliente.</li>
     *     <li>Histórico de investimentos, quando disponível.</li>
     * </ul>
     *
     * @return lista de produtos recomendados ordenada por relevância.
     * @throws br.gov.caixa.simulaicaixa.core.excecao.NegocioException
     *         quando o cliente não possui perfil de risco ou histórico suficiente
     *         para gerar recomendações.
     */
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