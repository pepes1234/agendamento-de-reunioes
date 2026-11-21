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
    <div class="d-flex align-items-center justify-content-between gap-3 mb-4">
        <div>
            <p class="text-primary fw-semibold text-uppercase small mb-1">Gerenciamento</p>
            <h1 class="h2 mb-0">Reunioes</h1>
        </div>
        <a class="btn btn-primary" href="<c:url value='/reunioes/nova'/>">Nova reuniao</a>
    </div>

    <c:choose>
        <c:when test="${empty reunioes}">
            <div class="alert alert-light border">Nenhuma reuniao cadastrada.</div>
        </c:when>
        <c:otherwise>
            <div class="row g-3">
                <c:forEach var="reuniao" items="${reunioes}">
                    <div class="col-md-6 col-lg-4">
                        <article class="card h-100 shadow-sm">
                            <div class="card-body d-flex flex-column">
                                <h2 class="h5 card-title"><c:out value="${reuniao.nome}"/></h2>
                                <p class="card-text mb-1"><strong>Periodo:</strong> <c:out value="${reuniao.periodoFormatado}"/></p>
                                <p class="card-text mb-4"><strong>Formato:</strong>
                                    <c:out value="${reuniao.formato == 'HORA' ? 'Por hora' : 'Por turno'}"/>
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
                                        "<strong><c:out value="${reuniao.nome}"/></strong>"
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
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
