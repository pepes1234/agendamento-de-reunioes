package br.edu.agendamento.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import br.edu.agendamento.config.Environment;
import br.edu.agendamento.model.Horario;
import br.edu.agendamento.model.Participante;
import br.edu.agendamento.model.Reuniao;
import br.edu.agendamento.model.ResumoHorario;
import br.edu.agendamento.repository.ParticipanteRepository;
import br.edu.agendamento.repository.ReuniaoRepository;

public class ReuniaoService {
    private final ReuniaoRepository reuniaoRepository = new ReuniaoRepository();
    private final ParticipanteRepository participanteRepository = new ParticipanteRepository();
    private final MailService mailService = new MailService();

    public Reuniao criar(Reuniao dados, List<String> emails) throws SQLException {
        return criar(dados, emails,
                Environment.get("APP_BASE_URL", "http://localhost:8080"));
    }

    public Reuniao criar(Reuniao dados, List<String> emails, String appBaseUrl) throws SQLException {
        validar(dados, emails);
        dados.setTokenPublico(UUID.randomUUID().toString());
        dados.setTokenAdmin(UUID.randomUUID().toString());
        Reuniao reuniao = reuniaoRepository.inserir(dados);
        reuniaoRepository.inserirHorarios(reuniao.getId(), gerarHorarios(reuniao));

        participanteRepository.inserir(reuniao.getId(), reuniao.getNomeProponente(),
                reuniao.getEmailProponente().trim().toLowerCase(), true);
        for (String email : emails) {
            String normalizado = email.trim().toLowerCase();
            if (normalizado.equalsIgnoreCase(reuniao.getEmailProponente())) continue;
            Participante participante = participanteRepository.inserir(
                    reuniao.getId(), null, normalizado, false);
            String assunto = "Disponibilidade: " + reuniao.getTitulo();
            String baseUrl = appBaseUrl == null ? "" : appBaseUrl.trim();
            if (baseUrl.isBlank()) {
                baseUrl = Environment.get("APP_BASE_URL", "http://localhost:8080");
            }
            while (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            String token = URLEncoder.encode(participante.getToken(), StandardCharsets.UTF_8);
            String link = baseUrl + "/resposta?token=" + token;
            try {
                mailService.enviar(normalizado, assunto,
                        "Voce foi convidado para a reuniao \"" + reuniao.getTitulo() + "\".\n\n"
                                + "Informe sua disponibilidade neste link:\n" + link);
                participanteRepository.registrarEmailEnviado(participante.getId());
            } catch (Exception ignored) {
            }
        }
        return reuniao;
    }

    public Reuniao buscar(long id) throws SQLException {
        return reuniaoRepository.buscarPorId(id);
    }

    public Reuniao buscarPorTokenAdmin(String token) throws SQLException {
        return reuniaoRepository.buscarPorTokenAdmin(token);
    }

    public Reuniao buscarPorTokenPublico(String token) throws SQLException {
        return reuniaoRepository.buscarPorTokenPublico(token);
    }

    public void excluir(long id) throws SQLException {
        reuniaoRepository.excluir(id);
    }

    public List<Reuniao> listarCriadasPor(String email) throws SQLException {
        return reuniaoRepository.listarCriadasPor(email);
    }

    public List<Reuniao> listarParticipadasPor(String email) throws SQLException {
        return reuniaoRepository.listarParticipadasPor(email);
    }

    public List<Horario> listarHorarios(long reuniaoId) throws SQLException {
        return reuniaoRepository.listarHorarios(reuniaoId);
    }

    public Participante buscarParticipantePorToken(String token) throws SQLException {
        return participanteRepository.buscarPorToken(token);
    }

    public List<Participante> listarParticipantes(long reuniaoId) throws SQLException {
        return participanteRepository.listarPorReuniao(reuniaoId);
    }

    public void salvarResposta(long participanteId, Map<Long, String> respostas) throws SQLException {
        participanteRepository.salvarResposta(participanteId, respostas);
    }

    public Map<Long, String> listarRespostas(long participanteId) throws SQLException {
        return participanteRepository.listarRespostas(participanteId);
    }

    public void atualizarNomeParticipante(long participanteId, String nome) throws SQLException {
        participanteRepository.atualizarNome(participanteId, nome);
    }

    public int contarRespondentes(long reuniaoId) throws SQLException {
        return participanteRepository.contarRespondentes(reuniaoId);
    }

    public List<ResumoHorario> listarResumo(long reuniaoId) throws SQLException {
        return participanteRepository.listarResumo(reuniaoId);
    }

    private void validar(Reuniao dados, List<String> emails) {
        if (dados.getTitulo() == null || dados.getTitulo().isBlank())
            throw new IllegalArgumentException("Informe o titulo da reuniao.");
        if (dados.getNomeProponente() == null || dados.getNomeProponente().isBlank())
            throw new IllegalArgumentException("Informe o nome do proponente.");
        if (dados.getEmailProponente() == null || dados.getEmailProponente().isBlank())
            throw new IllegalArgumentException("Informe o email do proponente.");
        if (dados.getDataInicio() == null || dados.getDataFim() == null
                || dados.getDataFim().isBefore(dados.getDataInicio()))
            throw new IllegalArgumentException("Informe um periodo de datas valido.");
        if (dados.getDataFim().isAfter(dados.getDataInicio().plusDays(9)))
            throw new IllegalArgumentException(
                    "O intervalo entre as datas nao pode ultrapassar 10 dias.");
        if (!List.of("DIA", "PERIODOS", "HORAS").contains(dados.getModoDivisao()))
            throw new IllegalArgumentException("Modo de divisao invalido.");
        if ("HORAS".equals(dados.getModoDivisao())
                && (dados.getHoraInicio() == null || dados.getHoraFim() == null
                || !dados.getHoraFim().isAfter(dados.getHoraInicio())
                || dados.getDuracaoMinutos() == null || dados.getDuracaoMinutos() <= 0))
            throw new IllegalArgumentException("Para horarios, informe inicio, fim e duracao valida.");
    }

    private List<Horario> gerarHorarios(Reuniao reuniao) {
        List<Horario> horarios = new ArrayList<>();
        for (LocalDate dia = reuniao.getDataInicio();
             !dia.isAfter(reuniao.getDataFim());
             dia = dia.plusDays(1)) {
            if ("PERIODOS".equals(reuniao.getModoDivisao())) {
                for (String periodo : new String[]{"MANHA", "TARDE", "NOITE"}) {
                    Horario horario = new Horario();
                    horario.setReuniaoId(reuniao.getId());
                    horario.setDia(dia);
                    horario.setPeriodo(periodo);
                    horarios.add(horario);
                }
            } else if ("HORAS".equals(reuniao.getModoDivisao())) {
                LocalTime inicio = reuniao.getHoraInicio();
                while (!inicio.plusMinutes(reuniao.getDuracaoMinutos()).isAfter(reuniao.getHoraFim())) {
                    Horario horario = new Horario();
                    horario.setReuniaoId(reuniao.getId());
                    horario.setDia(dia);
                    horario.setHoraInicio(inicio);
                    horario.setHoraFim(inicio.plusMinutes(reuniao.getDuracaoMinutos()));
                    horarios.add(horario);
                    inicio = inicio.plusMinutes(reuniao.getDuracaoMinutos());
                }
            } else {
                Horario horario = new Horario();
                horario.setReuniaoId(reuniao.getId());
                horario.setDia(dia);
                horarios.add(horario);
            }
        }
        return horarios;
    }
}
