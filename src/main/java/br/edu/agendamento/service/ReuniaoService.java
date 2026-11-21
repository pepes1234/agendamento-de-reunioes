package br.edu.agendamento.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.edu.agendamento.config.Environment;
import br.edu.agendamento.model.Convidado;
import br.edu.agendamento.model.OpcaoAgendamento;
import br.edu.agendamento.model.Reuniao;
import br.edu.agendamento.repository.ConvidadoRepository;
import br.edu.agendamento.repository.ReuniaoRepository;

public class ReuniaoService {
    private final ReuniaoRepository reuniaoRepository = new ReuniaoRepository();
    private final ConvidadoRepository convidadoRepository = new ConvidadoRepository();
    private final MailService mailService = new MailService();

    public Reuniao criar(Reuniao dados, List<String> emails) throws SQLException {
        validar(dados, emails);
        dados.setTokenExclusao(UUID.randomUUID().toString());
        Reuniao reuniao = reuniaoRepository.inserir(dados);
        reuniaoRepository.inserirOpcoes(reuniao.getId(), gerarOpcoes(reuniao));

        for (String email : emails) {
            String normalizado = email.trim().toLowerCase();
            Convidado convidado = convidadoRepository.inserir(reuniao.getId(), normalizado);
            String assunto = "Disponibilidade: " + reuniao.getNome();
            String link = Environment.get("APP_BASE_URL",
                    "http://localhost:8080")
                    + "/resposta?token=" + convidado.getTokenResposta();

            try {
                mailService.enviar(normalizado, assunto,
                        "Voce foi convidado para a reuniao \"" + reuniao.getNome() + "\".\n\n"
                                + "Informe sua disponibilidade neste link:\n" + link);
                convidadoRepository.registrarNotificacao(
                        convidado.getId(), assunto, "ENVIADO", null);
            } catch (Exception exception) {
                convidadoRepository.registrarNotificacao(
                        convidado.getId(), assunto, "FALHA", exception.getMessage());
            }
        }
        return reuniao;
    }

    public List<Reuniao> listar() throws SQLException {
        return reuniaoRepository.listarTodas();
    }

    public Reuniao buscar(long id) throws SQLException {
        return reuniaoRepository.buscarPorId(id);
    }

    public Reuniao buscarPorTokenExclusao(String token) throws SQLException {
        return reuniaoRepository.buscarPorTokenExclusao(token);
    }

    public void excluir(long id) throws SQLException {
        reuniaoRepository.excluir(id);
    }

    public List<OpcaoAgendamento> listarOpcoes(long reuniaoId) throws SQLException {
        return reuniaoRepository.listarOpcoes(reuniaoId);
    }

    public Convidado buscarConvidadoPorToken(String token) throws SQLException {
        return convidadoRepository.buscarPorToken(token);
    }

    public List<Convidado> listarConvidados(long reuniaoId) throws SQLException {
        return convidadoRepository.listarPorReuniao(reuniaoId);
    }

    public void salvarResposta(long convidadoId, java.util.Map<Long, String> respostas)
            throws SQLException {
        convidadoRepository.salvarResposta(convidadoId, respostas);
    }

    public java.util.Map<Long, String> listarRespostas(long convidadoId) throws SQLException {
        return convidadoRepository.listarRespostas(convidadoId);
    }

    public void atualizarNomeConvidado(long convidadoId, String nome) throws SQLException {
        convidadoRepository.atualizarNome(convidadoId, nome);
    }

    public int contarRespostasConfirmadas(long reuniaoId) throws SQLException {
        return convidadoRepository.contarRespostasConfirmadas(reuniaoId);
    }

    public java.util.List<br.edu.agendamento.model.ResumoOpcao> listarResumoRespostas(long reuniaoId)
            throws SQLException {
        return convidadoRepository.listarResumoRespostas(reuniaoId);
    }

    private void validar(Reuniao dados, List<String> emails) {
        if (dados.getNome() == null || dados.getNome().isBlank()) {
            throw new IllegalArgumentException("Informe o nome da reuniao.");
        }
        if (dados.getDataInicio() == null || dados.getDataFim() == null
                || dados.getDataFim().isBefore(dados.getDataInicio())) {
            throw new IllegalArgumentException("Informe um periodo de datas valido.");
        }
        if (emails == null || emails.isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos um convidado.");
        }
        if ("HORA".equals(dados.getFormato())
                && (dados.getHoraInicio() == null || dados.getHoraFim() == null
                || !dados.getHoraFim().isAfter(dados.getHoraInicio())
                || dados.getIntervaloMinutos() < 15)) {
            throw new IllegalArgumentException(
                    "Para horarios, informe inicio, fim e intervalo valido.");
        }
    }

    private List<OpcaoAgendamento> gerarOpcoes(Reuniao reuniao) {
        List<OpcaoAgendamento> opcoes = new ArrayList<>();
        for (LocalDate dia = reuniao.getDataInicio();
             !dia.isAfter(reuniao.getDataFim());
             dia = dia.plusDays(1)) {
            if ("TURNO".equals(reuniao.getFormato())) {
                for (String turno : new String[]{"MANHA", "TARDE", "NOITE"}) {
                    OpcaoAgendamento opcao = new OpcaoAgendamento();
                    opcao.setReuniaoId(reuniao.getId());
                    opcao.setDataOpcao(dia);
                    opcao.setTurno(turno);
                    opcoes.add(opcao);
                }
            } else {
                LocalTime inicio = reuniao.getHoraInicio();
                while (inicio.plusMinutes(reuniao.getIntervaloMinutos())
                        .compareTo(reuniao.getHoraFim()) <= 0) {
                    OpcaoAgendamento opcao = new OpcaoAgendamento();
                    opcao.setReuniaoId(reuniao.getId());
                    opcao.setDataOpcao(dia);
                    opcao.setHoraInicio(inicio);
                    opcao.setHoraFim(inicio.plusMinutes(reuniao.getIntervaloMinutos()));
                    opcoes.add(opcao);
                    inicio = inicio.plusMinutes(reuniao.getIntervaloMinutos());
                }
            }
        }
        return opcoes;
    }
}
