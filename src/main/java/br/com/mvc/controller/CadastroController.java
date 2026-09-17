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
 * Controller responsavel pelo cadastro de novos usuarios.
 *
 * Assim como os demais Controllers, apenas recebe a requisicao, chama o
 * Service e escolhe a tela a ser exibida.
 */
@WebServlet("/cadastro")
public class CadastroController extends HttpServlet {

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Quem ja esta logado nao precisa se cadastrar
        HttpSession sessao = request.getSession(false);
        if (sessao != null && sessao.getAttribute("usuarioLogado") != null) {
            response.sendRedirect(request.getContextPath() + "/tarefas");
            return;
        }

        request.getRequestDispatcher("/cadastro.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String login = request.getParameter("login");
        String senha = request.getParameter("senha");
        String confirmacaoSenha = request.getParameter("confirmacaoSenha");

        try {
            // O Service valida os dados e grava o usuario
            this.usuarioService.cadastrar(login, senha, confirmacaoSenha);

            // Cadastrou: ja entra no sistema, sem precisar digitar de novo
            Usuario usuario = this.usuarioService.autenticar(login.trim(), senha);
            HttpSession sessao = request.getSession();
            sessao.setAttribute("usuarioLogado", usuario);

            response.sendRedirect(request.getContextPath() + "/tarefas");

        } catch (IllegalArgumentException e) {
            // Deu erro de validacao: volta ao formulario mantendo o que foi digitado
            request.setAttribute("erro", e.getMessage());
            request.setAttribute("login", login);
            request.getRequestDispatcher("/cadastro.jsp").forward(request, response);
        }
    }
}
