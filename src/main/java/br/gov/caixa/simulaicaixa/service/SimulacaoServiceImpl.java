package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import br.gov.caixa.simulaicaixa.data.repository.SimulacaoInvestimentoRepository;
import br.gov.caixa.simulaicaixa.domain.SimulacaoInvestimento;
import br.gov.caixa.simulaicaixa.domain.enums.NivelRiscoProdutoEnum;
import br.gov.caixa.simulaicaixa.dto.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SimulacaoServiceImpl implements SimulacaoService {

    private static final BigDecimal RENTABILIDADE_PADRAO = new BigDecimal("0.10");
    private static final BigDecimal RENTABILIDADE_CDB = new BigDecimal("0.12");
    private static final BigDecimal RENTABILIDADE_FUNDO = new BigDecimal("0.18");

    private final SimulacaoInvestimentoRepository simulacaoInvestimentoRepository;
    private final ContextoClienteService contextoClienteService;

    @Inject
    public SimulacaoServiceImpl(SimulacaoInvestimentoRepository simulacaoInvestimentoRepository,
                                ContextoClienteService contextoClienteService) {
        this.simulacaoInvestimentoRepository = simulacaoInvestimentoRepository;
        this.contextoClienteService = contextoClienteService;
    }

    @Override
    public RespostaSimulacaoDto simularInvestimento(SolicitacaoSimulacaoDto solicitacao) {
        validarSolicitacaoSimulacao(solicitacao);

        Long clienteIdEfetivo = resolverClienteIdParaSimulacao(solicitacao);

        BigDecimal rentabilidade = obterRentabilidadeSimulada(solicitacao.tipoProduto());
        BigDecimal valorFinal = calcularValorFinal(solicitacao.valor(), rentabilidade, solicitacao.prazoMeses());

        SimulacaoInvestimento simulacao = criarSimulacao(
                solicitacao,
                clienteIdEfetivo,
                rentabilidade,
                valorFinal,
                OffsetDateTime.now()
        );

        SimulacaoInvestimento simulacaoSalva = simulacaoInvestimentoRepository.salvar(simulacao);

        ProdutoValidadoDto produtoValidado = criarProdutoValidadoDto(simulacaoSalva, rentabilidade);
        ResultadoSimulacaoDto resultadoSimulacao = criarResultadoSimulacaoDto(simulacaoSalva, rentabilidade);

        return new RespostaSimulacaoDto(
                produtoValidado,
                resultadoSimulacao,
                simulacaoSalva.getDataSimulacao()
        );
    }

    @Override
    public List<SimulacaoHistoricoDto> listarSimulacoes() {
        List<SimulacaoInvestimento> simulacoes = simulacaoInvestimentoRepository.listarTodas();

        if (!contextoClienteService.usuarioAtualEhAdmin()) {
            Long clienteId = contextoClienteService.obterClienteIdUsuarioObrigatorio();

            simulacoes = simulacoes.stream()
                    .filter(s -> clienteId.equals(s.getClienteId()))
                    .toList();
        }

        return simulacoes.stream()
                .map(this::mapearParaHistoricoDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SimulacaoPorProdutoDiaDto> listarSimulacoesPorProdutoEDia() {
        List<SimulacaoInvestimento> base = simulacaoInvestimentoRepository.listarTodas();

        if (!contextoClienteService.usuarioAtualEhAdmin()) {
            Long clienteId = contextoClienteService.obterClienteIdUsuarioObrigatorio();

            base = base.stream()
                    .filter(s -> clienteId.equals(s.getClienteId()))
                    .toList();
        }

        return base.stream()
                .collect(Collectors.groupingBy(simulacao ->
                        new ChaveProdutoDia(
                                simulacao.getNomeProduto(),
                                simulacao.getDataSimulacao() != null
                                        ? simulacao.getDataSimulacao().toLocalDate()
                                        : null
                        )
                ))
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().data() != null)
                .map(entry -> criarSimulacaoPorProdutoDiaDto(
                        entry.getKey().produto(),
                        entry.getKey().data(),
                        entry.getValue()
                ))
                .collect(Collectors.toList());
    }

    private void validarSolicitacaoSimulacao(SolicitacaoSimulacaoDto solicitacao) {
        if (solicitacao == null) {
            throw new NegocioException(
                    CodigoErroNegocio.SIMULACAO_DADOS_INVALIDOS,
                    "Dados da simulação não foram informados."
            );
        }

        BigDecimal valor = solicitacao.valor();
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegocioException(
                    CodigoErroNegocio.SIMULACAO_DADOS_INVALIDOS,
                    "Valor do investimento deve ser maior que zero."
            );
        }

        int prazo = solicitacao.prazoMeses();
        if (prazo <= 0) {
            throw new NegocioException(
                    CodigoErroNegocio.SIMULACAO_DADOS_INVALIDOS,
                    "Prazo da simulação em meses deve ser maior que zero."
            );
        }

        String tipoProduto = solicitacao.tipoProduto();
        if (tipoProduto == null || tipoProduto.trim().isEmpty()) {
            throw new NegocioException(
                    CodigoErroNegocio.SIMULACAO_DADOS_INVALIDOS,
                    "Tipo de produto para simulação deve ser informado."
            );
        }
    }

    private Long resolverClienteIdParaSimulacao(SolicitacaoSimulacaoDto solicitacao) {
        boolean usuarioEhAdmin = contextoClienteService.usuarioAtualEhAdmin();

        if (usuarioEhAdmin) {
            return solicitacao.clienteId();
        }

        return contextoClienteService.obterClienteIdUsuarioObrigatorio();
    }

    private BigDecimal obterRentabilidadeSimulada(String tipoProduto) {
        if (tipoProduto == null) {
            return RENTABILIDADE_PADRAO;
        }

        String tipoNormalizado = tipoProduto.trim().toUpperCase();

        if ("CDB".equals(tipoNormalizado)) {
            return RENTABILIDADE_CDB;
        }

        if (tipoNormalizado.contains("FUNDO")) {
            return RENTABILIDADE_FUNDO;
        }

        return RENTABILIDADE_PADRAO;
    }

    private BigDecimal calcularValorFinal(BigDecimal valor, BigDecimal rentabilidade, int prazoMeses) {
        BigDecimal fatorTempo = BigDecimal.valueOf(prazoMeses)
                .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);

        BigDecimal fator = BigDecimal.ONE.add(rentabilidade.multiply(fatorTempo));

        return valor
                .multiply(fator)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private SimulacaoInvestimento criarSimulacao(
            SolicitacaoSimulacaoDto solicitacao,
            Long clienteIdEfetivo,
            BigDecimal rentabilidade,
            BigDecimal valorFinal,
            OffsetDateTime dataSimulacao
    ) {
        SimulacaoInvestimento simulacao = new SimulacaoInvestimento();

        simulacao.setClienteId(clienteIdEfetivo);
        simulacao.setNomeProduto("Produto " + solicitacao.tipoProduto());
        simulacao.setTipoProduto(solicitacao.tipoProduto());
        simulacao.setValorInvestido(solicitacao.valor());
        simulacao.setValorFinal(valorFinal);
        simulacao.setPrazoMeses(solicitacao.prazoMeses());
        simulacao.setDataSimulacao(dataSimulacao);

        return simulacao;
    }

    private ProdutoValidadoDto criarProdutoValidadoDto(SimulacaoInvestimento simulacao, BigDecimal rentabilidade) {
        return new ProdutoValidadoDto(
                simulacao.getId(),
                simulacao.getNomeProduto(),
                simulacao.getTipoProduto().getDescricao(),
                rentabilidade,
                NivelRiscoProdutoEnum.BAIXO.getDescricao()
        );
    }

    private ResultadoSimulacaoDto criarResultadoSimulacaoDto(SimulacaoInvestimento simulacao, BigDecimal rentabilidade) {
        return new ResultadoSimulacaoDto(
                simulacao.getValorFinal(),
                rentabilidade,
                simulacao.getPrazoMeses()
        );
    }

    private SimulacaoHistoricoDto mapearParaHistoricoDto(SimulacaoInvestimento simulacao) {
        return new SimulacaoHistoricoDto(
                simulacao.getId(),
                simulacao.getClienteId(),
                simulacao.getNomeProduto(),
                simulacao.getValorInvestido(),
                simulacao.getValorFinal(),
                simulacao.getPrazoMeses(),
                simulacao.getDataSimulacao()
        );
    }

    private SimulacaoPorProdutoDiaDto criarSimulacaoPorProdutoDiaDto(
            String produto,
            LocalDate data,
            List<SimulacaoInvestimento> simulacoes
    ) {
        long quantidade = simulacoes.size();

        BigDecimal somaValorFinal = simulacoes.stream()
                .map(SimulacaoInvestimento::getValorFinal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal mediaValorFinal = quantidade == 0
                ? BigDecimal.ZERO
                : somaValorFinal.divide(BigDecimal.valueOf(quantidade), 2, RoundingMode.HALF_UP);

        return new SimulacaoPorProdutoDiaDto(
                produto,
                data,
                quantidade,
                mediaValorFinal
        );
    }

    private record ChaveProdutoDia(String produto, LocalDate data) {
    }
}