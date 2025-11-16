package br.gov.caixa.simulaicaixa.data.repository;

import br.gov.caixa.simulaicaixa.domain.Investimento;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class InvestimentoRepositoryImpl implements InvestimentoRepository {

    private final List<Investimento> investimentos;

    public InvestimentoRepositoryImpl() {
        this.investimentos = criarInvestimentosEmMemoria();
    }

    @Override
    public List<Investimento> listarPorClienteId(Long clienteId) {
        return investimentos.stream()
                .filter(investimento -> investimento.getClienteId().equals(clienteId))
                .collect(Collectors.toList());
    }

    private List<Investimento> criarInvestimentosEmMemoria() {
        Investimento investimento1 = new Investimento();
        investimento1.setId(1L);
        investimento1.setClienteId(123L);
        investimento1.setTipo("CDB");
        investimento1.setValor(new BigDecimal("5000.00"));
        investimento1.setRentabilidade(new BigDecimal("0.12"));
        investimento1.setData(LocalDate.of(2025, 1, 15));

        Investimento investimento2 = new Investimento();
        investimento2.setId(2L);
        investimento2.setClienteId(123L);
        investimento2.setTipo("Fundo Multimercado");
        investimento2.setValor(new BigDecimal("3000.00"));
        investimento2.setRentabilidade(new BigDecimal("0.08"));
        investimento2.setData(LocalDate.of(2025, 3, 10));

        Investimento investimentoOutroCliente = new Investimento();
        investimentoOutroCliente.setId(3L);
        investimentoOutroCliente.setClienteId(999L);
        investimentoOutroCliente.setTipo("CDB");
        investimentoOutroCliente.setValor(new BigDecimal("2000.00"));
        investimentoOutroCliente.setRentabilidade(new BigDecimal("0.10"));
        investimentoOutroCliente.setData(LocalDate.of(2025, 2, 1));

        return List.of(
                investimento1,
                investimento2,
                investimentoOutroCliente
        );
    }
}
