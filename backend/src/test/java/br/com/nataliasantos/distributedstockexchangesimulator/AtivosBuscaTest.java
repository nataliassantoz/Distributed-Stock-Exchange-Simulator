package br.com.nataliasantos.distributedstockexchangesimulator;

import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.Ativo;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.repository.AtivoRepository;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.ativos.AtivosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = {
        "spring.datasource.url=jdbc:h2:mem:ativosbusca;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=validate"
})
@Transactional
class AtivosBuscaTest {
    @Autowired
    AtivoRepository repository;
    @Autowired
    AtivosService service;

    @BeforeEach
    void prepararAtivos() {
        repository.saveAllAndFlush(List.of(
                new Ativo("PETR4", "Petrobras", new BigDecimal("38.50"), 1000),
                new Ativo("PETR3", "Petrobras", new BigDecimal("40.00"), 1000),
                new Ativo("VALE3", "Vale", new BigDecimal("60.00"), 1000)));
    }

    @Test
    void buscaPorParteDoNomeIgnoraCaixaEEspacosExternos() {
        assertEquals(List.of("PETR3", "PETR4"), codigos("  pEtRo  ", null));
    }

    @Test
    void buscaPorParteDoCodigoIgnoraCaixa() {
        assertEquals(List.of("PETR3", "PETR4"), codigos(null, "petr"));
    }

    @Test
    void combinaNomeECodigo() {
        assertEquals(List.of("PETR4"), codigos("petro", "4"));
        assertEquals(List.of(), codigos("vale", "PETR4"));
    }

    @Test
    void filtrosAusentesOuVaziosListamTodosOrdenados() {
        var esperados = List.of("PETR3", "PETR4", "VALE3");
        assertEquals(esperados, codigos(null, null));
        assertEquals(esperados, codigos(" ", ""));
    }

    @Test
    void caracteresCuringaSaoTratadosComoTexto() {
        assertEquals(List.of(), codigos("%", null));
        assertEquals(List.of(), codigos(null, "_"));
    }

    private List<String> codigos(String nome, String codigo) {
        var response = service.consultar(nome, codigo);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        return response.getBody().getDados().stream().map(ativo -> ativo.codigo()).toList();
    }
}
