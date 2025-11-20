package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.dto.ProdutoRecomendadoDto;
import br.gov.caixa.simulaicaixa.service.ProdutoService;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.Duration;
import java.util.List;

@Path("/produtos-recomendados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"cliente", "admin"})
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
    @RolesAllowed({"cliente", "admin"})
    public List<ProdutoRecomendadoDto> listarParaClienteAtual() {
        return produtoService.listarProdutosRecomendadosParaClienteAtual();
    }
}