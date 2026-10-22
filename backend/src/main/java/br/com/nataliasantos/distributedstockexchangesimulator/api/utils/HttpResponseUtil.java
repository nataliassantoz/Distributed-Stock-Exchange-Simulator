package br.com.nataliasantos.distributedstockexchangesimulator.api.utils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class HttpResponseUtil {
    private final JsonMapper json;

    public void escrever(HttpServletResponse response, ResponseEntity<?> resultado) throws IOException {
        response.setStatus(resultado.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(json.writeValueAsString(resultado.getBody()));
    }
}
