package br.edu.agendamento.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Reuniao {
    private long id;
    private String nome;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String formato;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private int intervaloMinutos;
    private String tokenExclusao;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
    public int getIntervaloMinutos() { return intervaloMinutos; }
    public void setIntervaloMinutos(int intervaloMinutos) { this.intervaloMinutos = intervaloMinutos; }
    public String getTokenExclusao() { return tokenExclusao; }
    public void setTokenExclusao(String tokenExclusao) { this.tokenExclusao = tokenExclusao; }

    public String getPeriodoFormatado() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (dataInicio.equals(dataFim)) {
            return dataInicio.format(formatter);
        }
        return dataInicio.format(formatter) + " a " + dataFim.format(formatter);
    }
}
