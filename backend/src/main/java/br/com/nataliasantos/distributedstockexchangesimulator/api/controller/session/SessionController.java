package br.com.nataliasantos.distributedstockexchangesimulator.api.controller.session;

import br.com.nataliasantos.distributedstockexchangesimulator.api.utils.ApiResult;
import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.session.SessionDTO;
import br.com.nataliasantos.distributedstockexchangesimulator.api.utils.ApiResultUtil;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.session.impl.SessionServiceImpl;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.session.utils.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sessoes")
@RequiredArgsConstructor
public class SessionController {

    private final SessionServiceImpl service;
    private final ApiResultUtil<SessionDTO.Response.Sessao> sessaoResult;
    private final ApiResultUtil<List<SessionDTO.Response.Sessao>> listaResult;

    @GetMapping("/atual")
    public ResponseEntity<ApiResult<SessionDTO.Response.Sessao>> consultar(
            HttpServletRequest request, Authentication authentication) {
        var session = request.getSession(false);
        var resultado = service.consultar(session == null ? null : session.getId(),
                Long.valueOf(authentication.getName()));
        return sessaoResult.resultSucesso(HttpStatus.OK, SessionUtil.resposta(resultado));
    }

    @GetMapping
    public ResponseEntity<ApiResult<List<SessionDTO.Response.Sessao>>> listar(Authentication authentication) {
        var sessoes = service.listar(Long.valueOf(authentication.getName())).stream()
                .map(SessionUtil::resposta).toList();
        return listaResult.resultSucesso(HttpStatus.OK, sessoes);
    }
}
