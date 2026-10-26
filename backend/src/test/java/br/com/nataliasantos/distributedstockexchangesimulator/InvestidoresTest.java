package br.com.nataliasantos.distributedstockexchangesimulator;

import br.com.nataliasantos.distributedstockexchangesimulator.utils.HttpTestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:investidores;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=validate"
})
class InvestidoresTest {
    @LocalServerPort
    int port;

    @Test
    void exigeAutenticacao() throws Exception {
        try (var client = new HttpTestUtil(port)) {
            assertEquals(401, client.enviar("GET", "/mercado/investidores", null, null).statusCode());
        }
    }

    @Test
    void listaInvestidoresUnicosERemoveSomenteQuemNaoTemMaisSessoes() throws Exception {
        try (var maria = new HttpTestUtil(port); var joao = new HttpTestUtil(port);
             var outraMaria = new HttpTestUtil(port)) {
            var emailMaria = UUID.randomUUID() + "@example.com";
            var loginMaria = cadastrarEEntrar(maria, "Maria Silva", emailMaria);
            var loginJoao = cadastrarEEntrar(joao, "Joao Silva", UUID.randomUUID() + "@example.com");
            var csrf = outraMaria.dados(outraMaria.enviar("GET", "/api/auth/csrf", null, null));
            assertEquals(200, outraMaria.enviar("POST", "/api/auth/login", Map.of(
                    "email", emailMaria, "senha", "Senha123!"), csrf).statusCode());

            var lista = consultar(joao);
            assertEquals(2, lista.size());
            var investidor = encontrar(lista, loginMaria.get("usuario").get("id").asLong());
            assertEquals("Maria Silva", investidor.get("nome").asString());
            assertEquals(2, investidor.get("quantidadeSessoes").asLong());
            assertNotNull(Instant.parse(investidor.get("dataHoraConexao").asString()));
            assertNull(investidor.get("idSessao"));
            assertNull(investidor.get("email"));
            assertNull(investidor.get("senhaHash"));

            sair(maria);
            assertEquals(1, encontrar(consultar(joao), loginMaria.get("usuario").get("id").asLong())
                    .get("quantidadeSessoes").asLong());
            sair(outraMaria);
            lista = consultar(joao);
            assertEquals(1, lista.size());
            assertEquals(loginJoao.get("usuario").get("id"), lista.get(0).get("idUsuario"));
            sair(joao);
        }
    }

    private JsonNode cadastrarEEntrar(HttpTestUtil client, String nome, String email) throws Exception {
        var csrf = client.dados(client.enviar("GET", "/api/auth/csrf", null, null));
        assertEquals(201, client.enviar("POST", "/api/auth/cadastro", Map.of(
                "nome", nome, "email", email, "senha", "Senha123!"), csrf).statusCode());
        var login = client.enviar("POST", "/api/auth/login", Map.of(
                "email", email, "senha", "Senha123!"), csrf);
        assertEquals(200, login.statusCode());
        return client.dados(login);
    }

    private JsonNode consultar(HttpTestUtil client) throws Exception {
        var response = client.enviar("GET", "/mercado/investidores", null, null);
        assertEquals(200, response.statusCode());
        return client.dados(response);
    }

    private JsonNode encontrar(JsonNode lista, long idUsuario) {
        for (var investidor : lista) {
            if (investidor.get("idUsuario").asLong() == idUsuario) return investidor;
        }
        throw new AssertionError("Investidor conectado não encontrado: " + idUsuario);
    }

    private void sair(HttpTestUtil client) throws Exception {
        var csrf = client.dados(client.enviar("GET", "/api/auth/csrf", null, null));
        assertEquals(200, client.enviar("POST", "/api/auth/logout", null, csrf).statusCode());
    }
}
