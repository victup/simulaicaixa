INSERT INTO produto_investimento (id, nome, tipo, rentabilidade, risco, perfil_recomendado)
VALUES (101, 'CDB Caixa 2026', 'CDB', 0.12, 'Baixo', 'CONSERVADOR');

INSERT INTO produto_investimento (id, nome, tipo, rentabilidade, risco, perfil_recomendado)
VALUES (102, 'Fundo XPTO', 'Fundo', 0.18, 'Alto', 'AGRESSIVO');

INSERT INTO produto_investimento (id, nome, tipo, rentabilidade, risco, perfil_recomendado)
VALUES (103, 'CDB Caixa Liquidez Diária', 'CDB', 0.10, 'Baixo', 'MODERADO');

INSERT INTO perfil_risco (cliente_id, perfil, pontuacao, descricao)
VALUES (123, 'Moderado', 65, 'Perfil equilibrado entre segurança e rentabilidade.');