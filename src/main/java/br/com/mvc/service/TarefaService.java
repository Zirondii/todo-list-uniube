package br.com.mvc.service;

import br.com.mvc.dao.TarefaDAO;
import br.com.mvc.model.Tarefa;

import java.util.List;

/**
 * Regras de negocio das tarefas.
 *
 * Alem de validar os dados, garante que um usuario so consiga mexer
 * nas proprias tarefas.
 */
public class TarefaService {

    private final TarefaDAO tarefaDAO;

    public TarefaService() {
        this.tarefaDAO = new TarefaDAO();
    }

    public List<Tarefa> listar(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("Usuario e obrigatorio.");
        }
        return this.tarefaDAO.listarPorUsuario(usuarioId);
    }

    /**
     * Busca uma tarefa garantindo que ela pertence ao usuario informado.
     * Evita que alguem edite a tarefa de outra pessoa trocando o id na URL.
     */
    public Tarefa buscarDoUsuario(Long id, Long usuarioId) {
        if (id == null || usuarioId == null) {
            return null;
        }
        Tarefa tarefa = this.tarefaDAO.buscarPorId(id);
        if (tarefa == null || !tarefa.getUsuarioId().equals(usuarioId)) {
            return null;
        }
        return tarefa;
    }

    public void inserir(Tarefa tarefa) {
        validar(tarefa);
        this.tarefaDAO.inserir(tarefa);
    }

    public void alterar(Tarefa tarefa, Long usuarioId) {
        if (tarefa.getId() == null) {
            throw new IllegalArgumentException("Id e obrigatorio para alterar.");
        }
        validar(tarefa);

        // So altera se a tarefa for mesmo do usuario logado
        if (buscarDoUsuario(tarefa.getId(), usuarioId) == null) {
            throw new IllegalArgumentException("Tarefa nao encontrada.");
        }
        this.tarefaDAO.alterar(tarefa);
    }

    public void deletar(Long id, Long usuarioId) {
        if (buscarDoUsuario(id, usuarioId) == null) {
            throw new IllegalArgumentException("Tarefa nao encontrada.");
        }
        this.tarefaDAO.deletar(id);
    }

    public void alternarConcluida(Long id, Long usuarioId) {
        if (buscarDoUsuario(id, usuarioId) == null) {
            throw new IllegalArgumentException("Tarefa nao encontrada.");
        }
        this.tarefaDAO.alternarConcluida(id);
    }

    private void validar(Tarefa tarefa) {
        if (tarefa == null) {
            throw new IllegalArgumentException("Tarefa e obrigatoria.");
        }
        if (tarefa.getTitulo() == null || tarefa.getTitulo().isBlank()) {
            throw new IllegalArgumentException("O titulo da tarefa e obrigatorio.");
        }
        if (tarefa.getTitulo().length() > 150) {
            throw new IllegalArgumentException("O titulo deve ter no maximo 150 caracteres.");
        }
        if (tarefa.getUsuarioId() == null) {
            throw new IllegalArgumentException("Usuario e obrigatorio.");
        }
    }
}
