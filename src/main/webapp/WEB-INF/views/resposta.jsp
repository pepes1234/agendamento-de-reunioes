<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Minha disponibilidade</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<main class="container py-4">
    <div class="row justify-content-center"><div class="col-lg-8">
    <c:if test="${readonly}">
        <a class="btn btn-outline-secondary mb-3"
           href="<c:url value='/reunioes/detalhes'><c:param name='id' value='${reuniao.id}'/></c:url>">&larr; Voltar</a>
    </c:if>
    <h1 class="h2"><c:out value="${reuniao.titulo}"/></h1>
    <p><strong>Periodo:</strong> <c:out value="${reuniao.periodoFormatado}"/></p>
    <c:if test="${param.salvo == '1'}"><div class="alert alert-success">Sua disponibilidade foi salva.</div></c:if>

    <form method="post" action="<c:url value='/resposta'/>" class="card shadow-sm p-4">
        <input type="hidden" name="token" value="${participante.token}">
        <c:if test="${not empty formularioFechado}">
            <div class="alert alert-warning">Esta reuniao ja recebeu 10 respostas e nao aceita novos envios.</div>
        </c:if>
        <c:forEach var="horario" items="${horarios}">
            <fieldset class="border rounded p-3 mb-3">
                <legend>
                    <c:out value="${horario.dia}"/>
                    <c:if test="${not empty horario.periodo}"> - <c:out value="${horario.periodo}"/></c:if>
                    <c:if test="${not empty horario.horaInicio}">
                        - <c:out value="${horario.horaInicio}"/> a <c:out value="${horario.horaFim}"/>
                    </c:if>
                </legend>
                <label class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" name="horario_${horario.id}" value="SIM"
                           <c:if test="${readonly || not empty formularioFechado}">disabled</c:if>
                           <c:if test="${respostas[horario.id] == 'SIM'}">checked</c:if>> Sim
                </label>
                <label class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" name="horario_${horario.id}" value="TALVEZ"
                           <c:if test="${readonly || not empty formularioFechado}">disabled</c:if>
                           <c:if test="${respostas[horario.id] == 'TALVEZ'}">checked</c:if>> Talvez
                </label>
            </fieldset>
        </c:forEach>
        <c:if test="${!readonly && empty formularioFechado}">
            <button class="btn btn-primary" type="submit">Salvar disponibilidade</button>
        </c:if>
    </form>
    </div></div>
</main>
</body>
</html>
