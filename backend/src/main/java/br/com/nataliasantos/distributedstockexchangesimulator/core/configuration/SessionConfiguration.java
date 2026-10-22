package br.com.nataliasantos.distributedstockexchangesimulator.core.configuration;

import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.session.impl.SessionServiceImpl;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionIdListener;
import jakarta.servlet.http.HttpSessionListener;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionConfiguration {
    @Bean
    ServletListenerRegistrationBean<SessionListener> sessionListener(SessionServiceImpl service) {
        return new ServletListenerRegistrationBean<>(new SessionListener(service));
    }

    @RequiredArgsConstructor
    static class SessionListener implements HttpSessionListener, HttpSessionIdListener {
        private final SessionServiceImpl service;

        @Override
        public void sessionDestroyed(HttpSessionEvent event) {
            service.remover(event.getSession().getId());
        }

        @Override
        public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
            service.remover(oldSessionId);
        }
    }
}
