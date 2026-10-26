package br.com.nataliasantos.distributedstockexchangesimulator.domain.repository.ordem;

import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.ordem.Ordem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdemRepository extends JpaRepository<Ordem, Long> {}
