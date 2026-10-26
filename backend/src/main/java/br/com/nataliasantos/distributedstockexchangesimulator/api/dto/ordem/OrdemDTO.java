package br.com.nataliasantos.distributedstockexchangesimulator.api.dto.ordem;

import br.com.nataliasantos.distributedstockexchangesimulator.core.enums.TipoOrdem;
import br.com.nataliasantos.distributedstockexchangesimulator.core.enums.StatusOrdem;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

public final class OrdemDTO {
    private OrdemDTO() {}

    public static final class Request {
        private Request() {}

        public record Cadastro(
                @NotNull @Positive Long ativo,
                @NotNull TipoOrdem tipo,
                @NotNull @Positive @Digits(integer = 10, fraction = 0)
                @DecimalMax("2147483647") BigDecimal quantidade,
                @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal preco) {}
    }

    public static final class Response {
        private Response() {}

        public record Ordem(Long id, Long investidor, Long ativo, TipoOrdem tipo,
                            Integer quantidade, BigDecimal preco, StatusOrdem status, Instant dataHora) {}
    }
}
