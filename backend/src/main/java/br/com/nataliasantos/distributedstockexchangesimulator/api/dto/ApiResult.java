package br.com.nataliasantos.distributedstockexchangesimulator.api.dto;

import lombok.Data;

@Data
public class ApiResult<T> {
    private Integer cdRetorno;
    private String msgTecnica;
    private String msgUsuario;
    private T dados;
}
