package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import br.gov.caixa.simulaicaixa.data.entity.UsuarioAutenticacaoEntity;
import br.gov.caixa.simulaicaixa.data.repository.UsuarioAutenticacaoRepository;
import br.gov.caixa.simulaicaixa.dto.RequisicaoLoginDto;
import br.gov.caixa.simulaicaixa.dto.RespostaLoginDto;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela autenticação de usuários da aplicação.
 * <p>
 * Valida credenciais (CPF e senha), verifica grupos de acesso
 * e gera o token JWT utilizado nas demais chamadas da API.
 */
@ApplicationScoped
public class AutenticacaoServiceImpl implements AutenticacaoService {

    private static final String EMISSOR = "simulaicaixa";
    private static final long TEMPO_EXPIRACAO_PADRAO_SEGUNDOS = 3600L;

    private static final Set<String> GRUPOS_PERMITIDOS = Set.of("cliente", "admin");

    private final UsuarioAutenticacaoRepository usuarioAutenticacaoRepository;

    @Inject
    public AutenticacaoServiceImpl(UsuarioAutenticacaoRepository usuarioAutenticacaoRepository) {
        this.usuarioAutenticacaoRepository = usuarioAutenticacaoRepository;
    }

    /**
     * Autentica um usuário a partir do CPF e senha informados na requisição.
     * <p>
     * Regras principais:
     * <ul>
     *     <li>Valida se o usuário existe e se a senha está correta.</li>
     *     <li>Verifica se o usuário possui ao menos um grupo de acesso válido.</li>
     *     <li>Gera e retorna um token JWT assinado para uso nas demais APIs.</li>
     * </ul>
     *
     * @param requisicao dados de login (CPF e senha em texto claro).
     * @return dados básicos do usuário autenticado e o token JWT.
     * @throws br.gov.caixa.simulaicaixa.core.excecao.NegocioException
     *         quando as credenciais são inválidas ou o usuário não possui grupo de acesso.
     */
    @Override
    public RespostaLoginDto autenticar(RequisicaoLoginDto requisicao) {
        if (requisicao == null
                || requisicao.cpf() == null || requisicao.cpf().isBlank()
                || requisicao.senha() == null || requisicao.senha().isBlank()) {

            throw new NegocioException(CodigoErroNegocio.CREDENCIAIS_INVALIDAS);
        }

        String cpfNormalizado = requisicao.cpf().replaceAll("\\D", "");

        UsuarioAutenticacaoEntity usuario =
                usuarioAutenticacaoRepository.buscarPorCpf(cpfNormalizado);

        if (usuario == null) {
            throw new NegocioException(CodigoErroNegocio.CREDENCIAIS_INVALIDAS);
        }

        boolean senhaValida = BCrypt.checkpw(requisicao.senha(), usuario.getSenhaHash());

        if (!senhaValida) {
            throw new NegocioException(CodigoErroNegocio.CREDENCIAIS_INVALIDAS);
        }

        String valorGruposBd = usuario.getGrupos();

        Set<String> grupos;
        if (valorGruposBd == null || valorGruposBd.isBlank()) {
            grupos = Set.of("cliente");
        } else {
            grupos = Arrays.stream(valorGruposBd.split(","))
                    .map(String::trim)
                    .filter(g -> !g.isEmpty())
                    .filter(GRUPOS_PERMITIDOS::contains)
                    .collect(Collectors.toUnmodifiableSet());

            if (grupos.isEmpty()) {
                throw new NegocioException(CodigoErroNegocio.USUARIO_SEM_GRUPO_ACESSO);
            }
        }

        Instant agora = Instant.now();
        Instant expiracao = agora.plusSeconds(TEMPO_EXPIRACAO_PADRAO_SEGUNDOS);

        String token = Jwt.issuer(EMISSOR)
                .subject(cpfNormalizado)
                .claim("cpf", cpfNormalizado)
                .groups(grupos)
                .expiresAt(expiracao)
                .sign();

        long expiraEmSegundos = Duration.between(agora, expiracao).getSeconds();

        return new RespostaLoginDto(token, "Bearer", expiraEmSegundos);
    }
}