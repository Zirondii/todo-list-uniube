<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">

    <title>Nova tarefa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script defer src="${pageContext.request.contextPath}/js/app.js"></script>
</head>
<body data-tela="form" data-base="${pageContext.request.contextPath}">

<div class="container">

    <div class="topo">
        <h1>Nova tarefa</h1>
        <span class="usuario"><span data-login></span></span>
    </div>

    <div class="card">

        <p class="erro" role="alert" hidden></p>

        <form method="post" action="${pageContext.request.contextPath}/tarefas">

            <div class="campo">
                <label for="titulo">Titulo</label>
                <input type="text" id="titulo" name="titulo" maxlength="150"
                       value="" required autofocus>
            </div>

            <div class="campo">
                <label for="descricao">Descricao</label>
                <textarea id="descricao" name="descricao"></textarea>
            </div>

            <div class="campo-check">
                <input type="checkbox" id="concluida" name="concluida">
                <label for="concluida">Tarefa concluida</label>
            </div>

            <div class="rodape-form">
                <button type="submit" class="btn">Salvar</button>
                <a href="${pageContext.request.contextPath}/tarefas" class="btn btn-cinza">Cancelar</a>
            </div>
        </form>

    </div>
</div>

</body>
</html>
