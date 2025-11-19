package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.dto.RequisicaoLoginDto;
import br.gov.caixa.simulaicaixa.dto.RespostaLoginDto;
import br.gov.caixa.simulaicaixa.service.AutenticacaoService;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.time.Duration;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AutenticacaoResource {

    private final AutenticacaoService autenticacaoService;
    private final ColetorTelemetria coletorTelemetria;

    @Inject
    public AutenticacaoResource(AutenticacaoService autenticacaoService,
                                ColetorTelemetria coletorTelemetria) {
        this.autenticacaoService = autenticacaoService;
        this.coletorTelemetria = coletorTelemetria;
    }

    @POST
    @Path("/login")
    @PermitAll
    public RespostaLoginDto login(RequisicaoLoginDto requisicao) {
        long inicio = System.nanoTime();

        RespostaLoginDto resposta = autenticacaoService.autenticar(requisicao);

        long fim = System.nanoTime();
        long duracaoMs = Duration.ofNanos(fim - inicio).toMillis();
        coletorTelemetria.registrarChamada("auth-login", duracaoMs);

        return resposta;
    }
}