<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Minhas Tarefas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="container">

    <div class="topo">
        <h1>Minhas Tarefas</h1>
        <span class="usuario">
            ${sessionScope.usuarioLogado.login}
            &nbsp;|&nbsp;
            <a href="${pageContext.request.contextPath}/logout">Sair</a>
        </span>
    </div>

    <div class="card">

        <%-- Mensagem de erro guardada na sessao pelo Controller --%>
        <c:if test="${not empty sessionScope.mensagemErro}">
            <p class="erro">${sessionScope.mensagemErro}</p>
            <c:remove var="mensagemErro" scope="session"/>
        </c:if>

        <a href="${pageContext.request.contextPath}/tarefas?acao=novo" class="btn">Nova tarefa</a>

        <c:choose>
            <%-- Nenhuma tarefa cadastrada ainda --%>
            <c:when test="${empty tarefas}">
                <p class="vazio">Nenhuma tarefa cadastrada. Que tal comecar agora?</p>
            </c:when>

            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th>Tarefa</th>
                            <th>Situacao</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <%-- Percorre a lista que o Controller enviou --%>
                        <c:forEach var="tarefa" items="${tarefas}">
                            <tr class="${tarefa.concluida ? 'concluida' : ''}">
                                <td>
                                    <span class="titulo">${tarefa.titulo}</span>
                                    <c:if test="${not empty tarefa.descricao}">
                                        <span class="descricao">${tarefa.descricao}</span>
                                    </c:if>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${tarefa.concluida}">
                                            <span class="situacao feita">Concluida</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="situacao pendente">Pendente</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="acoes">
                                    <a href="${pageContext.request.contextPath}/tarefas?acao=concluir&id=${tarefa.id}">
                                        ${tarefa.concluida ? 'Reabrir' : 'Concluir'}
                                    </a>
                                    <a href="${pageContext.request.contextPath}/tarefas?acao=editar&id=${tarefa.id}">
                                        Editar
                                    </a>
                                    <a href="${pageContext.request.contextPath}/tarefas?acao=deletar&id=${tarefa.id}"
                                       class="excluir"
                                       onclick="return confirm('Deseja excluir esta tarefa?');">
                                        Excluir
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <p class="resumo">${total} tarefa(s) - ${pendentes} pendente(s)</p>
            </c:otherwise>
        </c:choose>

    </div>
</div>

</body>
</html>
