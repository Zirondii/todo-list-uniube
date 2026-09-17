package br.com.mvc.controller;

import br.com.mvc.model.Tarefa;
import br.com.mvc.model.Usuario;
import br.com.mvc.service.TarefaService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller das tarefas - o "maestro" do CRUD.
 *
 * Fluxo de uma requisicao:
 *   1. O usuario acessa /tarefas
 *   2. Este Controller recebe a chamada
 *   3. Pede os dados ao TarefaService
 *   4. O Service busca no banco (via DAO) e devolve a lista
 *   5. O Controller coloca a lista na requisicao
 *   6. A View (lista.jsp) monta o HTML e exibe a pagina
 *
 * As acoes sao escolhidas pelo parametro "acao" na URL:
 *   /tarefas                     -> lista as tarefas
 *   /tarefas?acao=novo           -> formulario em branco
 *   /tarefas?acao=editar&id=1    -> formulario preenchido
 *   /tarefas?acao=deletar&id=1   -> exclui a tarefa
 *   /tarefas?acao=concluir&id=1  -> marca/desmarca como concluida
 */
@WebServlet("/tarefas")
public class TarefaController extends HttpServlet {

    private final TarefaService tarefaService = new TarefaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = obterUsuarioLogado(request);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String acao = request.getParameter("acao");

        try {
            if ("novo".equals(acao)) {
                abrirFormulario(request, response, null);

            } else if ("editar".equals(acao)) {
                Long id = lerId(request);
                Tarefa tarefa = this.tarefaService.buscarDoUsuario(id, usuario.getId());
                if (tarefa == null) {
                    redirecionarComErro(request, response, "Tarefa nao encontrada.");
                    return;
                }
                abrirFormulario(request, response, tarefa);

            } else if ("deletar".equals(acao)) {
                this.tarefaService.deletar(lerId(request), usuario.getId());
                // Redireciona em vez de encaminhar: evita reenviar a acao ao atualizar a pagina
                response.sendRedirect(request.getContextPath() + "/tarefas");

            } else if ("concluir".equals(acao)) {
                this.tarefaService.alternarConcluida(lerId(request), usuario.getId());
                response.sendRedirect(request.getContextPath() + "/tarefas");

            } else {
                listar(request, response, usuario);
            }

        } catch (IllegalArgumentException e) {
            redirecionarComErro(request, response, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = obterUsuarioLogado(request);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Monta o objeto com os dados que vieram do formulario
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(request.getParameter("titulo"));
        tarefa.setDescricao(request.getParameter("descricao"));
        tarefa.setConcluida(request.getParameter("concluida") != null);
        tarefa.setUsuarioId(usuario.getId());

        String idTexto = request.getParameter("id");
        boolean edicao = idTexto != null && !idTexto.isBlank();
        if (edicao) {
            tarefa.setId(Long.valueOf(idTexto));
        }

        try {
            // Sem id = tarefa nova; com id = alteracao de uma existente
            if (edicao) {
                this.tarefaService.alterar(tarefa, usuario.getId());
            } else {
                this.tarefaService.inserir(tarefa);
            }
            response.sendRedirect(request.getContextPath() + "/tarefas");

        } catch (IllegalArgumentException e) {
            // Deu erro de validacao: volta ao formulario mantendo o que foi digitado
            request.setAttribute("erro", e.getMessage());
            abrirFormulario(request, response, tarefa);
        }
    }

    /** Busca as tarefas do usuario e envia para a tela de listagem. */
    private void listar(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {

        List<Tarefa> tarefas = this.tarefaService.listar(usuario.getId());

        request.setAttribute("tarefas", tarefas);
        request.setAttribute("total", tarefas.size());
        request.setAttribute("pendentes", tarefas.stream().filter(t -> !t.isConcluida()).count());

        request.getRequestDispatcher("/tarefa/lista.jsp").forward(request, response);
    }

    /** Abre o formulario. Sem tarefa = cadastro novo; com tarefa = edicao. */
    private void abrirFormulario(HttpServletRequest request, HttpServletResponse response, Tarefa tarefa)
            throws ServletException, IOException {

        request.setAttribute("tarefa", tarefa);
        request.getRequestDispatcher("/tarefa/form.jsp").forward(request, response);
    }

    private Usuario obterUsuarioLogado(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao == null) {
            return null;
        }
        return (Usuario) sessao.getAttribute("usuarioLogado");
    }

    private Long lerId(HttpServletRequest request) {
        String id = request.getParameter("id");
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id nao informado.");
        }
        try {
            return Long.valueOf(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Id invalido.");
        }
    }

    private void redirecionarComErro(HttpServletRequest request, HttpServletResponse response, String mensagem)
            throws IOException {
        HttpSession sessao = request.getSession();
        sessao.setAttribute("mensagemErro", mensagem);
        response.sendRedirect(request.getContextPath() + "/tarefas");
    }
}
