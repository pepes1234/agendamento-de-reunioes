<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" href="data:,">
    <title>Erro</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<main class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-7">
            <h1 class="h2">Ocorreu um problema</h1>
            <div class="alert alert-danger"><c:out value="${erro}" default="Nao foi possivel concluir a operacao."/></div>
            <a class="btn btn-outline-secondary" href="<c:url value='/reunioes'/>">Voltar</a>
        </div>
    </div>
</main>
</body>
</html>
