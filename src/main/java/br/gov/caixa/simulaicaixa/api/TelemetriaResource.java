package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.dto.TelemetriaDto;
import br.gov.caixa.simulaicaixa.service.TelemetriaService;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.Duration;
import java.time.LocalDate;

@Path("/telemetria")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TelemetriaResource {

    private final TelemetriaService telemetriaService;
    private final ColetorTelemetria coletorTelemetria;

    @Inject
    public TelemetriaResource(TelemetriaService telemetriaService,
                              ColetorTelemetria coletorTelemetria) {
        this.telemetriaService = telemetriaService;
        this.coletorTelemetria = coletorTelemetria;
    }

    @GET
    public TelemetriaDto obterTelemetria() {
        long inicio = System.nanoTime();

        TelemetriaDto telemetria = telemetriaService.obterTelemetriaGeral();

        long fim = System.nanoTime();
        long duracaoMs = Duration.ofNanos(fim - inicio).toMillis();

        coletorTelemetria.registrarChamada("telemetria", duracaoMs);

        return telemetria;
    }

    @GET
    @Path("/resumo")
    public TelemetriaDto obterResumoPorPeriodo(
            @QueryParam("inicio") LocalDate inicio,
            @QueryParam("fim") LocalDate fim) {

        long inicioNanos = System.nanoTime();

        TelemetriaDto telemetria = telemetriaService.obterResumoPorPeriodo(inicio, fim);

        long fimNanos = System.nanoTime();
        long duracaoMs = Duration.ofNanos(fimNanos - inicioNanos).toMillis();

        coletorTelemetria.registrarChamada("telemetria-resumo", duracaoMs);

        return telemetria;
    }
}