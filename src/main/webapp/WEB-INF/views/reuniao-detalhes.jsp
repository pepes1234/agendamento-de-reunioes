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
<main class="container py-4 bg-light">
    <a class="btn btn-link px-0" href="<c:url value='/reunioes'/>">&larr; Voltar</a>
    <div class="card border-0 shadow-sm mb-3">
        <div class="card-body p-3 p-md-4">
            <p class="text-primary fw-semibold text-uppercase small mb-1">Reuniao</p>
            <h1 class="h3 mb-2"><c:out value="${reuniao.titulo}"/></h1>
            <div class="text-secondary small">
                <span class="me-3"><strong>Periodo:</strong> <c:out value="${reuniao.periodoFormatado}"/></span>
                <c:if test="${not empty reuniao.localReuniao}">
                    <span><strong>Local:</strong> <c:out value="${reuniao.localReuniao}"/></span>
                </c:if>
            </div>
            <c:if test="${not empty reuniao.descricao}">
                <p class="mb-0 mt-3"><c:out value="${reuniao.descricao}"/></p>
            </c:if>
        </div>
    </div>

    <div class="card border shadow-sm mb-3">
        <div class="card-body p-3 p-md-4">
            <h2 class="h5 mb-3">Resumo dos horarios</h2>
            <c:forEach var="item" items="${resumoHorarios}">
                <div class="d-flex flex-column flex-sm-row align-items-sm-center justify-content-between gap-2 border rounded p-2 mb-2 bg-white">
                    <div>
                        <strong class="d-block"><c:out value="${item.dia}"/></strong>
                        <span class="text-secondary small d-block">
                            <c:choose>
                                <c:when test="${not empty item.horaInicio}">
                                    <c:out value="${item.horaInicio}"/> - <c:out value="${item.horaFim}"/>
                                </c:when>
                                <c:when test="${not empty item.periodo}">
                                    <c:out value="${item.periodo}"/>
                                </c:when>
                                <c:otherwise>Dia inteiro</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <span class="badge rounded-pill text-bg-success">
                        <c:out value="${item.disponivel}"/> disponivel
                    </span>
                    <span class="badge rounded-pill text-bg-warning">
                        <c:out value="${item.talvez}"/> talvez
                    </span>
                </div>
            </c:forEach>
        </div>
    </div>

    <div class="card border-0 shadow-sm">
        <div class="card-body p-3 p-md-4">
            <h2 class="h5 mb-3">Participantes</h2>
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light"><tr><th>Participante</th><th>Status</th><th>Horarios</th><th></th></tr></thead>
                    <tbody>
                    <c:forEach var="participante" items="${participantes}">
                        <tr>
                            <td>
                                <div class="fw-semibold"><c:out value="${empty participante.nome ? participante.email : participante.nome}"/></div>
                                <div class="text-secondary small"><c:out value="${participante.email}"/></div>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${participante.proponente}">
                                        <span class="badge text-bg-primary">Administrador</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge <c:choose><c:when test="${participante.respondidoEm == null}">text-bg-secondary</c:when><c:otherwise>text-bg-success</c:otherwise></c:choose>">
                                            <c:out value="${participante.respondidoEm == null ? 'Pendente' : 'Respondido'}"/>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${participante.proponente}">
                                        <span class="text-secondary">-</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="fw-semibold"><c:out value="${participante.quantidadeDisponibilidades}"/></span> selecionados
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-end">
                                <c:if test="${!participante.proponente}">
                                    <a class="btn btn-sm btn-outline-primary"
                                       href="<c:url value='/resposta'><c:param name='token' value='${participante.token}'/><c:param name='readonly' value='1'/></c:url>">Ver resposta</a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</main>
</body>
</html>
