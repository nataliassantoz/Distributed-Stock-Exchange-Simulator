package br.com.nataliasantos.distributedstockexchangesimulator;

import br.com.nataliasantos.distributedstockexchangesimulator.core.enums.StatusOrdem;
import br.com.nataliasantos.distributedstockexchangesimulator.core.enums.TipoOrdem;
import br.com.nataliasantos.distributedstockexchangesimulator.core.enums.TipoUsuario;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.ativo.Ativo;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.ordem.Ordem;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.usuario.Usuario;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class OrdemTest {
    @Test
    void novaOrdemPreservaRecursosEInicializaQuantidadeRestante() {
        var usuario = new Usuario("Maria", "maria@example.com", "hash", TipoUsuario.HUMANO);
        var ativo = new Ativo("TEC3", "TechNova", new BigDecimal("50.00"), 1000);
        var antes = Instant.now();
        for (var tipo : TipoOrdem.values()) {
            var ordem = new Ordem(usuario, ativo, tipo, 10, new BigDecimal("45.00"));
            assertSame(usuario, ordem.getInvestidor());
            assertSame(ativo, ordem.getAtivo());
            assertEquals(StatusOrdem.PENDENTE, ordem.getStatus());
            assertEquals(ordem.getQuantidade(), ordem.getQuantidadeRestante());
            assertFalse(ordem.getDataHora().isBefore(antes));
            assertEquals(BigDecimal.ZERO, usuario.getSaldoCaixa());
            assertEquals(BigDecimal.ZERO, usuario.getSaldoReservado());
            assertEquals(new BigDecimal("50.00"), ativo.getPrecoAtual());
        }
    }
}
