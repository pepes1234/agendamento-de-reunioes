<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" href="data:,">
    <title>Minha disponibilidade</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<main class="container py-4">
    <div class="row justify-content-center">
        <div class="col-lg-8">
    <c:if test="${readonly}">
        <a class="btn btn-outline-secondary mb-3"
           href="<c:url value='/reunioes/detalhes'><c:param name='id' value='${reuniao.id}'/></c:url>">
            &larr; Voltar
        </a>
    </c:if>
    <p class="text-primary fw-semibold text-uppercase small mb-1">Disponibilidade</p>
    <h1 class="h2"><c:out value="${reuniao.nome}"/></h1>
    <p><strong>Periodo:</strong> <c:out value="${reuniao.periodoFormatado}"/></p>

    <c:if test="${param.salvo == '1'}">
        <div class="alert alert-success">Sua disponibilidade foi salva.</div>
    </c:if>

    <form method="post" action="<c:url value='/resposta'/>" class="card shadow-sm p-4">
        <input type="hidden" name="token" value="${convidado.tokenResposta}">
        <c:if test="${not empty formularioFechado}">
            <div class="alert alert-warning">
                Esta reuniao ja recebeu 10 respostas e nao aceita novos envios.
            </div>
        </c:if>
        <c:if test="${empty formularioFechado}">
        <div class="mb-4">
            <label class="form-label fw-semibold">E-mail do convidado</label>
            <input class="form-control" type="email" value="<c:out value='${convidado.email}'/>"
                   readonly>
        </div>
        </c:if>
        <c:choose>
            <c:when test="${empty opcoes}">
                <p>Nenhuma opcao esta disponivel.</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="opcao" items="${opcoes}">
                    <fieldset class="border rounded p-3 mb-3">
                        <legend>
                            <c:out value="${opcao.dataOpcao}"/>
                            <c:if test="${not empty opcao.turno}">
                                - <c:out value="${opcao.turno}"/>
                            </c:if>
                            <c:if test="${not empty opcao.horaInicio}">
                                - <c:out value="${opcao.horaInicio}"/> a
                                <c:out value="${opcao.horaFim}"/>
                            </c:if>
                        </legend>
                        <label class="form-check form-check-inline">
                            <input class="form-check-input" type="radio" name="opcao_${opcao.id}" value="DISPONIVEL"
                                   <c:if test="${readonly || not empty formularioFechado}">disabled</c:if>
                                   <c:if test="${respostas[opcao.id] == 'DISPONIVEL'}">checked</c:if>>
                            Disponivel
                        </label>
                        <label class="form-check form-check-inline">
                            <input class="form-check-input" type="radio" name="opcao_${opcao.id}" value="TALVEZ"
                                   <c:if test="${readonly || not empty formularioFechado}">disabled</c:if>
                                   <c:if test="${respostas[opcao.id] == 'TALVEZ'}">checked</c:if>>
                            Talvez
                        </label>
                        <label class="form-check form-check-inline">
                            <input class="form-check-input" type="radio" name="opcao_${opcao.id}" value="INDISPONIVEL"
                                   <c:if test="${readonly || not empty formularioFechado}">disabled</c:if>
                                   <c:if test="${respostas[opcao.id] == 'INDISPONIVEL'}">checked</c:if>>
                            Indisponivel
                        </label>
                    </fieldset>
                </c:forEach>
                <c:if test="${!readonly && empty formularioFechado}">
                    <button id="botaoSalvar" class="btn btn-primary" type="submit">Salvar disponibilidade</button>
                </c:if>
            </c:otherwise>
        </c:choose>
    </form>
        </div>
    </div>
</main>
<script>
    var formulario = document.querySelector("form");
    var botaoSalvar = document.getElementById("botaoSalvar");
    if (formulario && botaoSalvar) {
        formulario.addEventListener("submit", function () {
            botaoSalvar.disabled = true;
            botaoSalvar.innerHTML = "Salvando...";
        });
    }
</script>
</body>
</html>
