-- ============================================================
--  Agenda Telefônica — Script do Banco de Dados
--  Projeto Integrador II A · PUC Goiás
-- ============================================================

-- Cria o banco (caso ainda não exista) e seleciona
CREATE DATABASE IF NOT EXISTS agenda
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE agenda;

-- Remove a tabela se já existir (útil para recriar do zero)
DROP TABLE IF EXISTS contatos;

-- Cria a tabela de contatos
CREATE TABLE contatos (
    id         INT          NOT NULL AUTO_INCREMENT,
    nome       VARCHAR(100) NOT NULL,
    telefone   VARCHAR(20)  NOT NULL,
    email      VARCHAR(100),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
--  Dados de exemplo (opcional — apague se não quiser)
-- ============================================================
INSERT INTO contatos (nome, telefone, email) VALUES
    ('Ana Lima',      '(62) 99001-1111', 'ana.lima@email.com'),
    ('Bruno Souza',   '(11) 98002-2222', 'bruno.souza@email.com'),
    ('Carla Mendes',  '(31) 97003-3333', NULL),
    ('Diego Ferreira','(21) 96004-4444', 'diego.f@email.com'),
    ('Elisa Castro',  '(62) 95005-5555', 'elisa.castro@email.com');
