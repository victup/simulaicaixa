package br.gov.caixa.simulaicaixa.core.web;

import br.gov.caixa.simulaicaixa.core.erro.ErroResposta;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Mapper global de fallback para exceções não tratadas explicitamente.
 * <p>
 * Qualquer exceção que não seja um {@link br.gov.caixa.simulaicaixa.core.excecao.NegocioException}
 * e que alcance a camada JAX-RS será convertida em uma resposta HTTP 500
 * com um corpo de erro padronizado ({@link ErroResposta}).
 * <p>
 * Evita vazamento de detalhes internos da aplicação e fornece
 * uma resposta consistente para falhas inesperadas.
 */
@Provider
public class ExcecaoGeralMapper implements ExceptionMapper<Throwable> {

    /**
     * Converte qualquer exceção não tratada em uma resposta HTTP 500 (erro interno).
     * <p>
     * Utiliza um código genérico ({@code ERRO_INTERNO}) e um código interno fixo
     * ({@code GEN-0001}), além da mensagem da exceção original no campo {@code detalhes}.
     *
     * @param exception exceção inesperada lançada durante o processamento da requisição
     * @return resposta HTTP 500 com o payload de {@link ErroResposta} em JSON
     */
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