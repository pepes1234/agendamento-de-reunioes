package br.edu.agendamento.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class OpcaoAgendamento {
    private long id;
    private long reuniaoId;
    private LocalDate dataOpcao;
    private String turno;
    private LocalTime horaInicio;
    private LocalTime horaFim;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getReuniaoId() { return reuniaoId; }
    public void setReuniaoId(long reuniaoId) { this.reuniaoId = reuniaoId; }
    public LocalDate getDataOpcao() { return dataOpcao; }
    public void setDataOpcao(LocalDate dataOpcao) { this.dataOpcao = dataOpcao; }
    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
}
