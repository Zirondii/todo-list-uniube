<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%--
    Pagina de entrada da aplicacao.
    Apenas redireciona para a tela de login, que decide o que mostrar.
--%>
<% response.sendRedirect(request.getContextPath() + "/login"); %>
