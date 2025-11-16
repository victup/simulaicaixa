package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.data.repository.PerfilRiscoRepository;
import br.gov.caixa.simulaicaixa.domain.PerfilRisco;
import br.gov.caixa.simulaicaixa.dto.PerfilRiscoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PerfilRiscoServiceImpl implements PerfilRiscoService {

    private final PerfilRiscoRepository perfilRiscoRepository;

    @Inject
    public PerfilRiscoServiceImpl(PerfilRiscoRepository perfilRiscoRepository) {
        this.perfilRiscoRepository = perfilRiscoRepository;
    }

    @Override
    public PerfilRiscoDto obterPorClienteId(Long clienteId) {
        PerfilRisco perfil = perfilRiscoRepository.obterPorClienteId(clienteId);

        return new PerfilRiscoDto(
                perfil.getClienteId(),
                perfil.getPerfil(),
                perfil.getPontuacao(),
                perfil.getDescricao()
        );
    }
}
