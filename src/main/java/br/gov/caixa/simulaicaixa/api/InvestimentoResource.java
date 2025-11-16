package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.dto.InvestimentoHistoricoDto;
import br.gov.caixa.simulaicaixa.service.InvestimentoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/investimentos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InvestimentoResource {

    private final InvestimentoService investimentoService;

    @Inject
    public InvestimentoResource(InvestimentoService investimentoService) {
        this.investimentoService = investimentoService;
    }

    @GET
    @Path("/{clienteId}")
    public List<InvestimentoHistoricoDto> listarPorCliente(@PathParam("clienteId") Long clienteId) {
        return investimentoService.listarPorClienteId(clienteId);
    }
}
