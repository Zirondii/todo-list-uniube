package br.com.mvc.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class RespostaJson {
    private final ObjectMapper json = new ObjectMapper();

    public void enviar(HttpServletResponse res, Object dados) throws IOException {
        json.writeValue(res.getOutputStream(), dados);
    }

    public void metodo(HttpServletResponse res, String permitidos) throws IOException {
        res.setHeader("Allow", permitidos);
        erro(res, 405, "Método não permitido.");
    }
    public void erro(HttpServletResponse res, int status, String mensagem) throws IOException {
        res.setStatus(status);
        json.writeValue(res.getOutputStream(), Map.of("erro", mensagem));
    }
}
