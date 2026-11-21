<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" href="data:,">
    <title>Nova reuniao</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<main class="container py-4">
    <div class="row justify-content-center">
        <div class="col-lg-8">
    <p class="text-primary fw-semibold text-uppercase small mb-1">Cadastro</p>
    <h1 class="h2 mb-4">Nova reuniao</h1>
    <c:if test="${not empty erro}">
        <div class="alert alert-danger"><c:out value="${erro}"/></div>
    </c:if>
    <form method="post" action="<c:url value='/reunioes'/>" class="card shadow-sm p-4">
        <input type="hidden" name="formToken" value="${sessionScope.formToken}">
        <div class="mb-3">
            <label class="form-label fw-semibold">Nome da reuniao</label>
            <input class="form-control" type="text" name="nome" maxlength="150" required>
        </div>
        <div class="row g-3 mb-3">
            <div class="col-md-6">
                <label class="form-label fw-semibold">Data inicial</label>
                <input class="form-control" type="date" name="dataInicio" required>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold">Data final</label>
                <input class="form-control" type="date" name="dataFim" required>
            </div>
        </div>
        <fieldset class="mb-3">
            <legend class="form-label fw-semibold">Formato</legend>
            <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" name="formato" id="formatoHora" value="HORA" checked>
                <label class="form-check-label" for="formatoHora">Por hora</label>
            </div>
            <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" name="formato" id="formatoTurno" value="TURNO">
                <label class="form-check-label" for="formatoTurno">Por turno</label>
            </div>
        </fieldset>
        <div class="row g-3 mb-3">
            <div class="col-md-4">
                <label class="form-label fw-semibold">Hora inicial</label>
                <input class="form-control" type="time" name="horaInicio" value="08:00">
            </div>
            <div class="col-md-4">
                <label class="form-label fw-semibold">Hora final</label>
                <input class="form-control" type="time" name="horaFim" value="18:00">
            </div>
            <div class="col-md-4">
                <label class="form-label fw-semibold">Intervalo</label>
                <select class="form-select" name="intervaloMinutos">
                    <option value="30">30 minutos</option>
                    <option value="60" selected>60 minutos</option>
                </select>
            </div>
        </div>
        <div class="mb-4">
            <label class="form-label fw-semibold">E-mails dos convidados</label>
            <textarea class="form-control" name="emails" rows="6"
                      placeholder="ana@exemplo.com&#10;joao@exemplo.com"
                      required></textarea>
        </div>
        <div class="d-flex gap-2">
            <button id="botaoCriar" class="btn btn-primary" type="submit">Criar reuniao</button>
            <a class="btn btn-outline-secondary" href="<c:url value='/reunioes'/>">Cancelar</a>
        </div>
    </form>
        </div>
    </div>
</main>
<script>
    document.querySelector("form").addEventListener("submit", function () {
        var botao = document.getElementById("botaoCriar");
        botao.disabled = true;
        botao.innerHTML = "Criando...";
    });
</script>
</body>
</html>
