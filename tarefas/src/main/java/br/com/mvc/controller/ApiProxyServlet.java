package br.com.mvc.controller;

import java.io.IOException;
import br.com.mvc.dao.ApiDAO;
import br.com.mvc.service.TarefaService;
import java.util.HashMap;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

/** Encaminha as chamadas das telas para a API, sem acessar o banco. */
@WebServlet(urlPatterns = {"/api/*", "/dados/lista"}, loadOnStartup = 1)
public class ApiProxyServlet extends HttpServlet {
  private TarefaService service;

  @Override
  public void init() throws ServletException {
    String apiUrl = getInitParameter("apiUrl");
    if (apiUrl == null) apiUrl = System.getenv("API_URL");
    if (apiUrl == null || apiUrl.isBlank()) apiUrl = "http://localhost:8081";
    service = new TarefaService(new ApiDAO(apiUrl.replaceAll("/+$", "")));
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
    // Usa a URI original para preservar os caracteres escapados do caminho.
    String path = req.getRequestURI().substring(req.getContextPath().length());
    String query = req.getQueryString();
    var headers = new HashMap<String, String>();
    for (String header : new String[] {"Content-Type", "Accept", "Authorization"}) {
      String value = req.getHeader(header);
      if (value != null) headers.put(header, value);
    }
    byte[] corpo = req.getInputStream().readNBytes(262145);
    if (corpo.length > 262144) {
      res.setStatus(413);
      res.setContentType("application/json;charset=UTF-8");
      res.getWriter().write("{\"status\":413,\"erro\":\"Requisição muito grande.\"}");
      return;
    }
    res.setHeader("Cache-Control", "no-store");
    try {
      boolean lista = "/dados/lista".equals(req.getServletPath());
      if (lista && !"GET".equals(req.getMethod())) {
        res.setHeader("Allow", "GET");
        res.setStatus(405);
        return;
      }
      var upstream = service.consultar(req.getMethod(), lista ? "/api/tarefas" : path, query, headers, corpo);
      res.setStatus(upstream.statusCode());
      for (String header : new String[] {"Content-Type", "Allow", "Location", "WWW-Authenticate"}) {
        upstream
            .headers()
            .firstValue(header)
            .ifPresent(value ->
                res.setHeader(
                    header,
                    header.equals("Location") && value.startsWith("/api/")
                        ? req.getContextPath() + value
                        : value));
      }
      if (!req.getMethod().equals("HEAD") && upstream.statusCode() != 204) {
        byte[] resposta = lista && upstream.statusCode() == 200
            ? service.resumirLista(upstream.body()) : upstream.body();
        res.getOutputStream().write(resposta);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      unavailable(res);
    } catch (IOException e) {
      log("Não foi possível acessar a API", e);
      unavailable(res);
    }
  }

  private void unavailable(HttpServletResponse res) throws IOException {
    res.setStatus(502);
    res.setContentType("application/json;charset=UTF-8");
    res.getWriter()
        .write("{\"erro\":\"API indisponível. Tente novamente mais tarde.\",\"status\":502}");
  }
}
