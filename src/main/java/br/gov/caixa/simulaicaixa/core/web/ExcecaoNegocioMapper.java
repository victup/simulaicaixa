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

/**
 * Mapper global para {@link NegocioException} na camada REST.
 * <p>
 * Converte exceções de negócio conhecidas em respostas HTTP padronizadas,
 * utilizando o modelo {@link ErroResposta} e os metadados definidos em
 * {@link CodigoErroNegocio} (código interno e status HTTP).
 * <p>
 */
@Provider
public class ExcecaoNegocioMapper implements ExceptionMapper<NegocioException> {

    /**
     * Converte uma {@link NegocioException} em uma resposta HTTP com corpo padronizado.
     * <p>
     * A resposta utiliza:
     * <ul>
     *     <li>Status HTTP definido em {@link CodigoErroNegocio#getStatusHttp()}.</li>
     *     <li>Nome do {@link CodigoErroNegocio} como campo {@code codigo}.</li>
     *     <li>Código interno ({@code getCodigoInterno()}) para rastreio de erros.</li>
     *     <li>Horário atual em UTC no campo {@code timestamp}.</li>
     * </ul>
     *
     * @param exception exceção de negócio lançada na camada de aplicação
     * @return resposta HTTP com o payload de {@link ErroResposta} em JSON
     */
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