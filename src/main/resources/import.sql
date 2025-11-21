-- tipo: 1=CDB, 2=FUNDO, 3=LCI, 4=LCA, 5=TESOURO
-- risco: 1=BAIXO, 2=MEDIO, 3=ALTO
-- perfil_recomendado: 1=CONSERVADOR, 2=MODERADO, 3=AGRESSIVO

INSERT INTO produto_investimento (id, nome, tipo, rentabilidade, risco, perfil_recomendado) VALUES
(101, 'CDB Caixa 2026',               1, 0.1200, 1, 1),
(102, 'Fundo XPTO Ações',             2, 0.1800, 3, 3),
(103, 'CDB Caixa Liquidez Diária',    1, 0.1000, 1, 2),
(104, 'LCI Caixa Imobiliária',        3, 0.0950, 1, 1),
(105, 'LCA Caixa Agronegócio',        4, 0.1020, 2, 2),
(106, 'Fundo Multimercado Dinâmico',  2, 0.1550, 2, 3),
(107, 'Tesouro Selic 2029',           5, 0.0900, 1, 1),
(108, 'Tesouro IPCA 2035',            5, 0.1350, 2, 2);


-- perfil: 1=CONSERVADOR, 2=MODERADO, 3=AGRESSIVO
INSERT INTO perfil_risco (cliente_id, perfil, pontuacao, descricao) VALUES
(123, 2, 65, 'Perfil equilibrado entre segurança e rentabilidade.'),
(124, 1, 30, 'Perfil com foco em segurança e baixa volatilidade.'),
(125, 3, 85, 'Perfil com apetite a risco em busca de maior rentabilidade.'),
(126, 2, 55, 'Perfil moderado com leve inclinação à rentabilidade.');


-- tipo: 1=CDB, 2=FUNDO, 3=LCI, 4=LCA, 5=TESOURO
-- Cliente 123: moderado, com mix de produtos, volume médio e frequência razoável
INSERT INTO investimento (id, cliente_id, tipo, valor, rentabilidade, data) VALUES
(1,  123, 1,  5000.00, 0.1200, '2025-01-15 00:00:00.000'),
(2,  123, 2,  3000.00, 0.0800, '2025-03-10 00:00:00.000'),
(3,  123, 1,  7000.00, 0.1100, '2025-05-05 00:00:00.000'),
(4,  123, 5,  2000.00, 0.0900, '2025-06-20 00:00:00.000'),
(5,  123, 2,  4000.00, 0.1500, '2025-08-01 00:00:00.000');

-- Cliente 124: conservador, poucas movimentações e valores mais concentrados em baixo risco
INSERT INTO investimento (id, cliente_id, tipo, valor, rentabilidade, data) VALUES
(6,  124, 1,  8000.00, 0.0900, '2025-02-01 00:00:00.000'),
(7,  124, 5,  4000.00, 0.0850, '2025-04-15 00:00:00.000');

-- Cliente 125: agressivo, muitas movimentações, foco em fundos mais arriscados
INSERT INTO investimento (id, cliente_id, tipo, valor, rentabilidade, data) VALUES
(8,  125, 2,  4000.00, 0.1700, '2025-01-20 00:00:00.000'),
(9,  125, 2,  6000.00, 0.1850, '2025-02-10 00:00:00.000'),
(10, 125, 2,  5000.00, 0.1600, '2025-03-05 00:00:00.000'),
(11, 125, 4,  3000.00, 0.1400, '2025-04-12 00:00:00.000'),
(12, 125, 2,  7000.00, 0.1900, '2025-06-01 00:00:00.000');

-- Cliente 126: moderado, histórico mais recente e diversificado
INSERT INTO investimento (id, cliente_id, tipo, valor, rentabilidade, data) VALUES
(13, 126, 1,  3000.00, 0.1000, '2025-03-01 00:00:00.000'),
(14, 126, 3,  3500.00, 0.0950, '2025-04-01 00:00:00.000'),
(15, 126, 2,  2500.00, 0.1300, '2025-05-15 00:00:00.000');

INSERT INTO usuario_autenticacao (cpf, senha_hash, grupos, cliente_id) VALUES
('12345678901', '$2a$10$3UNn/j3Imb8gi/CZzuMO0uJqa4i02fSKaBMkFUiQnHmU8Cql.7K1e', 'cliente,admin', 123),
('12345678902', '$2a$10$3UNn/j3Imb8gi/CZzuMO0uJqa4i02fSKaBMkFUiQnHmU8Cql.7K1e', 'cliente', 124),
('12345678903', '$2a$10$3UNn/j3Imb8gi/CZzuMO0uJqa4i02fSKaBMkFUiQnHmU8Cql.7K1e', 'cliente', 125),
('99999999999', '$2a$10$3UNn/j3Imb8gi/CZzuMO0uJqa4i02fSKaBMkFUiQnHmU8Cql.7K1e', 'admin', NULL);
