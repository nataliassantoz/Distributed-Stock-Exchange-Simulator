package br.com.nataliasantos.distributedstockexchangesimulator.domain.service.investidores.impl;

import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.investidor.InvestidorDTO;
import br.com.nataliasantos.distributedstockexchangesimulator.api.utils.ApiResult;
import br.com.nataliasantos.distributedstockexchangesimulator.api.utils.ApiResultUtil;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.session.Session;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.repository.usuario.UsuarioRepository;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.investidores.InvestidoresService;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.investidores.utils.InvestidoresUtil;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.session.impl.SessionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvestidoresServiceImpl implements InvestidoresService {
    private final SessionServiceImpl sessionService;
    private final UsuarioRepository usuarioRepository;
    private final ApiResultUtil<List<InvestidorDTO.Response.Investidor>> resultUtil;

    @Override
    public ResponseEntity<ApiResult<List<InvestidorDTO.Response.Investidor>>> consultar() {
        var sessoes = sessionService.listarAtivas().stream()
                .collect(Collectors.groupingBy(Session::idUsuario));
        var investidores = usuarioRepository.findAllById(sessoes.keySet()).stream()
                .map(usuario -> InvestidoresUtil.paraResponse(usuario, sessoes.get(usuario.getId())))
                .sorted(Comparator.comparing(InvestidorDTO.Response.Investidor::idUsuario))
                .toList();
        return resultUtil.resultSucesso(HttpStatus.OK, investidores);
    }
}
