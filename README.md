# Lista de Tarefas (ToDo List)

Aplicação web desenvolvida em Java seguindo o padrão **MVC**, na variação
**Controller-Service-View** trabalhada em aula, com as camadas **DAO** e **Service**.

Trabalho da disciplina de Aplicações para Internet.

---

## Tecnologias

| Recurso | Versão |
|---|---|
| Java | 17 |
| Servlets / JSP | Jakarta EE 6.0 |
| Tomcat | 10.1 |
| MySQL | 8.0 |
| Maven | 3.9 |
| Docker / Docker Compose | — |

---

## Como executar

O projeto roda inteiramente em Docker. **Não é preciso ter Java, Maven, Tomcat
ou MySQL instalados** — os containers cuidam de tudo, inclusive da compilação.

```bash
docker compose up --build
```

Na primeira execução o Maven baixa as dependências, o que pode levar alguns
minutos. Depois disso, acesse:

```
http://localhost:8080
```

Para encerrar:

```bash
docker compose down
```

Para encerrar apagando também o banco de dados:

```bash
docker compose down -v
```

### Usuários já cadastrados

| Login | Senha |
|---|---|
| `admin` | `admin` |
| `natan` | `123456` |

O usuário `admin` já vem com três tarefas de exemplo. O usuário `natan` começa
com a lista vazia — útil para demonstrar que cada um vê apenas as próprias tarefas.

Também é possível criar uma conta nova pelo link **"Criar agora"** na tela de login.

---

## Funcionalidades

- Cadastro de usuário, login e logout com controle de sessão
- **CRUD completo de tarefas**
  - **Create** — cadastrar nova tarefa
  - **Read** — listar as tarefas do usuário logado
  - **Update** — editar uma tarefa e marcar/desmarcar como concluída
  - **Delete** — excluir uma tarefa (com confirmação)
- Cada usuário acessa somente as suas próprias tarefas
- Validação dos dados na camada de serviço

---

## Estrutura do projeto

```
todo-list/
├── docker-compose.yml          Sobe o MySQL e a aplicação
├── Dockerfile                  Compila o projeto e gera a imagem do Tomcat
├── init.sql                    Cria as tabelas e insere os dados iniciais
├── pom.xml                     Dependências do Maven
└── src/main/
    ├── java/br/com/mvc/
    │   ├── config/
    │   │   └── MysqlSingleton.java     Conexão única com o banco (Singleton)
    │   ├── model/
    │   │   ├── Mapeavel.java           Contrato: "sei me preencher do banco"
    │   │   ├── Usuario.java
    │   │   └── Tarefa.java
    │   ├── dao/
    │   │   ├── MysqlDAO.java           Classe base + mapeamento genérico
    │   │   ├── UsuarioDAO.java         SQL do usuário
    │   │   └── TarefaDAO.java          SQL do CRUD de tarefas
    │   ├── service/
    │   │   ├── UsuarioService.java     Regras de autenticação
    │   │   └── TarefaService.java      Regras e validações das tarefas
    │   └── controller/
    │       ├── LoginController.java     Servlet de entrada e saída
    │       ├── CadastroController.java  Servlet de cadastro de usuário
    │       └── TarefaController.java    Servlet do CRUD
    └── webapp/
        ├── index.jsp
        ├── login.jsp
        ├── cadastro.jsp
        ├── css/style.css
        ├── tarefa/lista.jsp            Listagem
        ├── tarefa/form.jsp             Formulário (cadastro e edição)
        └── WEB-INF/web.xml
```

---

## Como o MVC foi aplicado

Cada camada tem uma responsabilidade única e não invade a das outras.

| Camada | Responsabilidade | O que **não** faz |
|---|---|---|
| **Controller** (Servlet) | Recebe a requisição, chama o Service e escolhe a View | Não acessa o banco, não escreve HTML |
| **Service** | Regras de negócio e validações | Não sabe como os dados serão exibidos |
| **DAO** | Somente o SQL | Não contém regra de negócio |
| **View** (JSP) | Exibe os dados recebidos | Não busca dados, não tem lógica complexa |

