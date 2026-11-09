package br.com.nataliasantos.distributedstockexchangesimulator.core.configuration;

import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.session.impl.SessionServiceImpl;
import jakarta.servlet.http.HttpSessionEvent;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.*;

class SessionListenerTest {
    @Test
    void destruicaoPorExpiracaoOuInvalidacaoRemoveRegistro() {
        var service = new SessionServiceImpl();
        var session = new MockHttpSession();
        service.registrar(session, 1L);
        var listener = new SessionConfiguration.SessionListener(service);
        listener.sessionDestroyed(new HttpSessionEvent(session));
        listener.sessionDestroyed(new HttpSessionEvent(session));
        assertTrue(service.listar(1L).isEmpty());
    }
}
