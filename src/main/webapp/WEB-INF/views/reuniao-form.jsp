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
            <label class="form-label fw-semibold">Titulo da reuniao</label>
            <input class="form-control" type="text" name="titulo" maxlength="150" required>
        </div>
        <div class="mb-3">
            <label class="form-label fw-semibold">Descricao</label>
            <textarea class="form-control" name="descricao" maxlength="1000" rows="3"></textarea>
        </div>
        <div class="row g-3 mb-3">
            <div class="col-md-6">
                <label class="form-label fw-semibold">Local</label>
                <input class="form-control" type="text" name="localReuniao" maxlength="200">
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold">Nome do proponente</label>
                <input class="form-control" type="text" name="nomeProponente" maxlength="100" required>
            </div>
        </div>
        <div class="row g-3 mb-3">
            <div class="col-md-6">
                <label class="form-label fw-semibold">Data inicial</label>
                <input class="form-control" type="date" name="dataInicio" id="dataInicio" required>
            </div>
            <div class="col-md-6">
                <label class="form-label fw-semibold">Data final</label>
                <input class="form-control" type="date" name="dataFim" id="dataFim" required>
            </div>
        </div>
        <div class="form-text mb-3">O intervalo entre as duas datas pode ter no maximo 10 dias.</div>
        <fieldset class="mb-3">
            <legend class="form-label fw-semibold">Modo de divisao</legend>
            <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" name="modoDivisao" id="modoDia" value="DIA">
                <label class="form-check-label" for="modoDia">Por dia</label>
            </div>
            <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" name="modoDivisao" id="modoPeriodos" value="PERIODOS" checked>
                <label class="form-check-label" for="modoPeriodos">Por periodos</label>
            </div>
            <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" name="modoDivisao" id="modoHoras" value="HORAS">
                <label class="form-check-label" for="modoHoras">Por horas</label>
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
                <select class="form-select" name="duracaoMinutos">
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
    var dataInicio = document.getElementById("dataInicio");
    var dataFim = document.getElementById("dataFim");

    function atualizarLimiteDataFim() {
        if (!dataInicio.value) {
            dataFim.removeAttribute("min");
            dataFim.removeAttribute("max");
            return;
        }
        var inicio = new Date(dataInicio.value + "T00:00:00");
        var limite = new Date(inicio);
        limite.setDate(limite.getDate() + 9);
        dataFim.min = dataInicio.value;
        dataFim.max = limite.toISOString().slice(0, 10);
        if (dataFim.value && (dataFim.value < dataInicio.value || dataFim.value > dataFim.max)) {
            dataFim.value = "";
        }
    }

    dataInicio.addEventListener("change", atualizarLimiteDataFim);

    document.querySelector("form").addEventListener("submit", function () {
        var botao = document.getElementById("botaoCriar");
        botao.disabled = true;
        botao.innerHTML = "Criando...";
    });
</script>
</body>
</html>
