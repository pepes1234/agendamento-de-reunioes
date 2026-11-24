<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" href="data:,">
    <title>Entrar</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<main class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-5 col-lg-4">
            <div class="card shadow-sm border-0">
                <div class="card-body p-4">
                    <p class="text-primary fw-semibold text-uppercase small mb-1">Agendamento</p>
                    <h1 class="h3 mb-3">Consultar minhas reunioes</h1>
                    <p class="text-secondary small mb-4">
                        Informe seu e-mail para localizar reunioes criadas por voce e em que voce participa.
                    </p>
                    <c:if test="${not empty erro}">
                        <div class="alert alert-danger"><c:out value="${erro}"/></div>
                    </c:if>
                    <form method="post" action="<c:url value='/login'/>">
                        <div class="mb-3">
                            <label class="form-label fw-semibold" for="email">E-mail</label>
                            <input id="email" class="form-control" type="email" name="email"
                                   autocomplete="email" required>
                        </div>
                        <button class="btn btn-primary w-100" type="submit">Entrar</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</main>
</body>
</html>
