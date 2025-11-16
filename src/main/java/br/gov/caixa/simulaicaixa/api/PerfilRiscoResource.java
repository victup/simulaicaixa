package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.dto.PerfilRiscoDto;
import br.gov.caixa.simulaicaixa.service.PerfilRiscoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/perfil-risco")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PerfilRiscoResource {

    private final PerfilRiscoService perfilRiscoService;

    @Inject
    public PerfilRiscoResource(PerfilRiscoService perfilRiscoService) {
        this.perfilRiscoService = perfilRiscoService;
    }

    @GET
    @Path("/{clienteId}")
    public PerfilRiscoDto obterPerfilRisco(@PathParam("clienteId") Long clienteId) {
        return perfilRiscoService.obterPorClienteId(clienteId);
    }
}
