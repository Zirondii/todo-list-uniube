package br.com.mvc.service;

import br.com.mvc.dao.UsuarioDAO;
import br.com.mvc.model.Usuario;

/**
 * Regras de negocio relacionadas ao usuario.
 *
 * O Service fica entre o Controller e o DAO: valida os dados antes de
 * chegar ao banco. Nao sabe nada sobre telas nem sobre HTML.
 */
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Verifica se o login e a senha conferem.
     * Devolve o usuario quando estao corretos, ou null quando nao estao.
     */
    public Usuario autenticar(String login, String senha) {
        if (login == null || login.isBlank() || senha == null || senha.isBlank()) {
            return null;
        }
        return this.usuarioDAO.buscarPorLoginESenha(login, senha);
    }

    public Usuario buscarPorId(Long id) {
        if (id == null) {
            return null;
        }
        return this.usuarioDAO.buscarPorId(id);
    }

    /**
     * Cadastra um novo usuario.
     *
     * Valida os dados e confere se o login ainda esta disponivel antes
     * de gravar no banco.
     */
    public void cadastrar(String login, String senha, String confirmacaoSenha) {

        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("O usuario e obrigatorio.");
        }
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("A senha e obrigatoria.");
        }

        login = login.trim();

        if (login.length() < 3 || login.length() > 50) {
            throw new IllegalArgumentException("O usuario deve ter entre 3 e 50 caracteres.");
        }
        if (senha.length() < 4) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 4 caracteres.");
        }
        if (!senha.equals(confirmacaoSenha)) {
            throw new IllegalArgumentException("As senhas nao conferem.");
        }
        if (this.usuarioDAO.existeLogin(login)) {
            throw new IllegalArgumentException("Este usuario ja esta em uso. Escolha outro.");
        }

        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setSenha(senha);

        this.usuarioDAO.inserir(usuario);
    }
}
