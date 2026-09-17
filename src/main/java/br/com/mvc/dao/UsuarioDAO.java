package br.com.mvc.dao;

import br.com.mvc.model.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO = Data Access Object (acesso ao banco).
 *
 * Contem apenas o SQL. A conversao do ResultSet para objeto e feita pelo
 * metodo mapearSimples(), herdado de MysqlDAO.
 */
public class UsuarioDAO extends MysqlDAO {

    public UsuarioDAO() {
        super();
    }

    public Usuario buscarPorLoginESenha(String login, String senha) {
        String sql = "SELECT id, login, senha FROM usuario WHERE login = ? AND senha = ?";
        try (ResultSet rs = super.executar(sql, login, senha)) {
            if (rs.next()) {
                return super.mapearSimples(rs, Usuario.class);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuario.", e);
        }
        return null;
    }

    public Usuario buscarPorId(Long id) {
        String sql = "SELECT id, login, senha FROM usuario WHERE id = ?";
        try (ResultSet rs = super.executar(sql, id)) {
            if (rs.next()) {
                return super.mapearSimples(rs, Usuario.class);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuario por id.", e);
        }
        return null;
    }

    /**
     * Verifica se ja existe alguem usando este login.
     * Usado no cadastro para nao permitir logins repetidos.
     */
    public boolean existeLogin(String login) {
        String sql = "SELECT id FROM usuario WHERE login = ?";
        try (ResultSet rs = super.executar(sql, login)) {
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar o login.", e);
        }
    }

    public void inserir(Usuario usuario) {
        String sql = "INSERT INTO usuario (login, senha) VALUES (?, ?)";
        try {
            super.executarUpdate(sql, usuario.getLogin(), usuario.getSenha());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir usuario.", e);
        }
    }
}
