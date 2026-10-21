package br.com.nataliasantos.distributedstockexchangesimulator.domain.repository;

import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.Ativo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtivoRepository extends JpaRepository<Ativo, Long> {
    List<Ativo> findByNomeContainingIgnoreCaseAndTickerContainingIgnoreCaseOrderByTickerAsc(
            String nome, String ticker);
}
