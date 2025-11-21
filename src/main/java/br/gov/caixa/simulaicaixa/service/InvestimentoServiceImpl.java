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

/**
 * Serviço de consulta de investimentos.
 * <p>
 * Responsável por buscar o histórico de investimentos de um cliente
 * e expor os dados em formato adequado para a camada de API.
 */
@ApplicationScoped
public class InvestimentoServiceImpl implements InvestimentoService {

    private final InvestimentoRepository investimentoRepository;

    @Inject
    public InvestimentoServiceImpl(InvestimentoRepository investimentoRepository) {
        this.investimentoRepository = investimentoRepository;
    }

    /**
     * Lista o histórico de investimentos de um cliente.
     *
     * @param clienteId identificador do cliente.
     * @return lista de investimentos do cliente em ordem cronológica.
     * @throws br.gov.caixa.simulaicaixa.core.excecao.NegocioException
     *         quando o cliente não é encontrado ou não possui histórico suficiente.
     */
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