package br.com.nataliasantos.distributedstockexchangesimulator.core.configuration;

import br.com.nataliasantos.distributedstockexchangesimulator.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import static br.com.nataliasantos.distributedstockexchangesimulator.domain.service.autenticacao.AutenticacaoService.normalizarEmail;

@Configuration
public class SecurityConfiguration {
    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    AuthenticationManager authenticationManager(UsuarioRepository usuarios, PasswordEncoder encoder) {
        var provider = new DaoAuthenticationProvider(email -> usuarios.findByEmail(normalizarEmail(email))
                .map(usuario -> User.withUsername(usuario.getId().toString())
                        .password(usuario.getSenhaHash()).roles("USUARIO").build())
                .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas")));
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityContextRepository repository,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/cadastro", "/api/auth/login", "/api/auth/csrf", "/actuator/health", "/error").permitAll()
                        .anyRequest().authenticated())
                .securityContext(context -> context.securityContextRepository(repository))
                .requestCache(cache -> cache.disable())
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((request, response, exception) -> resolver.resolveException(request, response, null,
                                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autenticação necessária. Faça login para continuar.")))
                        .accessDeniedHandler((request, response, exception) -> resolver.resolveException(request, response, null,
                                new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado. Verifique suas permissões e o token CSRF."))))
                .build();
    }
}
