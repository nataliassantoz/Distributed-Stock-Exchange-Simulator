package br.com.nataliasantos.distributedstockexchangesimulator.domain.service.ordens;

import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.ordem.OrdemDTO;
import br.com.nataliasantos.distributedstockexchangesimulator.api.utils.ApiResult;
import org.springframework.http.ResponseEntity;

public interface OrdensService {
    ResponseEntity<ApiResult<OrdemDTO.Response.Ordem>> registrar(Long idInvestidor, OrdemDTO.Request.Cadastro request);
}
