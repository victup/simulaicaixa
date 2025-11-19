package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.dto.InvestimentoHistoricoDto;
import br.gov.caixa.simulaicaixa.service.InvestimentoService;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.Duration;
import java.util.List;

@Path("/investimentos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"cliente", "admin"})
public class InvestimentoResource {

    private final InvestimentoService investimentoService;
    private final ColetorTelemetria coletorTelemetria;

    @Inject
    public InvestimentoResource(InvestimentoService investimentoService,
                                ColetorTelemetria coletorTelemetria) {
        this.investimentoService = investimentoService;
        this.coletorTelemetria = coletorTelemetria;
    }

    @GET
    @Path("/{clienteId}")
    public List<InvestimentoHistoricoDto> listarPorCliente(@PathParam("clienteId") Long clienteId) {
        long inicio = System.nanoTime();

        List<InvestimentoHistoricoDto> investimentos = investimentoService.listarPorClienteId(clienteId);

        long fim = System.nanoTime();
        long duracaoMs = Duration.ofNanos(fim - inicio).toMillis();

        coletorTelemetria.registrarChamada("investimentos-por-cliente", duracaoMs);

        return investimentos;
    }
}