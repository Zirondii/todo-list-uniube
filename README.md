# Lista de Tarefas — Uniube

Trabalho de Aplicações para Internet. O navegador carrega as telas JSP e envia JSON ao proxy do front. O proxy encaminha a chamada HTTP à API. A API identifica o usuário pelo JWT, aplica as regras na camada Service e consulta o MySQL pelos DAOs.

## Executar no PowerShell

Na pasta do projeto, prepare a configuração no primeiro uso:

```powershell
Copy-Item .env.example .env
Add-Content .env ("JWT_SECRET=" + [guid]::NewGuid().ToString("N") + [guid]::NewGuid().ToString("N"))
```

Se já usa um volume MySQL, configure `MYSQL_ROOT_PASSWORD` com a senha desse volume. Na configuração original, ela é `root`.

```powershell
docker compose up --build -d
```

Abra http://localhost:8080. Em um banco inicializado com o SQL de demonstração, entre com `admin` / `admin` para ver três tarefas, ou crie uma conta. A lista mostra o total e as pendências. Cada usuário pode criar, editar, concluir, reabrir e excluir suas próprias tarefas.

Para encerrar preservando os dados, execute `docker compose down`. O serviço MySQL continua usando o volume `mysql_data` e o mesmo esquema da primeira versão.

## Organização

- `tarefas/` — telas JSP, CSS, JavaScript e proxy HTTP. A variável `API_URL` aponta para o servidor da API.
- `api/` — endpoints JSON, autenticação, serviços, modelos e DAOs. Usa `DB_URL`, `DB_USER`, `DB_PASSWORD` e `JWT_SECRET`.
- `init.sql` — tabelas e dados de demonstração.
- `postman/` — coleção para testar a API diretamente em http://localhost:8081.

Cada módulo gera seu próprio WAR para Tomcat 10.1 e Java 17. Para compilar ambos, execute `mvn package` na raiz.

## API

| Método | Caminho | Operação |
| --- | --- | --- |
| POST | /api/auth/cadastro | Criar conta com login, senha e confirmacaoSenha |
| POST | /api/auth/login | Autenticar com login e senha e obter token |
| GET | /api/tarefas | Listar as tarefas da conta |
| POST | /api/tarefas | Criar tarefa com titulo, descricao e concluida |
| GET | /api/tarefas/{id} | Consultar tarefa |
| PUT | /api/tarefas/{id} | Salvar edição completa |
| PATCH | /api/tarefas/{id} | Definir concluida como true ou false |
| DELETE | /api/tarefas/{id} | Excluir tarefa |

Envie corpos com `Content-Type: application/json`. As rotas de tarefas exigem `Authorization: Bearer <token>`. O token expira em 30 minutos. A interface guarda o token na aba e o remove ao sair. Novos cadastros usam hash PBKDF2. Contas da versão anterior são atualizadas no primeiro login válido.

Na coleção do Postman, preencha as variáveis login e senha e execute **Entrar**. A coleção salva o token. Execute **Listar**, copie o id de uma tarefa sua para a variável id e teste a consulta e as alterações.
