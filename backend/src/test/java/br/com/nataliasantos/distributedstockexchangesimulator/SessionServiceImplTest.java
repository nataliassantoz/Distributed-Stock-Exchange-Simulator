package br.com.nataliasantos.distributedstockexchangesimulator;

import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.session.impl.SessionServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class SessionServiceImplTest {
    private final SessionServiceImpl service = new SessionServiceImpl();

    @Test
    void permiteMultiplasSessoesPorUsuarioEIsolaConsultas() {
        var primeira = service.registrar("primeira", 1L);
        var segunda = service.registrar("segunda", 1L);
        service.registrar("outro", 2L);
        assertEquals(2, service.listar(1L).size());
        assertEquals(primeira, service.consultar("primeira", 1L));
        assertThrows(ResponseStatusException.class, () -> service.consultar("primeira", 2L));
        service.remover("primeira");
        service.remover("primeira");
        assertEquals(java.util.List.of(segunda), service.listar(1L));
        assertThrows(ResponseStatusException.class, () -> service.consultar("primeira", 1L));
    }

    @Test
    void rejeitaIdentificadoresInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> service.registrar(null, 1L));
        assertThrows(IllegalArgumentException.class, () -> service.registrar(" ", 1L));
        assertThrows(IllegalArgumentException.class, () -> service.registrar("sessao", null));
        assertThrows(IllegalArgumentException.class, () -> service.registrar("sessao", 0L));
        assertThrows(ResponseStatusException.class, () -> service.consultar(null, 1L));
    }

    @Test
    void suportaRegistrosERemocoesConcorrentes() throws Exception {
        try (var executor = Executors.newFixedThreadPool(8)) {
            var tarefas = IntStream.range(0, 100).<Callable<String>>mapToObj(i -> () -> {
                var id = UUID.randomUUID().toString();
                service.registrar(id, 1L);
                return id;
            }).toList();
            var resultados = executor.invokeAll(tarefas);
            assertEquals(100, service.listar(1L).size());
            var remocoes = resultados.stream().<Callable<Void>>map(resultado -> () -> {
                service.remover(resultado.get());
                return null;
            }).toList();
            for (var resultado : executor.invokeAll(remocoes)) resultado.get();
            assertTrue(service.listar(1L).isEmpty());
        }
    }
}
