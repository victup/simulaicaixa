package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.dto.RequisicaoLoginDto;
import br.gov.caixa.simulaicaixa.dto.RespostaLoginDto;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotAuthorizedException;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class AutenticacaoServiceImpl implements AutenticacaoService {

    private static final String EMISSOR = "simulaicaixa";
    private static final long TEMPO_EXPIRACAO_PADRAO_SEGUNDOS = 3600L;

    private final Map<String, String> credenciaisPorCpf = Map.of(
            "12345678901", "senha123",
            "00000000000", "senha000"
    );

    @Override
    public RespostaLoginDto autenticar(RequisicaoLoginDto requisicao) {
        if (requisicao == null || requisicao.cpf() == null || requisicao.senha() == null) {
            throw new NotAuthorizedException("CPF ou senha inválidos");
        }

        String cpfNormalizado = requisicao.cpf().replaceAll("\\D", "");
        String senhaEsperada = credenciaisPorCpf.get(cpfNormalizado);

        if (senhaEsperada == null || !senhaEsperada.equals(requisicao.senha())) {
            throw new NotAuthorizedException("CPF ou senha inválidos");
        }

        Instant agora = Instant.now();
        Instant expiracao = agora.plusSeconds(TEMPO_EXPIRACAO_PADRAO_SEGUNDOS);

        String token = Jwt.issuer(EMISSOR)
                .subject(cpfNormalizado)
                .claim("cpf", cpfNormalizado)
                .groups(Set.of("cliente"))
                .expiresAt(expiracao)
                .sign();

        long expiraEmSegundos = Duration.between(agora, expiracao).getSeconds();

        return new RespostaLoginDto(token, "Bearer", expiraEmSegundos);
    }
}