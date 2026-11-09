package br.com.nataliasantos.distributedstockexchangesimulator.api.dto.investidor;

import java.time.Instant;

public final class InvestidorDTO {
    private InvestidorDTO() {}

    public static final class Response {
        private Response() {}

        public record Investidor(Long idUsuario, String nome, Instant dataHoraConexao, long quantidadeSessoes) {}
    }
}
