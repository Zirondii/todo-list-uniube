package br.com.mvc.dao;

import br.com.mvc.model.Tarefa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO das tarefas. Aqui fica todo o SQL do CRUD.
 *
 * Assim como o UsuarioDAO, nao possui metodo "mapear" proprio:
 * usa o mapearSimples() herdado de MysqlDAO.
 */
public class TarefaDAO extends MysqlDAO {

    public TarefaDAO() {
        super();
    }

    /**
     * Lista as tarefas de um usuario. As nao concluidas aparecem primeiro.
     */
    public List<Tarefa> listarPorUsuario(Long usuarioId) {
        String sql = "SELECT id, usuario_id, titulo, descricao, concluida, criada_em "
                   + "FROM tarefas WHERE usuario_id = ? ORDER BY concluida ASC, id DESC";

        List<Tarefa> lista = new ArrayList<>();
        try (ResultSet rs = super.executar(sql, usuarioId)) {
            while (rs.next()) {
                lista.add(super.mapearSimples(rs, Tarefa.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar tarefas.", e);
        }
        return lista;
    }

    public Tarefa buscarPorId(Long id) {
        String sql = "SELECT id, usuario_id, titulo, descricao, concluida, criada_em "
                   + "FROM tarefas WHERE id = ?";
        try (ResultSet rs = super.executar(sql, id)) {
            if (rs.next()) {
                return super.mapearSimples(rs, Tarefa.class);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar tarefa por id.", e);
        }
        return null;
    }

    public void inserir(Tarefa tarefa) {
        String sql = "INSERT INTO tarefas (usuario_id, titulo, descricao, concluida) "
                   + "VALUES (?, ?, ?, ?)";
        try {
            super.executarUpdate(sql,
                    tarefa.getUsuarioId(),
                    tarefa.getTitulo(),
                    tarefa.getDescricao(),
                    tarefa.isConcluida());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir tarefa.", e);
        }
    }

    public void alterar(Tarefa tarefa) {
        String sql = "UPDATE tarefas SET titulo = ?, descricao = ?, concluida = ? WHERE id = ?";
        try {
            super.executarUpdate(sql,
                    tarefa.getTitulo(),
                    tarefa.getDescricao(),
                    tarefa.isConcluida(),
                    tarefa.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar tarefa.", e);
        }
    }

    public void deletar(Long id) {
        String sql = "DELETE FROM tarefas WHERE id = ?";
        try {
            super.executarUpdate(sql, id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar tarefa.", e);
        }
    }

    /**
     * Inverte o estado da tarefa: se estava pendente vira concluida e vice-versa.
     */
    public void alternarConcluida(Long id) {
        String sql = "UPDATE tarefas SET concluida = NOT concluida WHERE id = ?";
        try {
            super.executarUpdate(sql, id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar a situacao da tarefa.", e);
        }
    }
}
