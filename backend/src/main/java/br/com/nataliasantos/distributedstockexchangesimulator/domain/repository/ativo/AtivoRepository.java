package br.com.nataliasantos.distributedstockexchangesimulator.domain.repository.ativo;

import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.ativo.Ativo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtivoRepository extends JpaRepository<Ativo, Long> {
    List<Ativo> findByNomeContainingIgnoreCaseAndTickerContainingIgnoreCaseOrderByTickerAsc(
            String nome, String ticker);
}
