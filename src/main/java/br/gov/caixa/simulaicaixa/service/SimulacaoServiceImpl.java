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

    private final SimulacaoInvestimentoRepository simulacaoInvestimentoRepository;

    @Inject
    public SimulacaoServiceImpl(SimulacaoInvestimentoRepository simulacaoInvestimentoRepository) {
        this.simulacaoInvestimentoRepository = simulacaoInvestimentoRepository;
    }

    @Override
    public RespostaSimulacaoDto simularInvestimento(SolicitacaoSimulacaoDto solicitacao) {
        BigDecimal rentabilidade = obterRentabilidadeSimulada(solicitacao.tipoProduto());

        BigDecimal fatorTempo = BigDecimal.valueOf(solicitacao.prazoMeses())
                .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);

        BigDecimal fator = BigDecimal.ONE.add(rentabilidade.multiply(fatorTempo));
        BigDecimal valorFinal = solicitacao.valor()
                .multiply(fator)
                .setScale(2, RoundingMode.HALF_UP);

        SimulacaoInvestimento simulacao = new SimulacaoInvestimento();
        simulacao.setClienteId(solicitacao.clienteId());
        simulacao.setNomeProduto("Produto " + solicitacao.tipoProduto());
        simulacao.setTipoProduto(solicitacao.tipoProduto());
        simulacao.setValorInvestido(solicitacao.valor());
        simulacao.setValorFinal(valorFinal);
        simulacao.setPrazoMeses(solicitacao.prazoMeses());
        simulacao.setDataSimulacao(OffsetDateTime.now());

        SimulacaoInvestimento simulacaoSalva = simulacaoInvestimentoRepository.salvar(simulacao);

        ProdutoValidadoDto produtoValidado = new ProdutoValidadoDto(
                simulacaoSalva.getId(),
                simulacaoSalva.getNomeProduto(),
                simulacaoSalva.getTipoProduto(),
                rentabilidade,
                "Baixo"
        );

        ResultadoSimulacaoDto resultadoSimulacao = new ResultadoSimulacaoDto(
                simulacaoSalva.getValorFinal(),
                rentabilidade,
                simulacaoSalva.getPrazoMeses()
        );

        return new RespostaSimulacaoDto(
                produtoValidado,
                resultadoSimulacao,
                simulacaoSalva.getDataSimulacao()
        );
    }

    @Override
    public List<SimulacaoHistoricoDto> listarSimulacoes() {
        return simulacaoInvestimentoRepository.listarTodas().stream()
                .map(simulacao -> new SimulacaoHistoricoDto(
                        simulacao.getId(),
                        simulacao.getClienteId(),
                        simulacao.getNomeProduto(),
                        simulacao.getValorInvestido(),
                        simulacao.getValorFinal(),
                        simulacao.getPrazoMeses(),
                        simulacao.getDataSimulacao()
                ))
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
                .map(entry -> {
                    String produto = entry.getKey().produto();
                    LocalDate data = entry.getKey().data();
                    List<SimulacaoInvestimento> simulacoes = entry.getValue();

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
                })
                .collect(Collectors.toList());
    }

    private BigDecimal obterRentabilidadeSimulada(String tipoProduto) {
        if (tipoProduto == null) {
            return new BigDecimal("0.10");
        }

        String tipoNormalizado = tipoProduto.toUpperCase();

        if ("CDB".equals(tipoNormalizado)) {
            return new BigDecimal("0.12");
        }

        if (tipoNormalizado.contains("FUNDO")) {
            return new BigDecimal("0.18");
        }

        return new BigDecimal("0.10");
    }

    private record ChaveProdutoDia(String produto, LocalDate data) {
    }
}
