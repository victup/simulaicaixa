# SimulaICaixa -Simulação e Recomendação de Investimentps

API em **Java 21 + Quarkus 3.x**, voltada para **simulação de investimentos**, **recomendação de produtos** e **telemetria de chamadas**, utilizando **JWT**, **JPA/Hibernate** e **SQLite**.

---

### Chaves JWT (`keys/publicKey.pem` e `keys/privateKey.pem`)

As chaves RSA utilizadas para assinar e validar os tokens JWT estão versionadas neste repositório **exclusivamente para fins de demonstração**, facilitando a execução do projeto sem configuração adicional.

> **Atenção:** em ambientes reais (produção, homologação etc.), essas chaves **não seriam ser reutilizadas** nem versionadas. Novas chaves seriam armazenadas em um cofre de segredos.


## 1. Execução local (sem Docker)

### 1.1. Requisitos

- Java 21
- Maven 3.9+
- (Opcional) Postman/Insomnia para testar a API - o projeto já oferece interface swagger para uso.

### 1.2. Rodar os testes e gerar relatório de cobertura

```bash
mvn test
```

```bash
mvn clean verify
```


Relatório JaCoCo disponível em:

```text
target/site/jacoco/index.html
```

### 1.3. Gerar Javadoc da aplicação

```bash
mvn javadoc:javadoc
```

Javadoc gerado em:

```text
target/site/apidocs/index.html
```

### 1.4. Build do projeto

```bash
mvn clean package
```

### 1.5. Subir em modo dev (recomendado para testes)

```bash
mvn quarkus:dev
```

A API ficará disponível em:

```text
http://localhost:8080
```

Swagger UI (dev):

```text
http://localhost:8080/swagger
```

---

## 2. Execução com Docker

A aplicação está preparada para rodar em contêiner Docker em modo JVM com Java 21.

> **Importante sobre o banco de dados**
>
> O SQLite é criado **dentro do contêiner** e a configuração atual do Hibernate está como:
>
> ```properties
> quarkus.hibernate-orm.database.generation=drop-and-create
> quarkus.hibernate-orm.sql-load-script=import.sql
> ```
>
> Ou seja:
> - a cada nova subida de contêiner, o schema é recriado;
> - o banco começa sempre apenas com os dados do `import.sql`;
> - qualquer dado gravado em runtime é perdido ao parar/remover o contêiner.

### 2.1. Pré-requisitos

- Docker instalado e em execução
- Maven + Java 21

### 2.2. Gerar o pacote da aplicação

Na raiz do projeto:

```bash
mvn clean package -DskipTests
```

### 2.3. Build da imagem Docker

```bash
docker build -t simulaicaixa:latest .
```

### 2.4. Subir o contêiner

```bash
docker run --rm -p 8080:8080 --name simulaicaixa simulaicaixa:latest
```

- API disponível em: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger`

O parâmetro `--rm` remove o contêiner ao parar.  
Para parar manualmente:

```bash
docker stop simulaicaixa
```

Lembrando: cada nova execução recria o banco e recarrega somente os dados do `import.sql`.

---

## 3. Autenticação e usuários de teste

A aplicação utiliza **JWT** (SmallRye JWT). Os usuários abaixo já estão cadastrados na tabela `usuario_autenticacao` e podem ser usados para testes.

> Senha para todos: **123456**

| CPF        |    Senha | Grupos        |    cliente_id | Observação                         |
|-----------:|---------:|---------------|--------------:|------------------------------------|
| 12345678901| senha123 | cliente,admin |           123 | Usuário com acesso admin+cliente   |
| 12345678902| senha123 | cliente       |           124 | Cliente conservador                |
| 12345678903| senha123 | cliente       |           125 | Cliente agressivo                  |
| 99999999999| senha123 | admin         |        *null* | Usuário apenas admin               |

A senha é armazenada em hash **BCrypt**:

### 3.1. Login e obtenção do token

**Requisição**

```http
POST /auth/login
Content-Type: application/json

{
  "cpf": "12345678901",
  "senha": "123456"
}
```

**Resposta (exemplo)**

```json
{
  "token": "<jwt-aqui>",
  "tipoToken": "Bearer",
  "expiraEmSegundos": 3600
}
```

**Uso nas chamadas autenticadas**

```http
Authorization: Bearer <jwt-aqui>
```

---

## 4. Lógica da autenticação e acesso

- O **clienteId** **não é confiado** quando vem no payload:
    - Para usuários com papel **cliente**, o `clienteId` do corpo é ignorado.
        - O sistema usa sempre o `clienteId` obtido a partir do JWT.
    - Para usuários com papel **admin**, o `clienteId` enviado na requisição é respeitado.
    - 
---

## 5. Dados/massa de testes

O projeto usa **SQLite** e, a cada inicialização, carrega dados de exemplo via `import.sql`.

### 5.1. Produtos de investimento (`produto_investimento`)

```sql
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
```

### 5.2. Perfil de risco por cliente (`perfil_risco`)

```sql
-- perfil: 1=CONSERVADOR, 2=MODERADO, 3=AGRESSIVO

