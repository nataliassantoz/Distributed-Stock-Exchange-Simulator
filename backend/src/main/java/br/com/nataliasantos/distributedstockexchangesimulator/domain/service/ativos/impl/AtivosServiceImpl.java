package br.com.nataliasantos.distributedstockexchangesimulator.domain.service.ativos.impl;

import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.ativo.AtivoDTO;
import br.com.nataliasantos.distributedstockexchangesimulator.api.utils.ApiResult;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.repository.ativo.AtivoRepository;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.ativos.AtivosService;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.ativos.utils.AtivosUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import br.com.nataliasantos.distributedstockexchangesimulator.api.utils.ApiResultUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AtivosServiceImpl implements AtivosService {
    private final AtivoRepository ativoRepository;
    private final ApiResultUtil<List<AtivoDTO.Response.Ativo>> resultUtil;

    @Override
    public ResponseEntity<ApiResult<List<AtivoDTO.Response.Ativo>>> consultar(String nome, String codigo) {
        var ativos = ativoRepository.findByNomeContainingIgnoreCaseAndTickerContainingIgnoreCaseOrderByTickerAsc(
                        AtivosUtil.normalizarFiltro(nome), AtivosUtil.normalizarFiltro(codigo)).stream()
                .map(AtivosUtil::paraResponse)
                .toList();
        return resultUtil.resultSucesso(HttpStatus.OK, ativos);
    }
}
