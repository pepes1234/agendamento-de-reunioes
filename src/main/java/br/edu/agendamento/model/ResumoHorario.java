package br.edu.agendamento.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ResumoHorario {
    private long horarioId;
    private LocalDate dia;
    private String periodo;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private int disponivel;
    private int talvez;
    private final List<ParticipanteResposta> disponiveisPorNome = new ArrayList<>();
    private final List<ParticipanteResposta> talvezPorNome = new ArrayList<>();

    public long getHorarioId() { return horarioId; }
    public void setHorarioId(long horarioId) { this.horarioId = horarioId; }
    public LocalDate getDia() { return dia; }
    public void setDia(LocalDate dia) { this.dia = dia; }
    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
    public int getDisponivel() { return disponivel; }
    public void setDisponivel(int disponivel) { this.disponivel = disponivel; }
    public int getTalvez() { return talvez; }
    public void setTalvez(int talvez) { this.talvez = talvez; }
    public int getTotalRespondido() {
        return disponivel + talvez;
    }
    public List<ParticipanteResposta> getDisponiveisPorNome() { return disponiveisPorNome; }
    public List<ParticipanteResposta> getTalvezPorNome() { return talvezPorNome; }
}
