package br.edu.agendamento.model;

public class Participante {
    private long id;
    private long reuniaoId;
    private String nome;
    private String email;
    private String token;
    private boolean proponente;
    private java.time.LocalDateTime convidadoEm;
    private java.time.LocalDateTime respondidoEm;
    private java.time.LocalDateTime emailEnviadoEm;
    private int quantidadeDisponibilidades;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getReuniaoId() { return reuniaoId; }
    public void setReuniaoId(long reuniaoId) { this.reuniaoId = reuniaoId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public boolean isProponente() { return proponente; }
    public void setProponente(boolean proponente) { this.proponente = proponente; }
    public java.time.LocalDateTime getConvidadoEm() { return convidadoEm; }
    public void setConvidadoEm(java.time.LocalDateTime convidadoEm) { this.convidadoEm = convidadoEm; }
    public java.time.LocalDateTime getRespondidoEm() { return respondidoEm; }
    public void setRespondidoEm(java.time.LocalDateTime respondidoEm) { this.respondidoEm = respondidoEm; }
    public java.time.LocalDateTime getEmailEnviadoEm() { return emailEnviadoEm; }
    public void setEmailEnviadoEm(java.time.LocalDateTime emailEnviadoEm) { this.emailEnviadoEm = emailEnviadoEm; }
    public int getQuantidadeDisponibilidades() { return quantidadeDisponibilidades; }
    public void setQuantidadeDisponibilidades(int quantidadeDisponibilidades) {
        this.quantidadeDisponibilidades = quantidadeDisponibilidades;
    }
}
