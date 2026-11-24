<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Detalhes da reuniao</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<main class="container py-4">
    <a class="btn btn-link px-0" href="<c:url value='/reunioes'/>">&larr; Voltar</a>
    <h1 class="h2 mb-2"><c:out value="${reuniao.titulo}"/></h1>
    <p><strong>Periodo:</strong> <c:out value="${reuniao.periodoFormatado}"/></p>
    <c:if test="${not empty reuniao.descricao}">
        <p><strong>Descricao:</strong> <c:out value="${reuniao.descricao}"/></p>
    </c:if>
    <c:if test="${not empty reuniao.localReuniao}">
        <p><strong>Local:</strong> <c:out value="${reuniao.localReuniao}"/></p>
    </c:if>

    <h2 class="h4 mt-4">Horarios</h2>
    <ul class="list-group mb-4">
        <c:forEach var="horario" items="${horarios}">
            <li class="list-group-item">
                <c:out value="${horario.dia}"/>
                <c:if test="${not empty horario.periodo}"> - <c:out value="${horario.periodo}"/></c:if>
                <c:if test="${not empty horario.horaInicio}">
                    - <c:out value="${horario.horaInicio}"/> a <c:out value="${horario.horaFim}"/>
                </c:if>
            </li>
        </c:forEach>
    </ul>

    <h2 class="h4 mt-4">Resumo dos horarios</h2>
    <div class="table-responsive mb-4">
        <table class="table table-bordered align-middle">
            <thead class="table-light"><tr><th>Horario</th><th>Sim</th><th>Talvez</th></tr></thead>
            <tbody>
            <c:forEach var="item" items="${resumoHorarios}">
                <tr>
                    <td><c:out value="${item.dia}"/>
                        <c:if test="${not empty item.periodo}"> - <c:out value="${item.periodo}"/></c:if>
                        <c:if test="${not empty item.horaInicio}"> - <c:out value="${item.horaInicio}"/> a <c:out value="${item.horaFim}"/></c:if>
                    </td>
                    <td><span class="badge text-bg-success"><c:out value="${item.disponivel}"/></span></td>
                    <td><span class="badge text-bg-warning"><c:out value="${item.talvez}"/></span></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <h2 class="h4 mt-4">Participantes</h2>
    <table class="table table-striped table-bordered">
        <thead class="table-light"><tr><th>Nome</th><th>E-mail</th><th>Resposta</th><th>Opcoes marcadas</th></tr></thead>
        <tbody>
        <c:forEach var="participante" items="${participantes}">
            <tr>
                <td><c:out value="${empty participante.nome ? participante.email : participante.nome}"/></td>
                <td><c:out value="${participante.email}"/></td>
                <td><c:out value="${participante.respondidoEm == null ? 'Pendente' : 'Respondido'}"/></td>
                <td>
                    <span><c:out value="${participante.quantidadeDisponibilidades}"/></span>
                    <a class="btn btn-sm btn-outline-primary"
                       href="<c:url value='/resposta'><c:param name='token' value='${participante.token}'/><c:param name='readonly' value='1'/></c:url>">Ver resposta</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</main>
</body>
</html>
