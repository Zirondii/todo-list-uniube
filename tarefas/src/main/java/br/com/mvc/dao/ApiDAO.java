package br.com.mvc.dao;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.Map;

public class ApiDAO {
    private final String url;
    private final HttpClient cliente = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    public ApiDAO(String url) {
        this.url = url;
    }

    public HttpResponse<byte[]> enviar(String metodo, String caminho, String query,
            Map<String, String> headers, byte[] corpo) throws IOException, InterruptedException {
        URI uri = URI.create(url + caminho + (query == null ? "" : "?" + query));
        var pedido = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(15));
        headers.forEach(pedido::header);
        pedido.method(metodo, HttpRequest.BodyPublishers.ofByteArray(corpo));
        return cliente.send(pedido.build(), HttpResponse.BodyHandlers.ofByteArray());
    }
}
