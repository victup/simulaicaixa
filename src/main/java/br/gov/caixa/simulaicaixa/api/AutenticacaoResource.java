package br.gov.caixa.simulaicaixa.api;

import br.gov.caixa.simulaicaixa.core.erro.ErroResposta;
import br.gov.caixa.simulaicaixa.dto.RequisicaoCadastroUsuarioDto;
import br.gov.caixa.simulaicaixa.dto.RequisicaoLoginDto;
import br.gov.caixa.simulaicaixa.dto.RespostaCadastroUsuarioDto;
import br.gov.caixa.simulaicaixa.dto.RespostaLoginDto;
import br.gov.caixa.simulaicaixa.service.AutenticacaoService;
import br.gov.caixa.simulaicaixa.service.CadastroUsuarioService;
import br.gov.caixa.simulaicaixa.telemetria.ColetorTelemetria;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.time.Duration;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(
        name = "Autenticação",
        description = """
                Operações de autenticação da aplicação.

                • Endpoint público, não exige JWT prévio.
                • Em caso de sucesso, retorna um token JWT assinado para uso nas demais APIs.
                """
)
public class AutenticacaoResource {

    private final AutenticacaoService autenticacaoService;
    private final CadastroUsuarioService cadastroUsuarioService;
    private final ColetorTelemetria coletorTelemetria;

    @Inject
    public AutenticacaoResource(AutenticacaoService autenticacaoService,
                                CadastroUsuarioService cadastroUsuarioService,
                                ColetorTelemetria coletorTelemetria) {
        this.autenticacaoService = autenticacaoService;
        this.cadastroUsuarioService = cadastroUsuarioService;
        this.coletorTelemetria = coletorTelemetria;
    }

    @POST
    @Path("/login")
    @PermitAll
    @Operation(
            summary = "Realizar login",
            description = """
                    Autentica o usuário a partir de CPF e senha e retorna um token JWT válido.

                    • Não exige token JWT na chamada.
                    • Em caso de sucesso, retorna dados básicos do usuário e o token de acesso.
                    """
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Autenticação realizada com sucesso. Retorna o token JWT e informações básicas do usuário.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = RespostaLoginDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Credenciais inválidas (por exemplo, CPF ou senha incorretos).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "403",
                    description = "Usuário autenticado, porém sem grupo de acesso válido para utilizar a aplicação.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Erro interno ao processar a requisição de login.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            )
    })
    public RespostaLoginDto login(RequisicaoLoginDto requisicao) {
        long inicio = System.nanoTime();

        RespostaLoginDto resposta = autenticacaoService.autenticar(requisicao);

        long fim = System.nanoTime();
        long duracaoMs = Duration.ofNanos(fim - inicio).toMillis();
        coletorTelemetria.registrarChamada("auth-login", duracaoMs);

        return resposta;
    }

    @POST
    @Path("/usuarios/novo")
    @RolesAllowed("admin")
    @Operation(
            summary = "Cadastrar novo usuário",
            description = """
                    Cadastra um novo usuário que poderá se autenticar na aplicação.

                    • Restrito a usuários com papel 'admin'.
                    • Útil para criar massa de teste com diferentes perfis de acesso.
                    """
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Usuário cadastrado com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = RespostaCadastroUsuarioDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Dados de cadastro inválidos (por exemplo, CPF ou senha ausentes).",
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
                    description = "Usuário autenticado não possui permissão para cadastrar novos usuários.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "409",
                    description = "Já existe um usuário cadastrado para o CPF informado.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Erro interno ao processar a requisição de cadastro.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErroResposta.class)
                    )
            )
    })
    public RespostaCadastroUsuarioDto cadastrarUsuario(RequisicaoCadastroUsuarioDto requisicao) {
        long inicio = System.nanoTime();

        RespostaCadastroUsuarioDto resposta = cadastroUsuarioService.cadastrarUsuario(requisicao);

        long fim = System.nanoTime();
        long duracaoMs = Duration.ofNanos(fim - inicio).toMillis();
        coletorTelemetria.registrarChamada("auth-cadastro-usuario", duracaoMs);

        return resposta;
    }
}