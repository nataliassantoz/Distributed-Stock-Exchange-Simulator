package br.com.nataliasantos.distributedstockexchangesimulator.api.controller.ordens;

import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.ordem.OrdemDTO;
import br.com.nataliasantos.distributedstockexchangesimulator.api.utils.ApiResult;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.ordens.OrdensService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/ordens")
@RequiredArgsConstructor
public class OrdensController {
    private final OrdensService service;

    @PostMapping
    @Operation(summary = "Registra uma ordem pendente de compra ou venda")
    public ResponseEntity<ApiResult<OrdemDTO.Response.Ordem>> registrar(
            Principal principal, @Valid @RequestBody OrdemDTO.Request.Cadastro request) {
        return service.registrar(Long.valueOf(principal.getName()), request);
    }
}
