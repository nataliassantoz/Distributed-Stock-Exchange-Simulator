package br.com.nataliasantos.distributedstockexchangesimulator.domain.service.ativos.utils;

import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.ativo.AtivoDTO;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.ativo.Ativo;

public final class AtivosUtil {
    private AtivosUtil() {}

    public static String normalizarFiltro(String filtro) {
        return filtro == null ? "" : filtro.strip();
    }

    public static AtivoDTO.Response.Ativo paraResponse(Ativo ativo) {
        return new AtivoDTO.Response.Ativo(ativo.getTicker(), ativo.getNome(), ativo.getPrecoAtual());
    }
}
