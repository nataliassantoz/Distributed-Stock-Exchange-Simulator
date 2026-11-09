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

    private br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.session.Session registrar(String id, Long usuario) {
        return service.registrar(id == null ? null : new org.springframework.mock.web.MockHttpSession(null, id), usuario);
    }

    @Test
    void permiteMultiplasSessoesPorUsuarioEIsolaConsultas() {
        var primeira = registrar("primeira", 1L);
        var segunda = registrar("segunda", 1L);
        registrar("outro", 2L);
        assertEquals(2, service.listar(1L).size());
        assertEquals(primeira, service.consultar("primeira", 1L));
        assertThrows(ResponseStatusException.class, () -> service.consultar("primeira", 2L));
        service.remover("primeira");
        service.remover("primeira");
        assertEquals(java.util.List.of(segunda), service.listar(1L));
        assertThrows(ResponseStatusException.class, () -> service.consultar("primeira", 1L));
    }

    @Test
    void listagemGlobalExcluiSessoesExpiradasInvalidasERemovidas() {
        var ativa = new org.springframework.mock.web.MockHttpSession();
        var expirada = org.mockito.Mockito.mock(jakarta.servlet.http.HttpSession.class);
        org.mockito.Mockito.when(expirada.getId()).thenReturn("expirada");
        org.mockito.Mockito.when(expirada.getMaxInactiveInterval()).thenReturn(1);
        org.mockito.Mockito.when(expirada.getLastAccessedTime()).thenReturn(System.currentTimeMillis() - 2000);
        var invalida = new org.springframework.mock.web.MockHttpSession();
        var removida = new org.springframework.mock.web.MockHttpSession();
        var esperada = service.registrar(ativa, 1L);
        service.registrar(expirada, 2L);
        service.registrar(invalida, 3L);
        service.registrar(removida, 4L);
        invalida.invalidate();
        service.remover(removida.getId());
        assertEquals(java.util.List.of(esperada), service.listarAtivas());
        service.remover(ativa.getId());
        assertTrue(service.listarAtivas().isEmpty());
    }

    @Test
    void sessaoSemTimeoutPermaneceAtiva() {
        var conexao = new org.springframework.mock.web.MockHttpSession();
        conexao.setMaxInactiveInterval(0);
        var esperada = service.registrar(conexao, 1L);
        assertEquals(java.util.List.of(esperada), service.listarAtivas());
    }

    @Test
    void rejeitaIdentificadoresInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> registrar(null, 1L));
        assertThrows(IllegalArgumentException.class, () -> registrar(" ", 1L));
        assertThrows(IllegalArgumentException.class, () -> registrar("sessao", null));
        assertThrows(IllegalArgumentException.class, () -> registrar("sessao", 0L));
        assertThrows(ResponseStatusException.class, () -> service.consultar(null, 1L));
    }

    @Test
    void suportaRegistrosERemocoesConcorrentes() throws Exception {
        try (var executor = Executors.newFixedThreadPool(8)) {
            var tarefas = IntStream.range(0, 100).<Callable<String>>mapToObj(i -> () -> {
                var id = UUID.randomUUID().toString();
                registrar(id, 1L);
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
