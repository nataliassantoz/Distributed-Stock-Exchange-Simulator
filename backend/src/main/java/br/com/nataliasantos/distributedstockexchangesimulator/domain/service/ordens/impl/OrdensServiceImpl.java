package br.com.nataliasantos.distributedstockexchangesimulator.domain.service.ordens.impl;

import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.ordem.OrdemDTO;
import br.com.nataliasantos.distributedstockexchangesimulator.api.utils.ApiResult;
import br.com.nataliasantos.distributedstockexchangesimulator.api.utils.ApiResultUtil;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.ordem.Ordem;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.repository.ativo.AtivoRepository;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.repository.usuario.UsuarioRepository;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.repository.ordem.OrdemRepository;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.ordens.OrdensService;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.ordens.utils.OrdensUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OrdensServiceImpl implements OrdensService {
    private final UsuarioRepository usuarioRepository;
    private final AtivoRepository ativoRepository;
    private final OrdemRepository ordemRepository;
    private final ApiResultUtil<OrdemDTO.Response.Ordem> resultUtil;

    @Override
    @Transactional
    public ResponseEntity<ApiResult<OrdemDTO.Response.Ordem>> registrar(Long idInvestidor, OrdemDTO.Request.Cadastro request) {

        var investidor = usuarioRepository.findById(idInvestidor)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Investidor não encontrado"));

        var ativo = ativoRepository.findById(request.ativo())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Ativo não encontrado"));

        var ordem = ordemRepository
                .save(new Ordem(investidor, ativo, request.tipo(),
                        request.quantidade().intValueExact(),
                        request.preco()));

        return resultUtil.resultSucesso(HttpStatus.CREATED, OrdensUtil.paraResponse(ordem));
    }
}
