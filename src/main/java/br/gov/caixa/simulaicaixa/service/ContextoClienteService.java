package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import br.gov.caixa.simulaicaixa.data.entity.UsuarioAutenticacaoEntity;
import br.gov.caixa.simulaicaixa.data.repository.UsuarioAutenticacaoRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Set;

/**
 * Serviço responsável por expor informações do usuário autenticado
 * a partir do token JWT (como CPF, grupos de acesso e cliente vinculado).
 * <p>
 * Esta classe centraliza a lógica de:
 * <ul>
 *     <li>Descobrir o CPF do usuário a partir do token (claim {@code cpf} ou {@code sub}).</li>
 *     <li>Localizar o cliente associado ao usuário autenticado.</li>
 *     <li>Verificar se o usuário possui o papel {@code admin}.</li>
 * </ul>
 * <p>
 * Escopo {@link RequestScoped}: uma instância por requisição HTTP.
 */
@RequestScoped
public class ContextoClienteService {

    @Inject
    JsonWebToken jwt;

    @Inject
    UsuarioAutenticacaoRepository usuarioAutenticacaoRepository;

    /**
     * Obtém o identificador do cliente vinculado ao usuário autenticado,
     * caso exista o vínculo.
     * <p>
     * Regras:
     * <ul>
     *     <li>Obtém o CPF a partir do token JWT.</li>
     *     <li>Consulta o repositório de autenticação para localizar o usuário.</li>
     *     <li>Retorna o {@code clienteId} cadastrado para esse usuário.</li>
     * </ul>
     *
     * @return o identificador do cliente vinculado ao usuário autenticado,
     * ou {@code null} se não houver vínculo (usuário não encontrado ou sem cliente associado).
     */
    public Long obterClienteId() {
        String cpf = obterCpfDoToken();
        if (cpf == null || cpf.isBlank()) {
            return null;
        }

        UsuarioAutenticacaoEntity usuario = usuarioAutenticacaoRepository.buscarPorCpf(cpf);
        if (usuario == null) {
            return null;
        }

        return usuario.getClienteId();
    }

    /**
     * Obtém o identificador do cliente vinculado ao usuário autenticado,
     * lançando exceção de negócio caso não exista vínculo válido.
     * <p>
     * Este método é indicado para fluxos em que o vínculo com cliente
     * é obrigatório para prosseguir a operação.
     *
     * @return o identificador do cliente vinculado ao usuário autenticado
     * @throws NegocioException caso não exista cliente associado ao usuário autenticado.
     *                          A exceção utiliza o código
     *                          {@link CodigoErroNegocio#CLIENTE_NAO_ENCONTRADO}.
     */
    public Long obterClienteIdUsuarioObrigatorio() {
        Long clienteId = obterClienteId();
        if (clienteId == null) {
            throw new NegocioException(
                    CodigoErroNegocio.CLIENTE_NAO_ENCONTRADO,
                    "Usuário autenticado não está vinculado a um cliente válido."
            );
        }
        return clienteId;
    }

    /**
     * Verifica se o usuário autenticado possui o papel {@code admin}.
     * <p>
     * A verificação é feita com base no conjunto de grupos (roles)
     * presentes no token JWT.
     *
     * @return {@code true} se o usuário atual possuir o grupo {@code admin},
     * ou {@code false} caso contrário.
     */
    public boolean usuarioAtualEhAdmin() {
        Set<String> grupos = jwt.getGroups();
        return grupos != null && grupos.contains("admin");
    }

    /**
     * Obtém o CPF do usuário autenticado a partir do token JWT.
     * <p>
     * Estratégia:
     * <ul>
     *     <li>Tenta ler a claim {@code cpf}.</li>
     *     <li>Se não existir, utiliza o {@code subject} do token.</li>
     * </ul>
     *
     * @return o CPF obtido a partir do token JWT, ou {@code null}
     * se não for possível determiná-lo.
     */
    public String obterCpfUsuarioAtual() {
        return obterCpfDoToken();
    }

    /**
     * Resolve o CPF do usuário a partir do token JWT.
     * <p>
     * Método utilitário interno compartilhado pelas demais operações
     * desta classe.
     *
     * @return o valor da claim {@code cpf} ou, na ausência dela,
     * o {@code subject} do token; pode retornar {@code null}.
     */
    private String obterCpfDoToken() {
        String cpf = jwt.getClaim("cpf");
        if (cpf == null || cpf.isBlank()) {
            cpf = jwt.getSubject();
        }
        return cpf;
    }
}