package br.com.nataliasantos.distributedstockexchangesimulator.domain.service.ativos;

import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.AtivoDTO;
import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.ApiResult;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AtivosService {
    ResponseEntity<ApiResult<List<AtivoDTO.Response.Ativo>>> consultar(String nome, String codigo);
}
