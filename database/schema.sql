CREATE DATABASE IF NOT EXISTS sistema_reunioes
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sistema_reunioes;

CREATE TABLE IF NOT EXISTS reuniao (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    criador_email VARCHAR(254) NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    formato ENUM('HORA', 'TURNO') NOT NULL,
    hora_inicio TIME NULL,
    hora_fim TIME NULL,
    intervalo_minutos SMALLINT UNSIGNED NULL,
    token_exclusao CHAR(36) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_reuniao_token_exclusao (token_exclusao),
    KEY idx_reuniao_datas (data_inicio, data_fim),
    CONSTRAINT ck_reuniao_periodo CHECK (data_fim >= data_inicio)
) ENGINE=InnoDB;

ALTER TABLE reuniao
    ADD COLUMN IF NOT EXISTS criador_email VARCHAR(254) NULL AFTER nome,
    ADD INDEX IF NOT EXISTS idx_reuniao_criador_email (criador_email);

CREATE TABLE IF NOT EXISTS opcao_agendamento (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    reuniao_id BIGINT UNSIGNED NOT NULL,
    data_opcao DATE NOT NULL,
    turno ENUM('MANHA', 'TARDE', 'NOITE') NULL,
    hora_inicio TIME NULL,
    hora_fim TIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_opcao_reuniao (reuniao_id, data_opcao, turno, hora_inicio),
    KEY idx_opcao_reuniao_data (reuniao_id, data_opcao),
    CONSTRAINT fk_opcao_reuniao
        FOREIGN KEY (reuniao_id) REFERENCES reuniao (id)
        ON DELETE CASCADE,
    CONSTRAINT ck_opcao_horario CHECK (
        (turno IS NOT NULL AND hora_inicio IS NULL AND hora_fim IS NULL)
        OR
        (turno IS NULL AND hora_inicio IS NOT NULL AND hora_fim IS NOT NULL
         AND hora_fim > hora_inicio)
    )
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS convidado (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    reuniao_id BIGINT UNSIGNED NOT NULL,
    nome VARCHAR(150) NULL,
    email VARCHAR(254) NOT NULL,
    token_resposta CHAR(36) NOT NULL,
    confirmado BOOLEAN NOT NULL DEFAULT FALSE,
    respondido_em DATETIME NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_convidado_resposta (token_resposta),
    UNIQUE KEY uk_convidado_reuniao_email (reuniao_id, email),
    CONSTRAINT fk_convidado_reuniao
        FOREIGN KEY (reuniao_id) REFERENCES reuniao (id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS disponibilidade (
    convidado_id BIGINT UNSIGNED NOT NULL,
    opcao_id BIGINT UNSIGNED NOT NULL,
    nivel ENUM('INDISPONIVEL', 'TALVEZ', 'DISPONIVEL') NOT NULL DEFAULT 'DISPONIVEL',
    respondido_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (convidado_id, opcao_id),
    CONSTRAINT fk_disponibilidade_convidado
        FOREIGN KEY (convidado_id) REFERENCES convidado (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_disponibilidade_opcao
        FOREIGN KEY (opcao_id) REFERENCES opcao_agendamento (id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS notificacao_email (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    convidado_id BIGINT UNSIGNED NOT NULL,
    assunto VARCHAR(255) NOT NULL,
    status ENUM('PENDENTE', 'ENVIADO', 'FALHA') NOT NULL DEFAULT 'PENDENTE',
    tentativas TINYINT UNSIGNED NOT NULL DEFAULT 0,
    enviado_em DATETIME NULL,
    ultimo_erro TEXT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_notificacao_status (status),
    CONSTRAINT fk_notificacao_convidado
        FOREIGN KEY (convidado_id) REFERENCES convidado (id)
        ON DELETE CASCADE
) ENGINE=InnoDB;
