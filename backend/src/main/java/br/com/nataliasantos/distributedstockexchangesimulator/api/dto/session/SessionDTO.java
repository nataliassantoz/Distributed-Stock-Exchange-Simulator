package br.com.nataliasantos.distributedstockexchangesimulator.api.dto.session;

import java.time.Instant;

public final class SessionDTO {
    private SessionDTO() {}

    public static final class Response {
        private Response() {}

        public record Sessao(String idSessao, Long idUsuario, Instant dataHoraConexao, String status) {}
    }
}
