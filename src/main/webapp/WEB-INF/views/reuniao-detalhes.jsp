<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" href="data:,">
    <title>Detalhes da reuniao</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<main class="container py-4">
    <a class="btn btn-link px-0" href="<c:url value='/reunioes'/>">&larr; Voltar</a>
    <h1 class="h2 mb-2"><c:out value="${reuniao.nome}"/></h1>
    <p><strong>Periodo:</strong> <c:out value="${reuniao.periodoFormatado}"/></p>

    <h2 class="h4 mt-4">Opcoes</h2>
    <c:choose>
        <c:when test="${empty opcoes}">
            <div class="alert alert-light border">Nenhuma opcao gerada.</div>
        </c:when>
        <c:otherwise>
            <ul class="list-group mb-4">
                <c:forEach var="opcao" items="${opcoes}">
                    <li class="list-group-item">
                        <c:out value="${opcao.dataOpcao}"/>
                        <c:if test="${not empty opcao.turno}">
                            - <c:out value="${opcao.turno}"/>
                        </c:if>
                        <c:if test="${not empty opcao.horaInicio}">
                            - <c:out value="${opcao.horaInicio}"/> a
                            <c:out value="${opcao.horaFim}"/>
                        </c:if>
                    </li>
                </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>

    <h2 class="h4 mt-4">Resumo dos horarios registrados</h2>
    <c:choose>
        <c:when test="${empty resumoOpcoes}">
            <div class="alert alert-light border">Nenhuma opcao disponivel para resumo.</div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive mb-4">
                <table class="table table-bordered align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Horario</th>
                            <th class="text-success">Disponivel</th>
                            <th class="text-warning">Talvez</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${resumoOpcoes}">
                            <tr>
                                <td>
                                    <c:out value="${item.dataOpcao}"/>
                                    <c:if test="${not empty item.turno}">
                                        - <c:out value="${item.turno}"/>
                                    </c:if>
                                    <c:if test="${not empty item.horaInicio}">
                                        - <c:out value="${item.horaInicio}"/> a
                                        <c:out value="${item.horaFim}"/>
                                    </c:if>
                                </td>
                                <td>
                                    <span class="badge text-bg-success"><c:out value="${item.disponivel}"/></span>
                                    <c:if test="${not empty item.disponiveisPorNome}">
                                        <div class="small mt-1">
                                            <c:forEach var="participante" items="${item.disponiveisPorNome}" varStatus="status">
                                                <c:out value="${participante.nome}"/><c:if test="${!status.last}">, </c:if>
                                            </c:forEach>
                                        </div>
                                    </c:if>
                                </td>
                                <td>
                                    <span class="badge text-bg-warning"><c:out value="${item.talvez}"/></span>
                                    <c:if test="${not empty item.talvezPorNome}">
                                        <div class="small mt-1">
                                            <c:forEach var="participante" items="${item.talvezPorNome}" varStatus="status">
                                                <c:out value="${participante.nome}"/><c:if test="${!status.last}">, </c:if>
                                            </c:forEach>
                                        </div>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>

    <h2 class="h4 mt-4">Convidados e respostas</h2>
    <c:choose>
        <c:when test="${empty convidados}">
            <div class="alert alert-light border">Nenhum convidado.</div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
            <table class="table table-striped table-bordered align-middle">
                <thead class="table-light">
                    <tr><th>E-mail</th><th>Resposta</th><th>Opcoes marcadas</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="convidado" items="${convidados}">
                        <tr>
                            <td>
                                <c:out value="${convidado.email}"/>
                            </td>
                            <td>
                                <c:out value="${convidado.confirmado ? 'Respondido' : 'Pendente'}"/>
                            </td>
                            <td>
                                <div class="d-flex align-items-center justify-content-end gap-2">
                                    <span><c:out value="${convidado.quantidadeDisponibilidades}"/></span>
                                    <a class="btn btn-sm btn-outline-primary"
                                       href="<c:url value='/resposta'><c:param name='token' value='${convidado.tokenResposta}'/><c:param name='readonly' value='1'/></c:url>">
                                        Ver resposta
                                    </a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            </div>
        </c:otherwise>
    </c:choose>
</main>
</body>
</html>
