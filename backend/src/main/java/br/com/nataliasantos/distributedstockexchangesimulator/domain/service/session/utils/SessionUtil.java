package br.com.nataliasantos.distributedstockexchangesimulator.domain.service.session.utils;

import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.session.SessionDTO;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.session.Session;

import jakarta.servlet.http.HttpSession;
import java.time.Instant;

public final class SessionUtil {
    private SessionUtil() {}

    public static boolean ativa(HttpSession conexao, Instant agora) {
        if (conexao == null) return false;
        try {
            var ultimoAcesso = Instant.ofEpochMilli(conexao.getLastAccessedTime());
            var timeout = conexao.getMaxInactiveInterval();
            return timeout <= 0 || ultimoAcesso.plusSeconds(timeout).isAfter(agora);
        } catch (IllegalStateException exception) {
            return false;
        }
    }

    public static SessionDTO.Response.Sessao resposta(Session session) {
        return new SessionDTO.Response.Sessao(session.idSessao(), session.idUsuario(),
                session.dataHoraConexao(), session.status());
    }
}