### Fluxo de uma requisição

Exemplo: o usuário acessa a lista de tarefas.

1. O navegador chama `/tarefas`
2. O `TarefaController` recebe a requisição
3. O Controller pede os dados: `tarefaService.listar(usuarioId)`
4. O `TarefaService` valida e repassa ao `TarefaDAO`
5. O DAO executa o `SELECT` e devolve uma `List<Tarefa>`
6. O Controller coloca a lista na requisição e encaminha para a View
7. A `lista.jsp` percorre a lista com `<c:forEach>` e monta o HTML

### Rotas

| URL | Ação |
|---|---|
| `/login` | Tela de login (GET) e autenticação (POST) |
| `/cadastro` | Tela de cadastro (GET) e gravação do usuário (POST) |
| `/logout` | Encerra a sessão |
| `/tarefas` | Lista as tarefas |
| `/tarefas?acao=novo` | Formulário de cadastro |
| `/tarefas?acao=editar&id=1` | Formulário de edição |
| `/tarefas?acao=deletar&id=1` | Exclui a tarefa |
| `/tarefas?acao=concluir&id=1` | Marca ou desmarca como concluída |

Após salvar ou excluir, o Controller usa `sendRedirect` em vez de `forward`.
Isso evita que a ação seja repetida caso o usuário atualize a página (F5).

---

## O padrão `Mapeavel`

Originalmente cada DAO tinha um método `mapear()` que copiava os dados do
`ResultSet` para o objeto — código repetido a cada nova entidade.

Para eliminar essa repetição, foi criada a interface `Mapeavel`:

```java
public interface Mapeavel {
    void preencher(ResultSet rs) throws SQLException;
}
```

Agora quem sabe preencher os próprios dados é a própria classe de modelo:

```java
// Em Tarefa.java
@Override
public void preencher(ResultSet rs) throws SQLException {
    this.id = rs.getLong("id");
    this.titulo = rs.getString("titulo");
    this.concluida = rs.getBoolean("concluida");
    // ...
}
```

E o `MysqlDAO` ganhou um método genérico que serve para **qualquer** classe
que implemente a interface:

```java
protected <T extends Mapeavel> T mapearSimples(ResultSet rs, Class<T> classe) {
    T objeto = classe.getDeclaredConstructor().newInstance();
    objeto.preencher(rs);
    return objeto;
}
```

Resultado: nenhum DAO precisa mais do método `mapear()`. Para criar uma nova
entidade, basta implementar `Mapeavel` — o DAO já sabe convertê-la.

---

## Banco de dados

```sql
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
```

O arquivo `init.sql` é executado automaticamente pelo Docker na primeira vez
que o banco sobe.

Para acessar o banco pelo terminal:

```bash
docker exec -it todolist-mysql mysql -uroot -proot todolist
```

O MySQL também fica disponível na porta **3307** da máquina, para não conflitar
com uma instalação local já existente.

---

## Limitações conhecidas

**As senhas são armazenadas em texto puro.** O login compara a senha digitada
diretamente com o valor gravado no banco:

```sql
SELECT id, login, senha FROM usuario WHERE login = ? AND senha = ?
```

Em uma aplicação real isso seria inadequado: qualquer pessoa com acesso ao
banco leria todas as senhas. O correto seria gravar apenas um *hash* da senha
(com BCrypt, por exemplo) e comparar os hashes na autenticação.

A forma atual foi mantida por fidelidade ao material da disciplina, onde o
foco é a organização em camadas do padrão MVC.

Isso vale também para a tela de cadastro: a senha digitada é gravada como veio.

Outros pontos que evoluiriam em um sistema de produção:

- A conexão é única e compartilhada; o ideal seria um *pool* de conexões
- As credenciais do banco estão no `docker-compose.yml`, apropriado apenas
  para ambiente de desenvolvimento
- O cadastro é aberto e não exige confirmação por e-mail
