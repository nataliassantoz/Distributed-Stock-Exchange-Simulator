package br.com.nataliasantos.distributedstockexchangesimulator;

import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.session.impl.SessionServiceImpl;
import br.com.nataliasantos.distributedstockexchangesimulator.utils.HttpTestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.JsonNode;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:sessions;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=validate"
})
class SessionTest {
    @LocalServerPort
    int port;
    @Autowired
    SessionServiceImpl service;

    @Test
    void doisUsuariosPermanecemAutenticadosELogoutEncerraSomenteUmaSessao() throws Exception {
        try (var primeiro = new HttpTestUtil(port); var segundo = new HttpTestUtil(port)) {
            var loginUm = CompletableFuture.supplyAsync(() -> cadastrarEEntrar(primeiro));
            var loginDois = CompletableFuture.supplyAsync(() -> cadastrarEEntrar(segundo));
            var um = loginUm.get();
            var dois = loginDois.get();
            assertNotEquals(um.get("idSessao"), dois.get("idSessao"));
            assertSessao(primeiro, um);
            assertSessao(segundo, dois);

            var csrf = primeiro.dados(primeiro.enviar("GET", "/api/auth/csrf", null, null));
            assertEquals(403, primeiro.enviar("POST", "/api/auth/logout", null, null).statusCode());
            assertSessao(primeiro, um);
            var logout = primeiro.enviar("POST", "/api/auth/logout", null, csrf);
            assertEquals(200, logout.statusCode());
            assertTrue(logout.body().contains("\"cdRetorno\":200"));
            assertTrue(service.listar(um.get("usuario").get("id").asLong()).isEmpty());
            assertEquals(401, primeiro.enviar("GET", "/api/auth/me", null, null).statusCode());
            assertEquals(401, primeiro.enviar("GET", "/api/sessoes/atual", null, null).statusCode());
            assertSessao(segundo, dois);
        }
    }

    @Test
    void novoLoginRemoveIdentificadorAntigoESessoesAnonimasNaoSaoRegistradas() throws Exception {
        try (var client = new HttpTestUtil(port)) {
            assertEquals(401, client.enviar("GET", "/api/sessoes", null, null).statusCode());
            var email = UUID.randomUUID() + "@example.com";
            var csrf = client.dados(client.enviar("GET", "/api/auth/csrf", null, null));
            assertEquals(401, client.enviar("POST", "/api/auth/login", Map.of(
                    "email", email, "senha", "Senha123!"), csrf).statusCode());
            assertEquals(201, client.enviar("POST", "/api/auth/cadastro", Map.of(
                    "nome", "Maria Silva", "email", email, "senha", "Senha123!"), csrf).statusCode());
            var credenciais = Map.of("email", email, "senha", "Senha123!");
            var anterior = client.dados(client.enviar("POST", "/api/auth/login", credenciais, csrf));
            var atual = client.dados(client.enviar("POST", "/api/auth/login", credenciais, csrf));
            assertNotEquals(anterior.get("idSessao"), atual.get("idSessao"));
            assertThrows(org.springframework.web.server.ResponseStatusException.class, () ->
                    service.consultar(anterior.get("idSessao").asString(), atual.get("usuario").get("id").asLong()));
            assertSessao(client, atual);
        }
    }

    private JsonNode cadastrarEEntrar(HttpTestUtil client) {
        try {
            var csrf = client.dados(client.enviar("GET", "/api/auth/csrf", null, null));
            var email = UUID.randomUUID() + "@example.com";
            assertEquals(201, client.enviar("POST", "/api/auth/cadastro", Map.of(
                    "nome", "Maria Silva", "email", email, "senha", "Senha123!"), csrf).statusCode());
            var login = client.enviar("POST", "/api/auth/login", Map.of("email", email, "senha", "Senha123!"), csrf);
            assertEquals(200, login.statusCode());
            return client.dados(login);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    private void assertSessao(HttpTestUtil client, JsonNode login) throws Exception {
        var response = client.enviar("GET", "/api/sessoes/atual", null, null);
        assertEquals(200, response.statusCode());
        var session = client.dados(response);
        assertEquals(login.get("idSessao"), session.get("idSessao"));
        assertEquals(login.get("usuario").get("id"), session.get("idUsuario"));
        assertEquals("ATIVA", session.get("status").asString());
        assertNotNull(java.time.Instant.parse(session.get("dataHoraConexao").asString()));
        var lista = client.enviar("GET", "/api/sessoes", null, null);
        assertEquals(200, lista.statusCode());
        assertEquals(1, client.dados(lista).size());
        assertEquals(session, client.dados(lista).get(0));
        assertEquals(200, client.enviar("GET", "/api/auth/me", null, null).statusCode());
    }
}
