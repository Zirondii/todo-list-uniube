package br.com.mvc.controller;

import br.com.mvc.model.Usuario;
import br.com.mvc.service.UsuarioService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller responsavel pela entrada e saida do sistema.
 *
 * O Controller nao acessa o banco e nao monta HTML: ele recebe a requisicao,
 * pede o trabalho ao Service e encaminha o resultado para a View (JSP).
 */
@WebServlet(urlPatterns = {"/login", "/logout"})
public class LoginController extends HttpServlet {

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Encerra a sessao e volta para a tela de login
        if (request.getServletPath().equals("/logout")) {
            HttpSession sessao = request.getSession(false);
            if (sessao != null) {
                sessao.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Se ja estiver logado, vai direto para a lista de tarefas
        HttpSession sessao = request.getSession(false);
        if (sessao != null && sessao.getAttribute("usuarioLogado") != null) {
            response.sendRedirect(request.getContextPath() + "/tarefas");
            return;
        }

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String login = request.getParameter("login");
        String senha = request.getParameter("senha");

        // Pede ao Service para conferir os dados
        Usuario usuario = this.usuarioService.autenticar(login, senha);

        if (usuario == null) {
            // Dados invalidos: volta para a tela de login com a mensagem de erro
            request.setAttribute("erro", "Login ou senha invalidos.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // Guarda o usuario na sessao para as proximas telas saberem quem esta logado
        HttpSession sessao = request.getSession();
        sessao.setAttribute("usuarioLogado", usuario);

        response.sendRedirect(request.getContextPath() + "/tarefas");
    }
}
