package br.com.mvc.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet({"/login", "/cadastro", "/tarefas", "/logout", "/index.jsp", "/login.jsp", "/cadastro.jsp", "/tarefa/lista.jsp", "/tarefa/form.jsp"})
public class PaginaServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String pagina = switch (req.getServletPath()) {
            case "/tarefas" -> "novo".equals(req.getParameter("acao")) || "editar".equals(req.getParameter("acao"))
                    ? "/tarefa/form.jsp" : "/tarefa/lista.jsp";
            case "/index.jsp" -> "/index.jsp";
            case "/tarefa/form.jsp" -> "/tarefa/form.jsp";
            case "/tarefa/lista.jsp" -> "/tarefa/lista.jsp";
            case "/cadastro", "/cadastro.jsp" -> "/cadastro.jsp";
            default -> "/login.jsp";
        };
        req.getRequestDispatcher("/WEB-INF/views" + pagina).forward(req, res);
    }
}
