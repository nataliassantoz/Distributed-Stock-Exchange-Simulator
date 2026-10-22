package br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.session;

import java.time.Instant;

public record Session(String idSessao, Long idUsuario, Instant dataHoraConexao, String status) {}
