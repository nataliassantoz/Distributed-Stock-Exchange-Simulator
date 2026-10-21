package br.com.nataliasantos.distributedstockexchangesimulator.api.controller.ativos;

import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.AtivoDTO;
import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.ApiResult;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.ativos.AtivosService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ativos")
@RequiredArgsConstructor
public class AtivosController {
    private final AtivosService ativosService;

    @Operation(summary = "Consulta os ativos disponíveis e seus preços atuais")
    @GetMapping
    public ResponseEntity<ApiResult<List<AtivoDTO.Response.Ativo>>> consultar(
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "codigo", required = false) String codigo) {
        return ativosService.consultar(nome, codigo);
    }
}
