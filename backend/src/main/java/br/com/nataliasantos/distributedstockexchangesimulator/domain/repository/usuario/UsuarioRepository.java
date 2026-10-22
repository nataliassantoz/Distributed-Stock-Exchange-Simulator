package br.com.nataliasantos.distributedstockexchangesimulator.domain.repository.usuario;

import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
