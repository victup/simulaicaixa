package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.dto.TelemetriaDto;
import br.gov.caixa.simulaicaixa.service.TelemetriaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/telemetria")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TelemetriaResource {

    private final TelemetriaService telemetriaService;

    @Inject
    public TelemetriaResource(TelemetriaService telemetriaService) {
        this.telemetriaService = telemetriaService;
    }

    @GET
    public TelemetriaDto obterTelemetriaGeral() {
        return telemetriaService.obterTelemetriaGeral();
    }
}
