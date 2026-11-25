<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Minhas Tarefas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script defer src="${pageContext.request.contextPath}/js/app.js"></script>
</head>
<body data-tela="lista" data-base="${pageContext.request.contextPath}">

<div class="container">

    <div class="topo">
        <h1>Minhas Tarefas</h1>
        <span class="usuario">
            <span data-login></span>
            &nbsp;|&nbsp;
            <a href="${pageContext.request.contextPath}/logout">Sair</a>
        </span>
    </div>

    <div class="card">

        <p class="erro" role="alert" hidden></p>

        <a href="${pageContext.request.contextPath}/tarefas?acao=novo" class="btn">Nova tarefa</a>

        <p class="vazio" hidden>Nenhuma tarefa cadastrada. Que tal começar agora?</p>
        <table hidden>
            <thead><tr><th>Tarefa</th><th>Situação</th><th>Ações</th></tr></thead>
            <tbody></tbody>
        </table>

        <p class="resumo"></p>

    </div>
</div>

</body>
</html>
