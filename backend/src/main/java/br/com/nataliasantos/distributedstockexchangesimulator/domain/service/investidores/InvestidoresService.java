package br.com.nataliasantos.distributedstockexchangesimulator.domain.service.investidores;

import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.investidor.InvestidorDTO;
import br.com.nataliasantos.distributedstockexchangesimulator.api.utils.ApiResult;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface InvestidoresService {
    ResponseEntity<ApiResult<List<InvestidorDTO.Response.Investidor>>> consultar();
}
