package br.edu.agendamento.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Reuniao {
    private long id;
    private String titulo;
    private String descricao;
    private String localReuniao;
    private String nomeProponente;
    private String emailProponente;
    private String tokenPublico;
    private String tokenAdmin;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String modoDivisao;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private Integer duracaoMinutos;
    private LocalDate criadaEm;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getLocalReuniao() { return localReuniao; }
    public void setLocalReuniao(String localReuniao) { this.localReuniao = localReuniao; }
    public String getNomeProponente() { return nomeProponente; }
    public void setNomeProponente(String nomeProponente) { this.nomeProponente = nomeProponente; }
    public String getEmailProponente() { return emailProponente; }
    public void setEmailProponente(String emailProponente) { this.emailProponente = emailProponente; }
    public String getTokenPublico() { return tokenPublico; }
    public void setTokenPublico(String tokenPublico) { this.tokenPublico = tokenPublico; }
    public String getTokenAdmin() { return tokenAdmin; }
    public void setTokenAdmin(String tokenAdmin) { this.tokenAdmin = tokenAdmin; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public String getModoDivisao() { return modoDivisao; }
    public void setModoDivisao(String modoDivisao) { this.modoDivisao = modoDivisao; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
    public Integer getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(Integer duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }
    public LocalDate getCriadaEm() { return criadaEm; }
    public void setCriadaEm(LocalDate criadaEm) { this.criadaEm = criadaEm; }

    public String getPeriodoFormatado() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (dataInicio.equals(dataFim)) {
            return dataInicio.format(formatter);
        }
        return dataInicio.format(formatter) + " a " + dataFim.format(formatter);
    }
}
