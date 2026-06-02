-- ============================================================
-- SIGED v1.0 - Sistema de Gerenciamento de Estoque - CoDrive
-- Banco de Dados: siged_db
-- Disciplina: CMP1611 - Mini-Projeto de Software - PUC Goias
-- Autor: Maria Rita Verissimo
-- ============================================================

CREATE DATABASE IF NOT EXISTS siged_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE siged_db;

-- ------------------------------------------------------------
-- Tabela: categoria
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categoria (
    id   INT          NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: usuario
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id    INT          NOT NULL AUTO_INCREMENT,
    nome  VARCHAR(100) NOT NULL,
    login VARCHAR(50)  NOT NULL,
    senha VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_usuario_login (login)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: produto
-- (quantidade NAO e editavel manualmente — apenas via movimentacao)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS produto (
    id           INT            NOT NULL AUTO_INCREMENT,
    nome         VARCHAR(150)   NOT NULL,
    valor        DECIMAL(10, 2) NOT NULL,
    quantidade   INT            NOT NULL DEFAULT 0,
    id_categoria INT            NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_produto_categoria
        FOREIGN KEY (id_categoria)
        REFERENCES categoria (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Tabela: movimentacao
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS movimentacao (
    id         INT                      NOT NULL AUTO_INCREMENT,
    tipo       ENUM('ENTRADA', 'SAIDA') NOT NULL,
    quantidade INT                      NOT NULL,
    data       DATE                     NOT NULL,
    observacao VARCHAR(255)             NULL,
    id_produto INT                      NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_movimentacao_produto
        FOREIGN KEY (id_produto)
        REFERENCES produto (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Dados de Teste — Distribuidora de Eletronicos
-- ============================================================

-- Categorias
INSERT INTO categoria (nome) VALUES
    ('Notebooks'),
    ('Smartphones'),
    ('Perifericos'),
    ('Monitores'),
    ('Tablets');

-- Usuario padrao (login: admin / senha: admin123)
INSERT INTO usuario (nome, login, senha) VALUES
    ('Administrador', 'admin', 'admin123');

-- Produtos
-- (quantidades ja refletem o saldo apos todas as movimentacoes abaixo)
INSERT INTO produto (nome, valor, quantidade, id_categoria) VALUES
    ('Notebook Dell Inspiron 15 3520',     3499.00, 15, 1),
    ('Notebook Lenovo IdeaPad 3i',         2899.00, 20, 1),
    ('Smartphone Samsung Galaxy A54',      1899.00, 30, 2),
    ('Smartphone Apple iPhone 13',         4299.00, 12, 2),
    ('Mouse Logitech MX Master 3S',         599.00, 50, 3),
    ('Teclado Mecanico Redragon Kumara',    399.00, 40, 3),
    ('Monitor LG 27UK850-W 4K',           2199.00,  8, 4),
    ('Tablet Samsung Galaxy Tab A8',       1499.00, 25, 5);

-- Movimentacoes (16 registros: 8 ENTRADA + 8 SAIDA)
-- Saldo final por produto:
--   Produto 1: +20 -5  = 15  | Produto 2: +25 -5  = 20
--   Produto 3: +40 -10 = 30  | Produto 4: +15 -3  = 12
--   Produto 5: +60 -10 = 50  | Produto 6: +50 -10 = 40
--   Produto 7: +10 -2  =  8  | Produto 8: +30 -5  = 25
INSERT INTO movimentacao (tipo, quantidade, data, observacao, id_produto) VALUES
    ('ENTRADA', 20, '2025-01-10', 'Compra inicial — lote 01',           1),
    ('ENTRADA', 25, '2025-01-10', 'Compra inicial — lote 01',           2),
    ('ENTRADA', 40, '2025-01-15', 'Compra inicial — lote 01',           3),
    ('ENTRADA', 15, '2025-01-15', 'Compra inicial — lote 01',           4),
    ('ENTRADA', 60, '2025-02-01', 'Compra de reposicao',                5),
    ('ENTRADA', 50, '2025-02-01', 'Compra de reposicao',                6),
    ('ENTRADA', 10, '2025-02-10', 'Compra inicial — lote 01',           7),
    ('ENTRADA', 30, '2025-02-15', 'Compra inicial — lote 01',           8),
    ('SAIDA',    5, '2025-02-15', 'Venda — cliente TechShop Goiania',   1),
    ('SAIDA',    5, '2025-03-20', 'Venda — cliente InfoStore',          2),
    ('SAIDA',   10, '2025-02-28', 'Venda — cliente MobileMax',          3),
    ('SAIDA',    3, '2025-04-10', 'Venda — cliente Premium Distribui',  4),
    ('SAIDA',   10, '2025-03-05', 'Venda — cliente DataParts',          5),
    ('SAIDA',   10, '2025-04-20', 'Venda — cliente TechShop Goiania',   6),
    ('SAIDA',    2, '2025-05-15', 'Venda — cliente ScreenWorld',        7),
    ('SAIDA',    5, '2025-06-01', 'Venda — cliente MobileMax',          8);
