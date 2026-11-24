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
<main class="container py-3 py-md-4 bg-light">
    <div class="row justify-content-center"><div class="col-sm-11 col-md-9 col-lg-7">
    <c:if test="${readonly}">
        <a class="btn btn-outline-secondary mb-3"
           href="<c:url value='/reunioes/detalhes'><c:param name='id' value='${reuniao.id}'/></c:url>">&larr; Voltar</a>
    </c:if>
    <div class="card border-0 shadow-sm mx-auto">
        <div class="card-body p-3 p-md-4">
            <p class="text-primary fw-semibold text-uppercase small mb-1">
                <c:choose>
                    <c:when test="${readonly}">Resposta enviada</c:when>
                    <c:otherwise>Escolha seus horarios</c:otherwise>
                </c:choose>
            </p>
            <h1 class="h4 mb-1"><c:out value="${reuniao.titulo}"/></h1>
            <p class="text-secondary small mb-3">
                <c:out value="${reuniao.periodoFormatado}"/>
            </p>
            <c:if test="${param.salvo == '1'}">
                <div class="alert alert-success py-2">Sua disponibilidade foi salva.</div>
            </c:if>

            <form method="post" action="<c:url value='/resposta'/>">
                <input type="hidden" name="token" value="${participante.token}">
                <c:if test="${not empty formularioFechado}">
                    <div class="alert alert-warning py-2">Esta reuniao ja recebeu 10 respostas e nao aceita novos envios.</div>
                </c:if>
                <p class="text-secondary small mb-2">
                    Clique nos horarios em que voce pode participar.
                </p>
                <div class="row g-2">
                    <c:forEach var="horario" items="${horarios}">
                        <div class="col-12 col-sm-6">
                            <input class="btn-check" type="checkbox" id="horario_${horario.id}"
                                   name="horario_${horario.id}" value="SIM"
                                   <c:if test="${readonly || not empty formularioFechado}">disabled</c:if>
                                   <c:if test="${respostas[horario.id] == 'SIM' || respostas[horario.id] == 'TALVEZ'}">checked</c:if>>
                            <label class="btn <c:choose><c:when test="${respostas[horario.id] == 'TALVEZ'}">btn-outline-warning</c:when><c:otherwise>btn-outline-success</c:otherwise></c:choose> w-100 text-start py-2 px-3"
                                   for="horario_${horario.id}" data-state="<c:out value="${empty respostas[horario.id] ? '' : respostas[horario.id]}"/>">
                                <span class="fw-semibold d-block"><c:out value="${horario.dia}"/></span>
                                <span class="small d-block">
                                        <c:choose>
                                            <c:when test="${not empty horario.horaInicio}">
                                                <c:out value="${horario.horaInicio}"/> - <c:out value="${horario.horaFim}"/>
                                            </c:when>
                                            <c:when test="${not empty horario.periodo}">
                                                <c:out value="${horario.periodo}"/>
                                            </c:when>
                                            <c:otherwise>Dia inteiro</c:otherwise>
                                        </c:choose>
                                    </span>
                            </label>
                        </div>
                    </c:forEach>
                </div>
                <c:if test="${!readonly && empty formularioFechado}">
                    <div class="d-flex justify-content-end align-items-center gap-3 mt-4">
                        <span class="text-secondary small" id="contadorHorarios">0 horarios selecionados</span>
                        <button class="btn btn-primary px-4" type="submit">Salvar</button>
                    </div>
                </c:if>
            </form>
        </div>
    </div>
    </div></div>
</main>
<script>
    var contador = document.getElementById("contadorHorarios");
    var caixas = Array.prototype.slice.call(document.querySelectorAll(".btn-check:not(:disabled)"));

    function atualizarContador() {
        if (!contador) return;
        var total = caixas.filter(function (caixa) { return caixa.checked; }).length;
        contador.textContent = total + (total === 1 ? " horario selecionado" : " horarios selecionados");
    }

    function aplicarEstado(caixa, estado) {
        var rotulo = document.querySelector('label[for="' + caixa.id + '"]');
        caixa.value = estado;
        if (!rotulo) return;
        rotulo.dataset.state = estado;
        rotulo.classList.remove("btn-outline-success", "btn-outline-warning");
        rotulo.classList.add(estado === "TALVEZ" ? "btn-outline-warning" : "btn-outline-success");
    }

    caixas.forEach(function (caixa) {
        caixa.addEventListener("change", function () {
            aplicarEstado(caixa, caixa.checked ? "SIM" : "");
            atualizarContador();
        });
        var rotulo = caixa.nextElementSibling;
        rotulo.addEventListener("dblclick", function (evento) {
            evento.preventDefault();
            caixa.checked = true;
            aplicarEstado(caixa, "TALVEZ");
            atualizarContador();
        });

        if (caixa.checked) {
            aplicarEstado(caixa, caixa.nextElementSibling.dataset.state || "SIM");
        }
    });
    atualizarContador();
</script>
</body>
</html>
