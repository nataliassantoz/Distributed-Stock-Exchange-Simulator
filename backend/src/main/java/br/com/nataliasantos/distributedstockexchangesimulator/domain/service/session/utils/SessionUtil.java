package br.com.nataliasantos.distributedstockexchangesimulator.domain.service.session.utils;

import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.session.SessionDTO;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.session.Session;

public final class SessionUtil {
    private SessionUtil() {}

    public static SessionDTO.Response.Sessao resposta(Session session) {
        return new SessionDTO.Response.Sessao(session.idSessao(), session.idUsuario(),
                session.dataHoraConexao(), session.status());
    }
}
