package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.data.repository.SimulacaoInvestimentoRepository;
import br.gov.caixa.simulaicaixa.domain.SimulacaoInvestimento;
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

    @Inject
    public SimulacaoServiceImpl(SimulacaoInvestimentoRepository simulacaoInvestimentoRepository) {
        this.simulacaoInvestimentoRepository = simulacaoInvestimentoRepository;
    }

    @Override
    public RespostaSimulacaoDto simularInvestimento(SolicitacaoSimulacaoDto solicitacao) {
        BigDecimal rentabilidade = obterRentabilidadeSimulada(solicitacao.tipoProduto());
        BigDecimal valorFinal = calcularValorFinal(solicitacao.valor(), rentabilidade, solicitacao.prazoMeses());

        SimulacaoInvestimento simulacao = criarSimulacao(
                solicitacao,
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
        return simulacaoInvestimentoRepository.listarTodas().stream()
                .map(this::mapearParaHistoricoDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SimulacaoPorProdutoDiaDto> listarSimulacoesPorProdutoEDia() {
        return simulacaoInvestimentoRepository.listarTodas().stream()
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

    private BigDecimal obterRentabilidadeSimulada(String tipoProduto) {
        if (tipoProduto == null) {
            return RENTABILIDADE_PADRAO;
        }

        String tipoNormalizado = tipoProduto.toUpperCase();

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
            BigDecimal rentabilidade,
            BigDecimal valorFinal,
            OffsetDateTime dataSimulacao
    ) {
        SimulacaoInvestimento simulacao = new SimulacaoInvestimento();

        simulacao.setClienteId(solicitacao.clienteId());
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
                simulacao.getTipoProduto(),
                rentabilidade,
                "Baixo"
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