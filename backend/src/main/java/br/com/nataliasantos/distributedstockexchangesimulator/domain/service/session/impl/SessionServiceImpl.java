package br.com.nataliasantos.distributedstockexchangesimulator.domain.service.session.impl;

import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.session.Session;
import jakarta.servlet.http.HttpSession;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.session.utils.SessionUtil;
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
    private record Registro(Session dados, HttpSession conexao) {}

    private final ConcurrentMap<String, Registro> sessoes = new ConcurrentHashMap<>();

    public Session registrar(HttpSession conexao, Long idUsuario) {
        var idSessao = conexao == null ? null : conexao.getId();
        if (idSessao == null || idSessao.isBlank() || idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("Sessão e usuário válidos são obrigatórios");
        }
        var session = new Session(idSessao, idUsuario, Instant.now(), "ATIVA");
        sessoes.put(idSessao, new Registro(session, conexao));
        return session;
    }

    public Session consultar(String idSessao, Long idUsuario) {
        var registro = idSessao == null ? null : sessoes.get(idSessao);
        var session = registro == null ? null : registro.dados();
        if (session == null || !session.idUsuario().equals(idUsuario)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão ativa não encontrada");
        }
        return session;
    }

    public List<Session> listar(Long idUsuario) {
        return sessoes.values().stream()
                .map(Registro::dados)
                .filter(session -> session.idUsuario().equals(idUsuario))
                .sorted(Comparator.comparing(Session::dataHoraConexao).thenComparing(Session::idSessao))
                .toList();
    }

    public List<Session> listarAtivas() {
        var agora = Instant.now();
        return sessoes.values().stream()
                .filter(registro -> SessionUtil.ativa(registro.conexao(), agora))
                .map(Registro::dados)
                .sorted(Comparator.comparing(Session::idUsuario).thenComparing(Session::dataHoraConexao))
                .toList();
    }

    public void remover(String idSessao) {
        sessoes.remove(idSessao);
    }
}
