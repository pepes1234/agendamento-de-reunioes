<%
    Object usuarioEmail = session.getAttribute("usuarioEmail");
    response.sendRedirect(request.getContextPath() + (usuarioEmail == null ? "/login" : "/reunioes"));
%>
