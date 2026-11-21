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
        "/reunioes/excluir"})
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
            if ("/reunioes/nova".equals(caminho)) {
                request.getSession(true).setAttribute("formToken",
                        UUID.randomUUID().toString());
                request.getRequestDispatcher("/WEB-INF/views/reuniao-form.jsp")
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
                request.setAttribute("opcoes", service.listarOpcoes(id));
                request.setAttribute("convidados", service.listarConvidados(id));
                request.setAttribute("resumoOpcoes", service.listarResumoRespostas(id));
                request.getRequestDispatcher("/WEB-INF/views/reuniao-detalhes.jsp")
                        .forward(request, response);
                return;
            }

            request.setAttribute("reunioes", service.listar());
            request.getRequestDispatcher("/WEB-INF/views/reunioes.jsp")
                    .forward(request, response);
        } catch (Exception exception) {
            encaminharErro(request, response, exception.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("/reunioes/excluir".equals(request.getServletPath())) {
            try {
                service.excluir(Long.parseLong(request.getParameter("id")));
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
            reuniao.setNome(request.getParameter("nome"));
            reuniao.setDataInicio(LocalDate.parse(request.getParameter("dataInicio")));
            reuniao.setDataFim(LocalDate.parse(request.getParameter("dataFim")));
            reuniao.setFormato(request.getParameter("formato"));

            if ("HORA".equals(reuniao.getFormato())) {
                reuniao.setHoraInicio(LocalTime.parse(request.getParameter("horaInicio")));
                reuniao.setHoraFim(LocalTime.parse(request.getParameter("horaFim")));
                reuniao.setIntervaloMinutos(
                        Integer.parseInt(request.getParameter("intervaloMinutos")));
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
