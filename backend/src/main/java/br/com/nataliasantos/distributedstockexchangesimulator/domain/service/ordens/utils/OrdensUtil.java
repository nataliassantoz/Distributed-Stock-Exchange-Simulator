package br.com.nataliasantos.distributedstockexchangesimulator.domain.service.ordens.utils;

import br.com.nataliasantos.distributedstockexchangesimulator.api.dto.ordem.OrdemDTO;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.ordem.Ordem;

public final class OrdensUtil {
    private OrdensUtil() {}

    public static OrdemDTO.Response.Ordem paraResponse(Ordem ordem) {
        return new OrdemDTO.Response.Ordem(ordem.getId(), ordem.getInvestidor().getId(),
                ordem.getAtivo().getId(), ordem.getTipo(), ordem.getQuantidade(),
                ordem.getPreco(), ordem.getStatus(), ordem.getDataHora());
    }
}
