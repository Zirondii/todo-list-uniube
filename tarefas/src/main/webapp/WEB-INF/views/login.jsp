<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Entrar - Lista de Tarefas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script defer src="${pageContext.request.contextPath}/js/app.js"></script>
</head>
<body data-tela="login" data-base="${pageContext.request.contextPath}">

<div class="card login-box">
    <h1>Lista de Tarefas</h1>

    <p class="erro" role="alert" hidden></p>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <div class="campo">
            <label for="login">Usuario</label>
            <input type="text" id="login" name="login" required autofocus>
        </div>

        <div class="campo">
            <label for="senha">Senha</label>
            <input type="password" id="senha" name="senha" required>
        </div>

        <button type="submit" class="btn btn-largo">Entrar</button>
    </form>

    <p class="link-rodape">
        Ainda nao tem conta?
        <a href="${pageContext.request.contextPath}/cadastro">Criar agora</a>
    </p>
</div>

</body>
</html>
