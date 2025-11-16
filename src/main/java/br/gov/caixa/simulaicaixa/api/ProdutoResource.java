package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.dto.ProdutoRecomendadoDto;
import br.gov.caixa.simulaicaixa.service.ProdutoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/produtos-recomendados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutoResource {

    private final ProdutoService produtoService;

    @Inject
    public ProdutoResource(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GET
    @Path("/{perfil}")
    public List<ProdutoRecomendadoDto> listarPorPerfil(@PathParam("perfil") String perfil) {
        return produtoService.listarProdutosRecomendadosPorPerfil(perfil);
    }
}
