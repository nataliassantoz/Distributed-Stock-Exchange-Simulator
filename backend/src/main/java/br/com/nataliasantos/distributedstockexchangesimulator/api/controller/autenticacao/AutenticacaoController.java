package br.com.nataliasantos.distributedstockexchangesimulator.api.controller.autenticacao;

import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.usuario.UsuarioDTO;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.session.impl.SessionServiceImpl;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.autenticacao.AutenticacaoService;
import br.com.nataliasantos.distributedstockexchangesimulator.api.utils.ApiResult;
import br.com.nataliasantos.distributedstockexchangesimulator.api.utils.ApiResultUtil;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AutenticacaoService service;
    private final SessionServiceImpl sessionServiceImpl;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository contextRepository;

    private final ApiResultUtil<CsrfToken> csrfResult;
    private final ApiResultUtil<UsuarioDTO.Response.Usuario> usuarioResult;
    private final ApiResultUtil<UsuarioDTO.Response.Sessao> sessaoResult;

    @GetMapping("/csrf")
    public ResponseEntity<ApiResult<CsrfToken>> csrf(CsrfToken token) {
        return csrfResult.resultSucesso(HttpStatus.OK, token);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<ApiResult<UsuarioDTO.Response.Usuario>> cadastrar(@Valid @RequestBody UsuarioDTO.Request.Cadastro request) {
        return usuarioResult.resultSucesso(HttpStatus.CREATED, service.cadastrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResult<UsuarioDTO.Response.Sessao>> login(@Valid @RequestBody UsuarioDTO.Request.Login credentials,
                                          HttpServletRequest request, HttpServletResponse response) {
        final Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(credentials.getEmail(), credentials.getSenha()));
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }
        request.getSession(true);
        request.changeSessionId();
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        contextRepository.saveContext(context, request, response);
        sessionServiceImpl.registrar(request.getSession(), Long.valueOf(authentication.getName()));
        return sessaoResult.resultSucesso(HttpStatus.OK, new UsuarioDTO.Response.Sessao(request.getSession().getId(),
                service.identificar(Long.valueOf(authentication.getName()))));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResult<UsuarioDTO.Response.Usuario>> identificar(Authentication authentication) {
        return usuarioResult.resultSucesso(HttpStatus.OK, service.identificar(Long.valueOf(authentication.getName())));
    }
}
