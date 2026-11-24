package br.edu.agendamento.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.edu.agendamento.model.Reuniao;
import br.edu.agendamento.service.ReuniaoService;

@WebServlet(urlPatterns = {"/reunioes", "/reunioes/nova", "/reunioes/detalhes",
        "/reunioes/excluir", "/reunioes/publico", "/reunioes/admin"})
public class ReuniaoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Pattern EMAIL = Pattern.compile(
            "^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+$");
    private final ReuniaoService service = new ReuniaoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String caminho = request.getServletPath();
        try {
            Object usuarioEmail = request.getSession(false) == null
                    ? null : request.getSession(false).getAttribute("usuarioEmail");
            if (usuarioEmail == null
                    && !"/reunioes/publico".equals(caminho)
                    && !"/reunioes/admin".equals(caminho)) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            if ("/reunioes/nova".equals(caminho)) {
                request.getSession(true).setAttribute("formToken",
                        UUID.randomUUID().toString());
                request.getRequestDispatcher("/WEB-INF/views/reuniao-form.jsp")
                        .forward(request, response);
                return;
            }
            if ("/reunioes/publico".equals(caminho)) {
                Reuniao reuniao = service.buscarPorTokenPublico(request.getParameter("token"));
                if (reuniao == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                request.setAttribute("reuniao", reuniao);
                request.setAttribute("horarios", service.listarHorarios(reuniao.getId()));
                request.setAttribute("resumoHorarios", service.listarResumo(reuniao.getId()));
                request.setAttribute("consultaPublica", true);
                request.getRequestDispatcher("/WEB-INF/views/reuniao-detalhes.jsp")
                        .forward(request, response);
                return;
            }
            if ("/reunioes/admin".equals(caminho)) {
                Reuniao reuniao = service.buscarPorTokenAdmin(request.getParameter("token"));
                if (reuniao == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                long id = reuniao.getId();
                request.setAttribute("reuniao", reuniao);
                request.setAttribute("horarios", service.listarHorarios(id));
                request.setAttribute("participantes", service.listarParticipantes(id));
                request.setAttribute("resumoHorarios", service.listarResumo(id));
                request.setAttribute("painelAdmin", true);
                request.getRequestDispatcher("/WEB-INF/views/reuniao-detalhes.jsp")
                        .forward(request, response);
                return;
            }
            if ("/reunioes/detalhes".equals(caminho)) {
                long id = Long.parseLong(request.getParameter("id"));
                Reuniao reuniao = service.buscar(id);
                if (reuniao == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                request.setAttribute("reuniao", reuniao);
                request.setAttribute("horarios", service.listarHorarios(id));
                request.setAttribute("participantes", service.listarParticipantes(id));
                request.setAttribute("resumoHorarios", service.listarResumo(id));
                request.getRequestDispatcher("/WEB-INF/views/reuniao-detalhes.jsp")
                        .forward(request, response);
                return;
            }

            String email = usuarioEmail.toString();
            request.setAttribute("usuarioEmail", email);
            request.setAttribute("reunioesCriadas", service.listarCriadasPor(email));
            request.setAttribute("reunioesParticipadas", service.listarParticipadasPor(email));
            request.getRequestDispatcher("/WEB-INF/views/reunioes.jsp")
                    .forward(request, response);
        } catch (Exception exception) {
            encaminharErro(request, response, exception.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Object usuarioEmail = request.getSession(false) == null
                ? null : request.getSession(false).getAttribute("usuarioEmail");
        if (usuarioEmail == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if ("/reunioes/excluir".equals(request.getServletPath())) {
            try {
                long id = Long.parseLong(request.getParameter("id"));
                Reuniao reuniao = service.buscar(id);
                if (reuniao == null
                        || reuniao.getEmailProponente() == null
                        || !reuniao.getEmailProponente().equalsIgnoreCase(usuarioEmail.toString())) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                service.excluir(id);
                response.sendRedirect(request.getContextPath() + "/reunioes");
            } catch (Exception exception) {
                encaminharErro(request, response, exception.getMessage());
            }
            return;
        }

        try {
            String enviado = request.getParameter("formToken");
            Object esperado = request.getSession(false) == null
                    ? null : request.getSession(false).getAttribute("formToken");
            if (enviado == null || !enviado.equals(esperado)) {
                response.sendRedirect(request.getContextPath() + "/reunioes");
                return;
            }
            request.getSession(false).removeAttribute("formToken");

            Reuniao reuniao = new Reuniao();
            reuniao.setTitulo(request.getParameter("titulo"));
            reuniao.setDescricao(request.getParameter("descricao"));
            reuniao.setLocalReuniao(request.getParameter("localReuniao"));
            reuniao.setNomeProponente(request.getParameter("nomeProponente"));
            reuniao.setEmailProponente(usuarioEmail.toString());
            reuniao.setDataInicio(LocalDate.parse(request.getParameter("dataInicio")));
            reuniao.setDataFim(LocalDate.parse(request.getParameter("dataFim")));
            reuniao.setModoDivisao(request.getParameter("modoDivisao"));

            if ("HORAS".equals(reuniao.getModoDivisao())) {
                reuniao.setHoraInicio(LocalTime.parse(request.getParameter("horaInicio")));
                reuniao.setHoraFim(LocalTime.parse(request.getParameter("horaFim")));
                reuniao.setDuracaoMinutos(
                        Integer.parseInt(request.getParameter("duracaoMinutos")));
            }

            List<String> emails = separarEmails(request.getParameter("emails"));
            for (String email : emails) {
                if (!EMAIL.matcher(email).matches()) {
                    throw new IllegalArgumentException("E-mail invalido: " + email);
                }
            }

            service.criar(reuniao, emails);
            response.sendRedirect(request.getContextPath() + "/reunioes");
        } catch (IllegalArgumentException exception) {
            request.setAttribute("erro", exception.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/reuniao-form.jsp")
                    .forward(request, response);
        } catch (Exception exception) {
            encaminharErro(request, response, exception.getMessage());
        }
    }

    private List<String> separarEmails(String texto) {
        List<String> emails = new ArrayList<>();
        if (texto == null) {
            return emails;
        }
        for (String email : texto.split("[,;\\r\\n]+")) {
            String normalizado = email.trim();
            if (!normalizado.isEmpty() && !emails.contains(normalizado)) {
                emails.add(normalizado);
            }
        }
        return emails;
    }

    private void encaminharErro(HttpServletRequest request, HttpServletResponse response,
                                String mensagem) throws ServletException, IOException {
        request.setAttribute("erro", mensagem);
        request.getRequestDispatcher("/WEB-INF/views/erro.jsp").forward(request, response);
    }
}
