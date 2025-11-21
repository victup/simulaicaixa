package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.core.erro.ErroResposta;
import br.gov.caixa.simulaicaixa.dto.ProdutoRecomendadoDto;
import br.gov.caixa.simulaicaixa.service.ProdutoService;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.Duration;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Path("/produtos-recomendados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"cliente", "admin"})
@Tag(
        name = "Produtos Recomendados",
        description = "Consulta de produtos de investimento recomendados por perfil ou com base no cliente autenticado."
)
public class ProdutoResource {

    private final ProdutoService produtoService;
    private final ColetorTelemetria coletorTelemetria;

    @Inject
    public ProdutoResource(ProdutoService produtoService,
                           ColetorTelemetria coletorTelemetria) {
        this.produtoService = produtoService;
        this.coletorTelemetria = coletorTelemetria;
    }

    @GET
    @Path("/{perfil}")
    @Operation(
            summary = "Listar produtos recomendados por perfil",
            description = """
                    Retorna a lista de produtos de investimento recomendados para o perfil informado.
                    
                    • Aceita valores como \"Conservador\", \"Moderado\" ou \"Agressivo\" (case insensitive).
                    • Exige autenticação com papel 'cliente' ou 'admin'.
                    """
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Lista de produtos recomendados retornada com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ProdutoRecomendadoDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Perfil informado é inválido ou não reconhecido pelo sistema.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Não há produtos compatíveis com o perfil informado.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Requisição sem autenticação válida (token ausente ou inválido).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "403",
                    description = "Usuário autenticado não possui permissão para acessar este recurso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Erro interno ao processar a requisição.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            )
    })
    public List<ProdutoRecomendadoDto> listarPorPerfil(@PathParam("perfil") String perfil) {
        long inicio = System.nanoTime();

        List<ProdutoRecomendadoDto> produtos = produtoService
                .listarProdutosRecomendadosPorPerfil(perfil);

        long fim = System.nanoTime();
        long duracaoMs = Duration.ofNanos(fim - inicio).toMillis();

        coletorTelemetria.registrarChamada("produtos-recomendados", duracaoMs);

        return produtos;
    }

    @GET
    @Path("/usuario-logado")
    @Operation(
            summary = "Listar produtos recomendados para o cliente autenticado",
            description = """
                    Retorna produtos de investimento recomendados especificamente para o cliente associado
                    ao usuário autenticado.
                    
                    • A recomendação considera o perfil de risco do cliente e o histórico de investimentos.
                    • Clientes só veem recomendações baseadas em seus próprios dados.
                    • Usuários com papel 'admin' podem utilizar este endpoint para simular o comportamento
                      para o cliente associado ao token atual.
                    """
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Lista de produtos recomendados para o cliente autenticado retornada com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ProdutoRecomendadoDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Cliente autenticado não possui perfil de risco cadastrado ou não há produtos compatíveis com o seu perfil.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Requisição sem autenticação válida (token ausente ou inválido).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "403",
                    description = "Usuário autenticado não possui permissão para acessar este recurso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Erro interno ao processar a requisição.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            )
    })
    public List<ProdutoRecomendadoDto> listarParaClienteAtual() {
        return produtoService.listarProdutosRecomendadosParaClienteAtual();
    }
}