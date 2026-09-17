<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Criar conta - Lista de Tarefas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="card login-box">
    <h1>Criar conta</h1>

    <%-- Mensagem enviada pelo CadastroController quando a validacao falha --%>
    <c:if test="${not empty erro}">
        <p class="erro">${erro}</p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/cadastro">
        <div class="campo">
            <label for="login">Usuario</label>
            <%-- Mantem o que foi digitado caso ocorra erro --%>
            <input type="text" id="login" name="login" value="${login}"
                   minlength="3" maxlength="50" required autofocus>
        </div>

        <div class="campo">
            <label for="senha">Senha</label>
            <input type="password" id="senha" name="senha" minlength="4" required>
        </div>

        <div class="campo">
            <label for="confirmacaoSenha">Repita a senha</label>
            <input type="password" id="confirmacaoSenha" name="confirmacaoSenha"
                   minlength="4" required>
        </div>

        <button type="submit" class="btn btn-largo">Cadastrar</button>
    </form>

    <p class="link-rodape">
        Ja tem uma conta?
        <a href="${pageContext.request.contextPath}/login">Entrar</a>
    </p>
</div>

</body>
</html>
