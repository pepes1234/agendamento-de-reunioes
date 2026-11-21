package br.edu.agendamento.servlet;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.edu.agendamento.model.Convidado;
import br.edu.agendamento.model.Reuniao;
import br.edu.agendamento.service.ReuniaoService;

@WebServlet("/resposta")
public class RespostaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ReuniaoService service = new ReuniaoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        try {
            Convidado convidado = service.buscarConvidadoPorToken(token);
            if (convidado == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Link de disponibilidade invalido.");
                return;
            }
            Reuniao reuniao = service.buscar(convidado.getReuniaoId());
            boolean readonly = "1".equals(request.getParameter("readonly"));
            if (!readonly && service.contarRespostasConfirmadas(reuniao.getId()) >= 10
                    && !convidado.isConfirmado()) {
                request.setAttribute("reuniao", reuniao);
                request.setAttribute("formularioFechado", true);
                request.getRequestDispatcher("/WEB-INF/views/resposta.jsp")
                        .forward(request, response);
                return;
            }
            request.setAttribute("convidado", convidado);
            request.setAttribute("readonly", readonly);
            request.setAttribute("reuniao", reuniao);
            request.setAttribute("opcoes", service.listarOpcoes(reuniao.getId()));
            request.setAttribute("respostas", service.listarRespostas(convidado.getId()));
            request.getRequestDispatcher("/WEB-INF/views/resposta.jsp")
                    .forward(request, response);
        } catch (Exception exception) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    exception.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Convidado convidado = service.buscarConvidadoPorToken(
                    request.getParameter("token"));
            if (convidado == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            Reuniao reuniao = service.buscar(convidado.getReuniaoId());
            if (service.contarRespostasConfirmadas(reuniao.getId()) >= 10) {
                response.sendError(HttpServletResponse.SC_GONE,
                        "O prazo para respostas desta reuniao foi encerrado.");
                return;
            }

            Map<Long, String> respostas = new LinkedHashMap<>();
            Enumeration<String> parametros = request.getParameterNames();
            while (parametros.hasMoreElements()) {
                String parametro = parametros.nextElement();
                if (parametro.startsWith("opcao_")) {
                    long opcaoId = Long.parseLong(parametro.substring("opcao_".length()));
                    String nivel = request.getParameter(parametro);
                    if ("DISPONIVEL".equals(nivel) || "TALVEZ".equals(nivel)
                            || "INDISPONIVEL".equals(nivel)) {
                        respostas.put(opcaoId, nivel);
                    }
                }
            }
            service.salvarResposta(convidado.getId(), respostas);
            response.sendRedirect(request.getContextPath()
                    + "/resposta?token=" + request.getParameter("token") + "&salvo=1");
        } catch (Exception exception) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Nao foi possivel salvar sua disponibilidade: " + exception.getMessage());
        }
    }
}
