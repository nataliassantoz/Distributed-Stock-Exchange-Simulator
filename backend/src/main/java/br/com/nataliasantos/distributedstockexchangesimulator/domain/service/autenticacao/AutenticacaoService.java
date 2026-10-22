package br.com.nataliasantos.distributedstockexchangesimulator.domain.service.autenticacao;

import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.usuario.UsuarioDTO;
import br.com.nataliasantos.distributedstockexchangesimulator.core.enums.TipoUsuario;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.usuario.Usuario;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.repository.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;

    @Transactional
    public UsuarioDTO.Response.Usuario cadastrar(UsuarioDTO.Request.Cadastro request) {
        if (request.getNome().strip().length() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome deve ter entre 3 e 150 caracteres");
        }
        if (request.getSenha().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha deve ter no máximo 72 bytes UTF-8");
        }
        var usuario = new Usuario(request.getNome().strip(), normalizarEmail(request.getEmail()),
                encoder.encode(request.getSenha()), TipoUsuario.HUMANO);
        try {
            return resposta(usuarios.saveAndFlush(usuario));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }
    }

    public static String normalizarEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }

    public UsuarioDTO.Response.Usuario identificar(Long id) {
        return usuarios.findById(id).map(AutenticacaoService::resposta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private static UsuarioDTO.Response.Usuario resposta(Usuario usuario) {
        return new UsuarioDTO.Response.Usuario(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
