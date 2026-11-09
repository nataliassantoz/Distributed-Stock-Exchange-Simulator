package br.com.nataliasantos.distributedstockexchangesimulator.api.controller.investidores;

import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.investidor.InvestidorDTO;
import br.com.nataliasantos.distributedstockexchangesimulator.api.utils.ApiResult;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.investidores.InvestidoresService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mercado/investidores")
@RequiredArgsConstructor
public class InvestidoresController {
    private final InvestidoresService service;

    @Operation(summary = "Consulta os investidores com sessões autenticadas ativas no mercado")
    @GetMapping
    public ResponseEntity<ApiResult<List<InvestidorDTO.Response.Investidor>>> consultar() {
        return service.consultar();
    }
}
