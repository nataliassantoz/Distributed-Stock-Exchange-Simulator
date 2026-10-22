package br.com.nataliasantos.distributedstockexchangesimulator;

import br.com.nataliasantos.distributedstockexchangesimulator.api.controller.ativos.AtivosController;
import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.ativo.AtivoDTO;
import br.com.nataliasantos.distributedstockexchangesimulator.api.utils.ApiResultUtil;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.ativo.Ativo;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.repository.ativo.AtivoRepository;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.ativos.impl.AtivosServiceImpl;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AtivosTest {
    private final AtivoRepository repository = mock(AtivoRepository.class);
    private final AtivosController controller = new AtivosController(new AtivosServiceImpl(repository, new ApiResultUtil<>()));

    @Test
    void consultaRetornaCodigoNomeEPrecoAtualSemExporCamposInternos() {
        var ativo = new Ativo("PETR4", "Petrobras", new BigDecimal("30.00"), 1000);
        ativo.setPrecoAtual(new BigDecimal("38.50"));
        when(repository.findByNomeContainingIgnoreCaseAndTickerContainingIgnoreCaseOrderByTickerAsc("", "")).thenReturn(List.of(ativo));

        var response = controller.consultar(null, null);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCdRetorno());
        assertEquals("Operação realizada com sucesso", response.getBody().getMsgUsuario());
        assertNull(response.getBody().getMsgTecnica());
        assertEquals(List.of(new AtivoDTO.Response.Ativo("PETR4", "Petrobras", new BigDecimal("38.50"))),
                response.getBody().getDados());
        var json = new JsonMapper();
        assertEquals(json.readTree("""
                [{"codigo":"PETR4","nome":"Petrobras","precoAtual":38.50}]
                """), json.readTree(json.writeValueAsString(response.getBody().getDados())));
    }

    @Test
    void consultaSemAtivosRetornaListaVaziaComSucesso() {
        when(repository.findByNomeContainingIgnoreCaseAndTickerContainingIgnoreCaseOrderByTickerAsc("", "")).thenReturn(List.of());

        var response = controller.consultar(null, null);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCdRetorno());
        assertEquals("Operação realizada com sucesso", response.getBody().getMsgUsuario());
        assertNull(response.getBody().getMsgTecnica());
        assertEquals(List.of(), response.getBody().getDados());
    }
}
