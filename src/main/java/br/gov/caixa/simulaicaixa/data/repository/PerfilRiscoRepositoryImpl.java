package br.gov.caixa.simulaicaixa.data.repository;

import br.gov.caixa.simulaicaixa.domain.PerfilRisco;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PerfilRiscoRepositoryImpl implements PerfilRiscoRepository {

    @Override
    public PerfilRisco obterPorClienteId(Long clienteId) {
        PerfilRisco perfil = new PerfilRisco();

        perfil.setClienteId(clienteId);
        perfil.setPerfil("Moderado");
        perfil.setPontuacao(65);
        perfil.setDescricao("Perfil equilibrado entre segurança e rentabilidade.");

        return perfil;
    }
}