INSERT INTO produto_investimento (id, nome, tipo, rentabilidade, risco, perfil_recomendado)
VALUES (101, 'CDB Caixa 2026', 1, 0.12, 1, 1);

INSERT INTO produto_investimento (id, nome, tipo, rentabilidade, risco, perfil_recomendado)
VALUES (102, 'Fundo XPTO', 2, 0.18, 3, 3);

INSERT INTO produto_investimento (id, nome, tipo, rentabilidade, risco, perfil_recomendado)
VALUES (103, 'CDB Caixa Liquidez Diária', 1, 0.10, 1, 2);

INSERT INTO perfil_risco (cliente_id, perfil, pontuacao, descricao)
VALUES (123, 2, 65, 'Perfil equilibrado entre segurança e rentabilidade.');

INSERT INTO perfil_risco (cliente_id, perfil, pontuacao, descricao)
VALUES (123, 3, 85, 'Perfil com apetite a risco em busca de maior rentabilidade.');

INSERT INTO perfil_risco (cliente_id, perfil, pontuacao, descricao)
VALUES (124, 1, 30, 'Perfil com foco em segurança e baixa volatilidade.');

INSERT INTO investimento (id, cliente_id, tipo, valor, rentabilidade, data)
VALUES (1, 123, 1, 5000.00, 0.12, '2025-01-15 00:00:00.000');

INSERT INTO investimento (id, cliente_id, tipo, valor, rentabilidade, data)
VALUES (2, 123, 2, 3000.00, 0.08, '2025-03-10 00:00:00.000');

INSERT INTO usuario_autenticacao (cpf, senha_hash, grupos)
VALUES ('12345678901', '$2a$10$3UNn/j3Imb8gi/CZzuMO0uJqa4i02fSKaBMkFUiQnHmU8Cql.7K1e', 'cliente,admin');

INSERT INTO usuario_autenticacao (cpf, senha_hash, grupos)
VALUES ('12345678902', '$2a$10$3UNn/j3Imb8gi/CZzuMO0uJqa4i02fSKaBMkFUiQnHmU8Cql.7K1e', 'cliente');