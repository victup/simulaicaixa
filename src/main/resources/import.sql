INSERT INTO produto_investimento (id, nome, tipo, rentabilidade, risco, perfil_recomendado)
VALUES (101, 'CDB Caixa 2026', 'CDB', 0.12, 'Baixo', 'CONSERVADOR');

INSERT INTO produto_investimento (id, nome, tipo, rentabilidade, risco, perfil_recomendado)
VALUES (102, 'Fundo XPTO', 'Fundo', 0.18, 'Alto', 'AGRESSIVO');

INSERT INTO produto_investimento (id, nome, tipo, rentabilidade, risco, perfil_recomendado)
VALUES (103, 'CDB Caixa Liquidez Diária', 'CDB', 0.10, 'Baixo', 'MODERADO');

INSERT INTO perfil_risco (cliente_id, perfil, pontuacao, descricao)
VALUES (123, 'Moderado', 65, 'Perfil equilibrado entre segurança e rentabilidade.');

INSERT INTO investimento (id, cliente_id, tipo, valor, rentabilidade, data)
VALUES (1, 123, 'CDB', 5000.00, 0.12, '2025-01-15 00:00:00.000');

INSERT INTO investimento (id, cliente_id, tipo, valor, rentabilidade, data)
VALUES (2, 123, 'Fundo Multimercado', 3000.00, 0.08, '2025-03-10 00:00:00.000');

INSERT INTO telemetria_servico (
    id,
    nome,
    quantidade_chamadas,
    media_tempo_resposta_ms,
    periodo_inicio,
    periodo_fim
) VALUES (
    1,
    'simular-investimento',
    120,
    250,
    '2025-10-01 00:00:00.000',
    '2025-10-31 00:00:00.000'
);

INSERT INTO telemetria_servico (
    id,
    nome,
    quantidade_chamadas,
    media_tempo_resposta_ms,
    periodo_inicio,
    periodo_fim
) VALUES (
    2,
    'perfil-risco',
    80,
    180,
    '2025-10-01 00:00:00.000',
    '2025-10-31 00:00:00.000'
);