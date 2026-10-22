package br.com.nataliasantos.distributedstockexchangesimulator.utils;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class HttpTestUtil implements AutoCloseable {
    private final HttpClient client = HttpClient.newBuilder().cookieHandler(new CookieManager()).build();
    private final JsonMapper json = new JsonMapper();
    private final String baseUrl;

    public HttpTestUtil(int port) {
        baseUrl = "http://localhost:" + port;
    }

    public HttpResponse<String> enviar(String metodo, String caminho, Object corpo, JsonNode csrf) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create(baseUrl + caminho));
        if (csrf != null) builder.header(csrf.get("headerName").asString(), csrf.get("token").asString());
        builder.header("Content-Type", "application/json").method(metodo, corpo == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(json.writeValueAsString(corpo)));
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    public JsonNode dados(HttpResponse<String> response) {
        return json.readTree(response.body()).get("dados");
    }

    @Override
    public void close() {
        client.close();
    }
}
