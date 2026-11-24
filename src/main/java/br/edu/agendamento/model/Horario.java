package br.edu.agendamento.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Horario {
    private long id;
    private long reuniaoId;
    private LocalDate dia;
    private String periodo;
    private LocalTime horaInicio;
    private LocalTime horaFim;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getReuniaoId() { return reuniaoId; }
    public void setReuniaoId(long reuniaoId) { this.reuniaoId = reuniaoId; }
    public LocalDate getDia() { return dia; }
    public void setDia(LocalDate dia) { this.dia = dia; }
    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
}
