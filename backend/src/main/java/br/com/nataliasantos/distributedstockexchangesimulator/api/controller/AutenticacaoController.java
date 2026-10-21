package br.com.nataliasantos.distributedstockexchangesimulator.api.controller;

import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.UsuarioDTO;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.service.autenticacao.AutenticacaoService;
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
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository contextRepository;

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) { return token; }

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO.Response.Usuario cadastrar(@Valid @RequestBody UsuarioDTO.Request.Cadastro request) {
        return service.cadastrar(request);
    }

    @PostMapping("/login")
    public UsuarioDTO.Response.Sessao login(@Valid @RequestBody UsuarioDTO.Request.Login credentials,
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
        return new UsuarioDTO.Response.Sessao(request.getSession().getId(),
                service.identificar(Long.valueOf(authentication.getName())));
    }

    @GetMapping("/me")
    public UsuarioDTO.Response.Usuario identificar(Authentication authentication) {
        return service.identificar(Long.valueOf(authentication.getName()));
    }
}
