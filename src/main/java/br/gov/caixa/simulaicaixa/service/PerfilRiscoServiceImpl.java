package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import br.gov.caixa.simulaicaixa.data.repository.PerfilRiscoRepository;
import br.gov.caixa.simulaicaixa.domain.PerfilRisco;
import br.gov.caixa.simulaicaixa.dto.PerfilRiscoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Serviço responsável pelo acesso ao perfil de risco dos clientes.
 * <p>
 * Centraliza a lógica de leitura do perfil, incluindo validações
 * e mapeamento para o DTO exposto na camada de API.
 */
@ApplicationScoped
public class PerfilRiscoServiceImpl implements PerfilRiscoService {

    private final PerfilRiscoRepository perfilRiscoRepository;

    @Inject
    public PerfilRiscoServiceImpl(PerfilRiscoRepository perfilRiscoRepository) {
        this.perfilRiscoRepository = perfilRiscoRepository;
    }

    /**
     * Obtém o perfil de risco de um cliente.
     *
     * @param clienteId identificador do cliente.
     * @return perfil de risco do cliente.
     * @throws br.gov.caixa.simulaicaixa.core.excecao.NegocioException
     *         quando o cliente não possui perfil de risco cadastrado
     *         ou os dados não estão consistentes.
     */
    @Override
    public PerfilRiscoDto obterPorClienteId(Long clienteId) {
        PerfilRisco perfil = perfilRiscoRepository.obterPorClienteId(clienteId);

        if (perfil == null || perfil.getTipoPerfilRisco() == null) {
            throw new NegocioException(
                    CodigoErroNegocio.CLIENTE_SEM_PERFIL_RISCO,
                    "Cliente " + clienteId + " não possui perfil de risco cadastrado."
            );
        }

        return new PerfilRiscoDto(
                perfil.getClienteId(),
                perfil.getPerfil(),
                perfil.getPontuacao(),
                perfil.getDescricao()
        );
    }
}