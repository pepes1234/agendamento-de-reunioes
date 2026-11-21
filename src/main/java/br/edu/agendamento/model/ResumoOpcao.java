package br.edu.agendamento.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ResumoOpcao {
    private long opcaoId;
    private LocalDate dataOpcao;
    private String turno;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private int disponivel;
    private int talvez;
    private int indisponivel;
    private final List<ParticipanteResposta> disponiveisPorNome = new ArrayList<>();
    private final List<ParticipanteResposta> talvezPorNome = new ArrayList<>();
    private final List<ParticipanteResposta> indisponiveisPorNome = new ArrayList<>();

    public long getOpcaoId() { return opcaoId; }
    public void setOpcaoId(long opcaoId) { this.opcaoId = opcaoId; }
    public LocalDate getDataOpcao() { return dataOpcao; }
    public void setDataOpcao(LocalDate dataOpcao) { this.dataOpcao = dataOpcao; }
    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
    public int getDisponivel() { return disponivel; }
    public void setDisponivel(int disponivel) { this.disponivel = disponivel; }
    public int getTalvez() { return talvez; }
    public void setTalvez(int talvez) { this.talvez = talvez; }
    public int getIndisponivel() { return indisponivel; }
    public void setIndisponivel(int indisponivel) { this.indisponivel = indisponivel; }
    public int getTotalRespondido() {
        return disponivel + talvez + indisponivel;
    }
    public List<ParticipanteResposta> getDisponiveisPorNome() { return disponiveisPorNome; }
    public List<ParticipanteResposta> getTalvezPorNome() { return talvezPorNome; }
    public List<ParticipanteResposta> getIndisponiveisPorNome() { return indisponiveisPorNome; }
}
