package br.gov.caixa.simulaicaixa.core.web;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.erro.ErroResposta;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Provider
public class ExcecaoNegocioMapper implements ExceptionMapper<NegocioException> {

    @Override
    public Response toResponse(NegocioException exception) {
        CodigoErroNegocio codigoErro = exception.getCodigoErro();

        int status = codigoErro.getStatusHttp();

        ErroResposta corpo = new ErroResposta(
                codigoErro.name(),
                exception.getMessage(),
                exception.getDetalhes(),
                status,
                codigoErro.getCodigoInterno(),
                OffsetDateTime.now(ZoneOffset.UTC).toString()
        );

        return Response.status(status)
                .entity(corpo)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}