INSERT INTO perfil_risco (cliente_id, perfil, pontuacao, descricao) VALUES
(123, 2, 65, 'Perfil equilibrado entre segurança e rentabilidade.'),
(124, 1, 30, 'Perfil com foco em segurança e baixa volatilidade.'),
(125, 3, 85, 'Perfil com apetite a risco em busca de maior rentabilidade.'),
(126, 2, 55, 'Perfil moderado com leve inclinação à rentabilidade.');
```

### 5.3. Histórico de investimentos (`investimento`)

```sql
-- tipo: 1=CDB, 2=FUNDO, 3=LCI, 4=LCA, 5=TESOURO
-- Cliente 123: moderado, mix de produtos
INSERT INTO investimento (id, cliente_id, tipo, valor, rentabilidade, data) VALUES
(1,  123, 1,  5000.00, 0.1200, '2025-01-15 00:00:00.000'),
(2,  123, 2,  3000.00, 0.0800, '2025-03-10 00:00:00.000'),
(3,  123, 1,  7000.00, 0.1100, '2025-05-05 00:00:00.000'),
(4,  123, 5,  2000.00, 0.0900, '2025-06-20 00:00:00.000'),
(5,  123, 2,  4000.00, 0.1500, '2025-08-01 00:00:00.000');

-- Cliente 124: conservador
INSERT INTO investimento (id, cliente_id, tipo, valor, rentabilidade, data) VALUES
(6,  124, 1,  8000.00, 0.0900, '2025-02-01 00:00:00.000'),
(7,  124, 5,  4000.00, 0.0850, '2025-04-15 00:00:00.000');

-- Cliente 125: agressivo, foco em fundos
INSERT INTO investimento (id, cliente_id, tipo, valor, rentabilidade, data) VALUES
(8,  125, 2,  4000.00, 0.1700, '2025-01-20 00:00:00.000'),
(9,  125, 2,  6000.00, 0.1850, '2025-02-10 00:00:00.000'),
(10, 125, 2,  5000.00, 0.1600, '2025-03-05 00:00:00.000'),
(11, 125, 4,  3000.00, 0.1400, '2025-04-12 00:00:00.000'),
(12, 125, 2,  7000.00, 0.1900, '2025-06-01 00:00:00.000');

-- Cliente 126: moderado, histórico recente e diversificado
INSERT INTO investimento (id, cliente_id, tipo, valor, rentabilidade, data) VALUES
(13, 126, 1,  3000.00, 0.1000, '2025-03-01 00:00:00.000'),
(14, 126, 3,  3500.00, 0.0950, '2025-04-01 00:00:00.000'),
(15, 126, 2,  2500.00, 0.1300, '2025-05-15 00:00:00.000');
```

Esses dados foram pensados para gerar cenários distintos de recomendação:
- conservador x moderado x agressivo
- mix de tipos de investimento
- volumes e frequências diferentes.

---

## 6. Endpoints principais 

> A documentação detalhada está disponível via **Swagger UI** em `/swagger`.

---

## 7. Motor de recomendação (funcionametno)

O serviço do motor de recomendação calcula um **score** para cada produto considerando:

1. **Compatibilidade de perfil**
    - Cliente x produto (conservador/moderado/agressivo).

2. **Nível de risco do produto**
    - Ajustes diferentes para clientes conservadores, moderados e agressivos.

3. **Rentabilidade estimada**
    - Faixas de rentabilidade impactam o score.

4. **Histórico do cliente** (quando disponível)
    - Tipos de produto mais usados.
    - Volume total investido.
    - Frequência de operações.

Foi construido pensando em ser simples e leve, porém **legível** e fácil de evoluir.

---

## 8. Tratamento de erros

- Exceções personalizadas tratadas em um corpo de mensagem padrão para toda a aplicaçao.

## 9. Testes automatizados

Cobertura total: **~85%**, concentrada em regras de negócio (service/core/api).

---

## 10. Documentação da API (Swagger/OpenAPI/Javadoc)

- **Swagger UI** (dev):  
  `http://localhost:8080/swagger`

- **OpenAPI (JSON/YAML)**:  
  `http://localhost:8080/q/openapi`

- **Javadoc** (código Java):  
  Gerar com:

  ```bash
  mvn javadoc:javadoc
  ```

  Acessar em:

  ```text
  target/site/apidocs/index.html
  ```
---