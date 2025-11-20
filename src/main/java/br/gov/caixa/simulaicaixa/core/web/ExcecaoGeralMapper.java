package br.gov.caixa.simulaicaixa.core.web;

import br.gov.caixa.simulaicaixa.core.erro.ErroResposta;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Provider
public class ExcecaoGeralMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        int status = 500;

        ErroResposta corpo = new ErroResposta(
                "ERRO_INTERNO",
                "Erro inesperado ao processar a requisição.",
                exception.getMessage(),
                status,
                "GEN-0001",
                OffsetDateTime.now(ZoneOffset.UTC).toString()
        );

        return Response.status(status)
                .entity(corpo)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}