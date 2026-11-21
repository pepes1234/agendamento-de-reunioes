package br.edu.agendamento.model;

public class Convidado {
    private long id;
    private long reuniaoId;
    private String nome;
    private String email;
    private String tokenResposta;
    private boolean confirmado;
    private int quantidadeDisponibilidades;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getReuniaoId() { return reuniaoId; }
    public void setReuniaoId(long reuniaoId) { this.reuniaoId = reuniaoId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTokenResposta() { return tokenResposta; }
    public void setTokenResposta(String tokenResposta) { this.tokenResposta = tokenResposta; }
    public boolean isConfirmado() { return confirmado; }
    public void setConfirmado(boolean confirmado) { this.confirmado = confirmado; }
    public int getQuantidadeDisponibilidades() { return quantidadeDisponibilidades; }
    public void setQuantidadeDisponibilidades(int quantidadeDisponibilidades) {
        this.quantidadeDisponibilidades = quantidadeDisponibilidades;
    }
}
