package br.com.nataliasantos.distributedstockexchangesimulator.domain.service.investidores.utils;

import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.investidor.InvestidorDTO;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.session.Session;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.usuario.Usuario;

import java.util.List;

public final class InvestidoresUtil {
    private InvestidoresUtil() {}

    public static InvestidorDTO.Response.Investidor paraResponse(Usuario usuario, List<Session> sessoes) {
        var conexao = sessoes.stream().map(Session::dataHoraConexao).min(java.time.Instant::compareTo).orElseThrow();
        return new InvestidorDTO.Response.Investidor(usuario.getId(), usuario.getNome(), conexao, sessoes.size());
    }
}
