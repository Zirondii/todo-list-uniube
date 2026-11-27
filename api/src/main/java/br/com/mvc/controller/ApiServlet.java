package br.com.mvc.controller;

import br.com.mvc.model.*;
import br.com.mvc.view.RespostaJson;
import br.com.mvc.security.JwtService;
import br.com.mvc.service.*;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Map;

@WebServlet(value = "/api/*", loadOnStartup = 1)
public class ApiServlet extends HttpServlet {
    private final ObjectMapper json = new ObjectMapper();
    private final RespostaJson resposta = new RespostaJson();
    private final TarefaService tarefas = new TarefaService();
    private final UsuarioService usuarios = new UsuarioService();
    private JwtService tokens;

    @Override public void init() {
        tokens = new JwtService(System.getenv("JWT_SECRET"));
    }

    @Override protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setHeader("Cache-Control", "no-store");
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();
        try {
            if (path.equals("/auth/login") || path.equals("/auth/cadastro")) {
                if (!req.getMethod().equals("POST")) {
                    resposta.metodo(res, "POST");
                    return;
                }
                JsonNode dados = corpo(req);
                String login = texto(dados, "login");
                String senha = texto(dados, "senha");
                if (path.endsWith("/cadastro")) {
                    usuarios.cadastrar(login, senha, texto(dados, "confirmacaoSenha"));
                    res.setStatus(201);
                } else {
                    Usuario usuario = usuarios.autenticar(login, senha);
                    if (usuario == null) resposta.erro(res, 401, "Login ou senha inválidos.");
                    else resposta.enviar(res, Map.of("token", tokens.emitir(usuario.getId()), "login", usuario.getLogin()));
                }
                return;
            }
            String auth = req.getHeader("Authorization");
            if (auth == null || !auth.startsWith("Bearer ")) {
                resposta.erro(res, 401, "Faça login para acessar suas tarefas.");
                return;
            }
            long usuarioId = Long.parseLong(tokens.verificar(auth.substring(7)).getSubject());
            if (path.equals("/tarefas")) {
                switch (req.getMethod()) {
                    case "GET" -> resposta.enviar(res, tarefas.listar(usuarioId));
                    case "POST" -> {
                        tarefas.inserir(tarefa(req, null, usuarioId));
                        res.setStatus(201);
                    }
                    default -> resposta.metodo(res, "GET, POST");
                }
            } else if (path.matches("/tarefas/[1-9][0-9]*")) {
                long id = Long.parseLong(path.substring("/tarefas/".length()));
                Tarefa atual = tarefas.buscarDoUsuario(id, usuarioId);
                if (atual == null) {
                    resposta.erro(res, 404, "Tarefa não encontrada.");
                    return;
                }
                switch (req.getMethod()) {
                    case "GET" -> resposta.enviar(res, atual);
                    case "PUT" -> {
                        tarefas.alterar(tarefa(req, id, usuarioId), usuarioId);
                        res.setStatus(204);
                    }
                    case "PATCH" -> {
                        JsonNode estado = corpo(req).path("concluida");
                        if (!estado.isBoolean()) throw new IllegalArgumentException("Informe concluida como booleano.");
                        tarefas.definirConclusao(id, usuarioId, estado.booleanValue());
                        res.setStatus(204);
                    }
                    case "DELETE" -> {
                        tarefas.deletar(id, usuarioId);
                        res.setStatus(204);
                    }
                    default -> resposta.metodo(res, "GET, PUT, PATCH, DELETE");
                }
            } else resposta.erro(res, 404, "Recurso não encontrado.");
        } catch (JWTVerificationException e) {
            resposta.erro(res, 401, "Sessão inválida ou expirada.");
        } catch (TipoInvalido e) {
            resposta.erro(res, 415, "Use Content-Type application/json.");
        } catch (JsonProcessingException e) {
            resposta.erro(res, 400, "JSON inválido.");
        } catch (IllegalArgumentException e) {
            resposta.erro(res, 400, e.getMessage());
        } catch (RuntimeException e) {
            log("Falha ao processar a requisição", e);
            resposta.erro(res, 500, "Não foi possível completar a operação.");
        }
    }

    private static class TipoInvalido extends RuntimeException {}
    private JsonNode corpo(HttpServletRequest req) throws IOException {
        String tipo = req.getContentType();
        if (tipo == null || !tipo.split(";", 2)[0].trim().equalsIgnoreCase("application/json")) throw new TipoInvalido();
        JsonNode dados = json.readTree(req.getInputStream());
        if (dados == null || !dados.isObject()) throw new IllegalArgumentException("Informe um objeto JSON.");
        return dados;
    }
    private String texto(JsonNode dados, String campo) {
        return dados.path(campo).textValue();
    }
    private Tarefa tarefa(HttpServletRequest req, Long id, long usuarioId) throws IOException {
        JsonNode dados = corpo(req);
        if (dados.has("concluida") && !dados.get("concluida").isBoolean())
            throw new IllegalArgumentException("Informe concluida como booleano.");
        return new Tarefa(id, usuarioId, texto(dados, "titulo"), texto(dados, "descricao"), dados.path("concluida").asBoolean(false));
    }
}
