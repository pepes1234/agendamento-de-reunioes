<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" href="data:,">
    <title>Reunioes</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<main class="container py-4">
    <div class="d-flex align-items-center justify-content-between gap-3 mb-2">
        <div>
            <p class="text-primary fw-semibold text-uppercase small mb-1">Gerenciamento</p>
            <h1 class="h2 mb-0">Reunioes</h1>
        </div>
        <div class="d-flex gap-2">
            <a class="btn btn-primary" href="<c:url value='/reunioes/nova'/>">Nova reuniao</a>
            <a class="btn btn-outline-secondary" href="<c:url value='/logout'/>">Sair</a>
        </div>
    </div>
    <p class="text-secondary mb-4">Conectado como <strong><c:out value="${usuarioEmail}"/></strong></p>

    <h2 class="h4 mt-4">Criadas por mim</h2>
    <c:choose>
        <c:when test="${empty reunioesCriadas}">
            <div class="alert alert-light border">Voce ainda nao criou reunioes.</div>
        </c:when>
        <c:otherwise>
            <div class="row g-3 mb-4">
                <c:forEach var="reuniao" items="${reunioesCriadas}">
                    <div class="col-md-6 col-lg-4">
                        <article class="card h-100 shadow-sm">
                            <div class="card-body d-flex flex-column">
                                <h2 class="h5 card-title"><c:out value="${reuniao.titulo}"/></h2>
                                <p class="card-text mb-1"><strong>Periodo:</strong> <c:out value="${reuniao.periodoFormatado}"/></p>
                                <p class="card-text mb-4"><strong>Formato:</strong>
                                    <c:out value="${reuniao.modoDivisao == 'HORAS' ? 'Por horas' : (reuniao.modoDivisao == 'PERIODOS' ? 'Por periodos' : 'Por dia')}"/>
                                </p>
                                <div class="mt-auto d-flex gap-2">
                                    <a class="btn btn-outline-primary"
                                       href="<c:url value='/reunioes/detalhes'><c:param name='id' value='${reuniao.id}'/></c:url>">
                                        Acompanhar
                                    </a>
                                    <button class="btn btn-outline-danger"
                                            type="button"
                                            data-bs-toggle="modal"
                                            data-bs-target="#modalExcluir${reuniao.id}">
                                        Excluir
                                    </button>
                                </div>
                            </div>
                        </article>
                        <div class="modal fade" id="modalExcluir${reuniao.id}" tabindex="-1"
                             aria-labelledby="tituloExcluir${reuniao.id}" aria-hidden="true">
                            <div class="modal-dialog modal-dialog-centered">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h2 class="modal-title fs-5" id="tituloExcluir${reuniao.id}">
                                            Confirmar exclusao
                                        </h2>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                aria-label="Fechar"></button>
                                    </div>
                                    <div class="modal-body">
                                        Excluir a reuniao
                                        "<strong><c:out value="${reuniao.titulo}"/></strong>"
                                        e todos os dados associados?
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary"
                                                data-bs-dismiss="modal">Cancelar</button>
                                        <form method="post" action="<c:url value='/reunioes/excluir'/>">
                                            <input type="hidden" name="id" value="${reuniao.id}">
                                            <button class="btn btn-danger" type="submit">Excluir reuniao</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>

    <h2 class="h4 mt-4">Em que participo</h2>
    <c:choose>
        <c:when test="${empty reunioesParticipadas}">
            <div class="alert alert-light border">Voce nao consta como participante de nenhuma reuniao.</div>
        </c:when>
        <c:otherwise>
            <div class="row g-3">
                <c:forEach var="reuniao" items="${reunioesParticipadas}">
                    <div class="col-md-6 col-lg-4">
                        <article class="card h-100 shadow-sm">
                            <div class="card-body d-flex flex-column">
                                <h3 class="h5 card-title"><c:out value="${reuniao.titulo}"/></h3>
                                <p class="card-text mb-1"><strong>Periodo:</strong> <c:out value="${reuniao.periodoFormatado}"/></p>
                                <p class="card-text mb-4"><strong>Criada por:</strong>
                                    <c:out value="${reuniao.nomeProponente}"/>
                                </p>
                                <div class="mt-auto">
                                    <a class="btn btn-outline-primary"
                                       href="<c:url value='/reunioes/detalhes'><c:param name='id' value='${reuniao.id}'/></c:url>">
                                        Ver reuniao
                                    </a>
                                </div>
                            </div>
                        </article>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
