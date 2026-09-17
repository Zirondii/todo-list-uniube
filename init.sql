-- Script executado automaticamente pelo Docker na primeira vez que o banco sobe.
-- Cria as tabelas e ja insere dados para facilitar os testes.

CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL
);

CREATE TABLE tarefas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    concluida BOOLEAN NOT NULL DEFAULT FALSE,
    criada_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Usuarios para teste
INSERT INTO usuario (login, senha) VALUES ('admin', 'admin');
INSERT INTO usuario (login, senha) VALUES ('natan', '123456');

-- Tarefas de exemplo do usuario admin
INSERT INTO tarefas (usuario_id, titulo, descricao, concluida) VALUES
    (1, 'Estudar o padrao MVC', 'Revisar os slides sobre Controller, Service e View', TRUE),
    (1, 'Montar o banco de dados', 'Criar as tabelas de usuario e tarefas no MySQL', TRUE),
    (1, 'Preparar a apresentacao', 'Explicar a estrutura de pastas e o fluxo de uma requisicao', FALSE);
