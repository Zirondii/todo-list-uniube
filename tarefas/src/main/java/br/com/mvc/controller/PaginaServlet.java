package br.com.mvc.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet({"/login", "/cadastro", "/tarefas", "/logout"})
public class PaginaServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String pagina = switch (req.getServletPath()) {
            case "/tarefas" -> "novo".equals(req.getParameter("acao")) || "editar".equals(req.getParameter("acao"))
                    ? "/tarefa/form.jsp" : "/tarefa/lista.jsp";
            case "/cadastro" -> "/cadastro.jsp";
            default -> "/login.jsp";
        };
        req.getRequestDispatcher(pagina).forward(req, res);
    }
}
