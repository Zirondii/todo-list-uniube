package br.com.mvc.service;

import br.com.mvc.dao.ApiDAO;
import br.com.mvc.model.ListaTarefas;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.*;

public class TarefaService {
    private final ApiDAO api;
    private final ObjectMapper json = new ObjectMapper();

    public TarefaService(ApiDAO api) {
        this.api = api;
    }

    public HttpResponse<byte[]> consultar(String metodo, String caminho, String query,
            Map<String, String> headers, byte[] corpo) throws IOException, InterruptedException {
        return api.enviar(metodo, caminho, query, headers, corpo);
    }

    public byte[] resumirLista(byte[] resposta) throws IOException {
        List<JsonNode> tarefas = json.readValue(resposta, new TypeReference<List<JsonNode>>() {});
        long pendentes = tarefas.stream().filter(t -> !t.path("concluida").asBoolean()).count();
        return json.writeValueAsBytes(new ListaTarefas(tarefas, tarefas.size(), pendentes));
    }
}
