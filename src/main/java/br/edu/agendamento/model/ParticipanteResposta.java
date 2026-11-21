package br.edu.agendamento.model;

public class ParticipanteResposta {
    private String nome;
    private String tokenResposta;

    public ParticipanteResposta(String nome, String tokenResposta) {
        this.nome = nome;
        this.tokenResposta = tokenResposta;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTokenResposta() { return tokenResposta; }
    public void setTokenResposta(String tokenResposta) { this.tokenResposta = tokenResposta; }
}
