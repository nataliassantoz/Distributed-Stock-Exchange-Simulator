package br.com.nataliasantos.distributedstockexchangesimulator.api.dto.ativo;

import java.math.BigDecimal;

public final class AtivoDTO {
    private AtivoDTO() {}

    public static final class Response {
        private Response() {}

        public record Ativo(String codigo, String nome, BigDecimal precoAtual) {}
    }
}
