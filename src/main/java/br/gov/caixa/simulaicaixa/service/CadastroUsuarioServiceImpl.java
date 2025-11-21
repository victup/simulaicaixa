package br.gov.caixa.simulaicaixa.service;

import br.gov.caixa.simulaicaixa.core.erro.CodigoErroNegocio;
import br.gov.caixa.simulaicaixa.core.excecao.NegocioException;
import br.gov.caixa.simulaicaixa.data.entity.UsuarioAutenticacaoEntity;
import br.gov.caixa.simulaicaixa.data.repository.UsuarioAutenticacaoRepository;
import br.gov.caixa.simulaicaixa.dto.RequisicaoCadastroUsuarioDto;
import br.gov.caixa.simulaicaixa.dto.RespostaCadastroUsuarioDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Implementação padrão do serviço de cadastro de usuários.
 */
@ApplicationScoped
public class CadastroUsuarioServiceImpl implements CadastroUsuarioService {

    private final UsuarioAutenticacaoRepository usuarioAutenticacaoRepository;

    @Inject
    public CadastroUsuarioServiceImpl(UsuarioAutenticacaoRepository usuarioAutenticacaoRepository) {
        this.usuarioAutenticacaoRepository = usuarioAutenticacaoRepository;
    }

    /**
     * Cadastra um novo usuário que poderá se autenticar na aplicação.
     *
     * @param requisicao dados obrigatórios para cadastro do usuário
     * @return informações básicas do usuário cadastrado
     */
    @Override
    @Transactional
    public RespostaCadastroUsuarioDto cadastrarUsuario(RequisicaoCadastroUsuarioDto requisicao) {
        if (requisicao == null) {
            throw new NegocioException(
                    CodigoErroNegocio.USUARIO_DADOS_INVALIDOS,
                    "Dados de cadastro são obrigatórios."
            );
        }

        String cpf = requisicao.cpf();
        String senha = requisicao.senha();

        if (cpf == null || cpf.isBlank() || senha == null || senha.isBlank()) {
            throw new NegocioException(
                    CodigoErroNegocio.USUARIO_DADOS_INVALIDOS,
                    "CPF e senha são obrigatórios para cadastro de usuário."
            );
        }

        String cpfNormalizado = cpf.trim();

        if (usuarioAutenticacaoRepository.buscarPorCpf(cpfNormalizado) != null) {
            throw new NegocioException(
                    CodigoErroNegocio.USUARIO_JA_EXISTE,
                    "Já existe um usuário cadastrado para o CPF informado."
            );
        }

        String grupos = requisicao.grupos();
        if (grupos == null || grupos.isBlank()) {
            grupos = "cliente";
        }

        UsuarioAutenticacaoEntity entity = new UsuarioAutenticacaoEntity();
        entity.setCpf(cpfNormalizado);
        entity.setSenhaHash(BCrypt.hashpw(senha, BCrypt.gensalt()));
        entity.setGrupos(grupos);
        entity.setClienteId(requisicao.clienteId());

        UsuarioAutenticacaoEntity salvo = usuarioAutenticacaoRepository.salvar(entity);

        return new RespostaCadastroUsuarioDto(
                salvo.getId(),
                salvo.getCpf(),
                salvo.getGrupos(),
                salvo.getClienteId()
        );
    }
}