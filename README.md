# Agendamento de Reunioes

## Requisitos

- JDK 17
- Apache Tomcat 9.0.x. Tomcat 10 ou superior nao e compativel com esta versao.

## Configuracao do Tomcat

Crie o arquivo `setenv.bat` na pasta `bin` do Tomcat usado pelo NetBeans. Neste ambiente, o caminho e:

```text
C:\apache-tomcat-9.0.124\bin\setenv.bat
```

Adicione as configuracoes usando `set`:

```bat
@echo off
set "DB_URL=jdbc:mysql://localhost:3306/sistema_reunioes?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
set "DB_USER=seu_usuario"
set "DB_PASSWORD=sua_senha"

set "MAIL_HOST=smtp.seuprovedor.com"
set "MAIL_PORT=587"
set "MAIL_USER=seu_email"
set "MAIL_PASSWORD=sua_senha_ou_token_smtp"
set "MAIL_FROM=seu_email"
set "MAIL_TLS=true"
set "APP_BASE_URL=http://localhost:8080"
```

O e-mail e usado para enviar convites aos participantes. Preencha `MAIL_HOST` com o servidor SMTP do seu provedor; use a porta e as credenciais fornecidas por ele. `APP_BASE_URL` e o endereco que vai nos links dos convites. Sem essas configuracoes, os convites por e-mail nao serao enviados.

Se o NetBeans usar outro `CATALINA_BASE`, crie o arquivo na pasta `bin` desse Tomcat. Reinicie o servidor depois de alterar o arquivo. Mantenha suas credenciais fora do repositorio.
