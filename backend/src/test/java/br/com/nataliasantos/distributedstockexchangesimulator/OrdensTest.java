package br.com.nataliasantos.distributedstockexchangesimulator;

import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.ativo.Ativo;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.repository.ativo.AtivoRepository;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.repository.ordem.OrdemRepository;
import br.com.nataliasantos.distributedstockexchangesimulator.utils.HttpTestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:ordens;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=validate"
})
class OrdensTest {
    @LocalServerPort int port;
    @Autowired AtivoRepository ativos;
    @Autowired OrdemRepository ordens;

    @Test
    void registraCompraEVendaComInvestidorDaSessao() throws Exception {
        var ativo = criarAtivo();
        try (var client = new HttpTestUtil(port)) {
            long investidor = entrar(client);
            var token = csrf(client);
            for (var tipo : new String[]{"COMPRA", "VENDA"}) {
                var response = client.enviar("POST", "/ordens", Map.of(
                        "ativo", ativo.getId(), "tipo", tipo, "quantidade", 10, "preco", "12.50",
                        "investidor", -1, "status", "EXECUTADA"), token);
                assertEquals(201, response.statusCode(), response.body());
                var dados = client.dados(response);
                assertEquals(investidor, dados.get("investidor").asLong());
                assertEquals("PENDENTE", dados.get("status").asString());
                validarPersistencia(dados.get("id").asLong(), tipo);
            }
            assertEquals(new BigDecimal("20.00"), ativos.findById(ativo.getId()).orElseThrow().getPrecoAtual());
        }
    }

    @Test
    void rejeitaDadosInvalidosSemPersistir() throws Exception {
        var ativo = criarAtivo();
        try (var client = new HttpTestUtil(port)) {
            entrar(client);
            var token = csrf(client);
            long antes = ordens.count();
            for (var quantidade : new Object[]{0, -1, "1.5", new BigDecimal("1.5"), 2147483648L}) {
                assertEquals(400, client.enviar("POST", "/ordens", pedido(ativo.getId(), "COMPRA", quantidade, "10"), token).statusCode());
            }
            for (var preco : new String[]{"0", "-1", "1.001", "100000000000000000.00"}) {
                assertEquals(400, client.enviar("POST", "/ordens", pedido(ativo.getId(), "VENDA", 1, preco), token).statusCode());
            }
            assertEquals(400, client.enviar("POST", "/ordens", Map.of(), token).statusCode());
            assertEquals(400, client.enviar("POST", "/ordens", pedido(ativo.getId(), "INVALIDO", 1, "10"), token).statusCode());
            assertEquals(404, client.enviar("POST", "/ordens", pedido(Long.MAX_VALUE, "COMPRA", 1, "10"), token).statusCode());
            assertEquals(antes, ordens.count());
        }
    }

    @Test
    void exigeAutenticacaoECsrf() throws Exception {
        try (var client = new HttpTestUtil(port)) {
            var pedido = pedido(1L, "COMPRA", 1, "10");
            assertEquals(401, client.enviar("POST", "/ordens", pedido, csrf(client)).statusCode());
            entrar(client);
            assertEquals(403, client.enviar("POST", "/ordens", pedido, null).statusCode());
        }
    }

    private Ativo criarAtivo() {
        return ativos.saveAndFlush(new Ativo(UUID.randomUUID().toString().substring(0, 12), "Ativo", new BigDecimal("20.00"), 1000));
    }

    private void validarPersistencia(Long id, String tipo) {
        var persistida = ordens.findById(id).orElseThrow();
        assertEquals(tipo, persistida.getTipo().name());
        assertEquals("PENDENTE", persistida.getStatus().name());
        assertEquals(10, persistida.getQuantidade());
        assertEquals(10, persistida.getQuantidadeRestante());
        assertEquals(new BigDecimal("12.50"), persistida.getPreco());
        assertNotNull(persistida.getDataHora());
    }

    private long entrar(HttpTestUtil client) throws Exception {
        var token = csrf(client);
        var email = UUID.randomUUID() + "@example.com";
        assertEquals(201, client.enviar("POST", "/api/auth/cadastro", Map.of(
                "nome", "Maria Silva", "email", email, "senha", "Senha123!"), token).statusCode());
        var login = client.enviar("POST", "/api/auth/login", Map.of("email", email, "senha", "Senha123!"), token);
        assertEquals(200, login.statusCode());
        return client.dados(login).get("usuario").get("id").asLong();
    }

    private Map<String, Object> pedido(Long ativo, String tipo, Object quantidade, String preco) {
        return Map.of("ativo", ativo, "tipo", tipo, "quantidade", quantidade, "preco", preco);
    }

    private JsonNode csrf(HttpTestUtil client) throws Exception {
        return client.dados(client.enviar("GET", "/api/auth/csrf", null, null));
    }
}
