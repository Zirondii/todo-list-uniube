<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <%-- O mesmo formulario serve para cadastrar e para editar --%>
    <title>${empty tarefa.id ? 'Nova tarefa' : 'Editar tarefa'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="container">

    <div class="topo">
        <h1>${empty tarefa.id ? 'Nova tarefa' : 'Editar tarefa'}</h1>
        <span class="usuario">${sessionScope.usuarioLogado.login}</span>
    </div>

    <div class="card">

        <c:if test="${not empty erro}">
            <p class="erro">${erro}</p>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/tarefas">

            <%-- Campo escondido: quando tem id, o Controller entende que e uma edicao --%>
            <input type="hidden" name="id" value="${tarefa.id}">

            <div class="campo">
                <label for="titulo">Titulo</label>
                <input type="text" id="titulo" name="titulo" maxlength="150"
                       value="${tarefa.titulo}" required autofocus>
            </div>

            <div class="campo">
                <label for="descricao">Descricao</label>
                <textarea id="descricao" name="descricao">${tarefa.descricao}</textarea>
            </div>

            <div class="campo-check">
                <input type="checkbox" id="concluida" name="concluida"
                       ${tarefa.concluida ? 'checked' : ''}>
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
