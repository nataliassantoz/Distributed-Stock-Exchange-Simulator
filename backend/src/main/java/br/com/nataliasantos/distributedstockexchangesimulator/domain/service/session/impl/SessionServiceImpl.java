package br.com.nataliasantos.distributedstockexchangesimulator.domain.service.session.impl;

import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.session.Session;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class SessionServiceImpl {
    private final ConcurrentMap<String, Session> sessoes = new ConcurrentHashMap<>();

    public Session registrar(String idSessao, Long idUsuario) {
        if (idSessao == null || idSessao.isBlank() || idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("Sessão e usuário válidos são obrigatórios");
        }
        var session = new Session(idSessao, idUsuario, Instant.now(), "ATIVA");
        sessoes.put(idSessao, session);
        return session;
    }

    public Session consultar(String idSessao, Long idUsuario) {
        var session = idSessao == null ? null : sessoes.get(idSessao);
        if (session == null || !session.idUsuario().equals(idUsuario)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão ativa não encontrada");
        }
        return session;
    }

    public List<Session> listar(Long idUsuario) {
        return sessoes.values().stream()
                .filter(session -> session.idUsuario().equals(idUsuario))
                .sorted(Comparator.comparing(Session::dataHoraConexao).thenComparing(Session::idSessao))
                .toList();
    }

    public void remover(String idSessao) {
        sessoes.remove(idSessao);
    }
}
