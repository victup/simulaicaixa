package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import br.gov.caixa.simulaicaixa.data.repository.InvestimentoRepository;
import br.gov.caixa.simulaicaixa.domain.Investimento;
import br.gov.caixa.simulaicaixa.dto.InvestimentoHistoricoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class InvestimentoServiceImpl implements InvestimentoService {

    private final InvestimentoRepository investimentoRepository;

    @Inject
    public InvestimentoServiceImpl(InvestimentoRepository investimentoRepository) {
        this.investimentoRepository = investimentoRepository;
    }

    @Override
    public List<InvestimentoHistoricoDto> listarPorClienteId(Long clienteId) {
        List<Investimento> investimentos = investimentoRepository.listarPorClienteId(clienteId);

        if (investimentos == null || investimentos.isEmpty()) {
            throw new NegocioException(
                    CodigoErroNegocio.CLIENTE_SEM_HISTORICO_INVESTIMENTOS,
                    "Cliente " + clienteId + " não possui histórico de investimentos suficiente para recomendação."
            );
        }

        return investimentos.stream()
                .map(investimento -> new InvestimentoHistoricoDto(
                        investimento.getId(),
                        investimento.getTipo().getDescricao(),
                        investimento.getValor(),
                        investimento.getRentabilidade(),
                        investimento.getData()
                ))
                .collect(Collectors.toList());
    }
}