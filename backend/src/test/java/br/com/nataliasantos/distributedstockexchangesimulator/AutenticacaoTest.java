package br.com.nataliasantos.distributedstockexchangesimulator;

import br.com.nataliasantos.distributedstockexchangesimulator.domain.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:auth;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=validate"
})
class AutenticacaoTest {
    @LocalServerPort
    int port;
    @Autowired
    UsuarioRepository usuarios;
    @Autowired
    PasswordEncoder encoder;
    private final JsonMapper json = new JsonMapper();

    @Test
    void cadastroLoginESessaoIdentificamOMesmoUsuario() throws Exception {
        try (var client = cliente()) {
            var csrf = dados(enviar(client, "GET", "/csrf", null, null));
            var email = UUID.randomUUID() + "@example.com";
            var cadastro = enviar(client, "POST", "/cadastro", Map.of(
                    "nome", "Maria Silva", "email", email.toUpperCase(), "senha", "Senha123!"), csrf);
            assertEquals(201, cadastro.statusCode());
            var usuario = dados(cadastro);
            assertFalse(usuario.has("senha"));
            assertFalse(usuario.has("senhaHash"));
            assertEquals(email, usuario.get("email").asString());
            var persistido = usuarios.findByEmail(email).orElseThrow();
            assertTrue(encoder.matches("Senha123!", persistido.getSenhaHash()));
            assertNotNull(persistido.getCarteira().getId());

            var sessaoAnterior = client.cookieHandler().orElseThrow().get(
                    URI.create("http://localhost:" + port), Map.of()).toString();
            var login = enviar(client, "POST", "/login", Map.of("email", email, "senha", "Senha123!"), csrf);
            assertEquals(200, login.statusCode());
            var sessao = dados(login);
            assertFalse(sessao.get("idSessao").asString().isBlank());
            assertFalse(sessaoAnterior.contains(sessao.get("idSessao").asString()));
            assertEquals(usuario.get("id"), sessao.get("usuario").get("id"));
            var me = enviar(client, "GET", "/me", null, null);
            assertEquals(200, me.statusCode());
            assertEquals(usuario, dados(me));

            try (var outroCliente = cliente()) {
                assertEquals(401, enviar(outroCliente, "GET", "/me", null, null).statusCode());
            }
            // Uma sessão invalidada não pode voltar a identificar o usuário.
            var cookie = ((CookieManager) client.cookieHandler().orElseThrow()).getCookieStore()
                    .getCookies().stream().filter(c -> c.getName().equals("JSESSIONID")).findFirst().orElseThrow();
            cookie.setValue("sessao-inexistente");
            assertEquals(401, enviar(client, "GET", "/me", null, null).statusCode());
        }
    }

    @Test
    void rejeitaDuplicidadeDadosInvalidosECredenciaisIncorretas() throws Exception {
        try (var client = cliente()) {
            var csrf = dados(enviar(client, "GET", "/csrf", null, null));
            var email = UUID.randomUUID() + "@example.com";
            var cadastro = Map.of("nome", "Maria Silva", "email", email, "senha", "Senha123!");
            assertEquals(201, enviar(client, "POST", "/cadastro", cadastro, csrf).statusCode());
            assertEquals(409, enviar(client, "POST", "/cadastro", Map.of(
                    "nome", "Outra Maria", "email", email.toUpperCase(), "senha", "Senha123!"), csrf).statusCode());
            assertEquals(400, enviar(client, "POST", "/cadastro", Map.of(
                    "nome", "M", "email", "invalido", "senha", "123"), csrf).statusCode());
            assertEquals(400, enviar(client, "POST", "/cadastro", Map.of(
                    "nome", "Maria Silva", "email", "outra@example.com", "senha", "á".repeat(40)), csrf).statusCode());
            assertEquals(401, enviar(client, "POST", "/login", Map.of(
                    "email", email, "senha", "SenhaErrada"), csrf).statusCode());
            assertEquals(401, enviar(client, "POST", "/login", Map.of(
                    "email", "ausente@example.com", "senha", "SenhaErrada"), csrf).statusCode());
            assertEquals(401, enviar(client, "GET", "/me", null, null).statusCode());
            assertEquals(403, enviar(client, "POST", "/login", Map.of(
                    "email", email, "senha", "Senha123!"), null).statusCode());
        }
    }

    @Test
    void errosRetornamMensagensAmigaveisSemDetalhesInternos() throws Exception {
        try (var client = cliente()) {
            var csrf = dados(enviar(client, "GET", "/csrf", null, null));
            var email = UUID.randomUUID() + "@example.com";
            var cadastro = Map.of("nome", "Maria Silva", "email", email, "senha", "Senha123!");
            assertEquals(201, enviar(client, "POST", "/cadastro", cadastro, csrf).statusCode());
            assertErro(enviar(client, "POST", "/cadastro", cadastro, csrf), 409, "Email já cadastrado");
            assertErro(enviar(client, "POST", "/login", Map.of(
                    "email", email, "senha", "SenhaErrada"), csrf), 401, "Credenciais inválidas");
            assertErro(enviar(client, "GET", "/me", null, null), 401,
                    "Autenticação necessária. Faça login para continuar.");
            assertErro(enviar(client, "POST", "/login", cadastro, null), 403,
                    "Acesso negado. Verifique suas permissões e o token CSRF.");
            var validacao = enviar(client, "POST", "/cadastro", Map.of(
                    "nome", "M", "email", "invalido", "senha", "123"), csrf);
            assertErro(validacao, 400, "Dados inválidos");
            assertTrue(dados(validacao).get("erros").has("senha"));
            assertFalse(validacao.body().contains("123"));

            var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/auth/login"))
                    .header("Content-Type", "application/json")
                    .header(csrf.get("headerName").asString(), csrf.get("token").asString())
                    .POST(HttpRequest.BodyPublishers.ofString("{invalido"))
                    .build();
            assertErro(client.send(request, HttpResponse.BodyHandlers.ofString()), 400,
                    "Corpo da requisição ausente ou JSON inválido");
        }
    }

    private void assertErro(HttpResponse<String> response, int status, String detail) {
        assertEquals(status, response.statusCode());
        assertTrue(response.headers().firstValue("Content-Type").orElse("").contains("application/json"));
        var body = json.readTree(response.body());
        assertEquals(status, body.get("cdRetorno").asInt());
        assertEquals(detail, body.get("msgUsuario").asString());
        assertTrue(body.get("msgTecnica").isNull());
        assertFalse(body.has("trace"));
        assertFalse(body.has("exception"));
        assertFalse(response.body().contains("org.springframework"));
        assertFalse(response.body().contains("br.com.nataliasantos"));
    }

    private HttpClient cliente() {
        return HttpClient.newBuilder().cookieHandler(new CookieManager()).build();
    }

    private JsonNode dados(HttpResponse<String> response) {
        var body = json.readTree(response.body());
        assertEquals(response.statusCode(), body.get("cdRetorno").asInt());
        assertTrue(body.has("msgUsuario"));
        assertTrue(body.has("msgTecnica"));
        assertTrue(body.has("dados"));
        return body.get("dados");
    }

    private HttpResponse<String> enviar(HttpClient client, String method, String path,
                                       Map<String, String> body, JsonNode csrf) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/auth" + path));
        if (csrf != null) builder.header(csrf.get("headerName").asString(), csrf.get("token").asString());
        if (body == null) builder.GET();
        else builder.header("Content-Type", "application/json")
                .method(method, HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body)));
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }
}
