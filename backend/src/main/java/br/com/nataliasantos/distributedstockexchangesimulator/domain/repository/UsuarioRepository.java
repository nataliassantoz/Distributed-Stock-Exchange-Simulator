package br.com.nataliasantos.distributedstockexchangesimulator.domain.repository;

import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
