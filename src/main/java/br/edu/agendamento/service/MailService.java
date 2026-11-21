 package br.edu.agendamento.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import br.edu.agendamento.config.Environment;

public class MailService {
    public void enviar(String destinatario, String assunto, String texto) throws Exception {
        String host = Environment.get("MAIL_HOST");
        String usuario = Environment.get("MAIL_USER");
        String senha = Environment.get("MAIL_PASSWORD");
        if (host.isBlank()) {
            throw new IllegalStateException(
                    "Configuracao de e-mail ausente: preencha MAIL_HOST no arquivo .env.");
        }

        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", Environment.get("MAIL_PORT", "587"));
        properties.put("mail.smtp.auth", usuario.isBlank() ? "false" : "true");
        properties.put("mail.smtp.starttls.enable",
                Environment.get("MAIL_TLS", "true"));

        Session session = Session.getInstance(properties,
                usuario.isBlank() ? null : new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(usuario, senha);
                    }
                });

        MimeMessage mensagem = new MimeMessage(session);
        mensagem.setFrom(new InternetAddress(
                Environment.get("MAIL_FROM", usuario.isBlank()
                        ? "noreply@localhost" : usuario)));
        mensagem.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(destinatario));
        mensagem.setSubject(assunto, "UTF-8");
        mensagem.setText(texto, "UTF-8");
        Transport.send(mensagem);
    }
}
