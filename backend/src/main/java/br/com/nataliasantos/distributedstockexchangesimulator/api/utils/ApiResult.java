package br.com.nataliasantos.distributedstockexchangesimulator.api.utils;

import lombok.Data;

@Data
public class ApiResult<T> {
    private Integer cdRetorno;
    private String msgTecnica;
    private String msgUsuario;
    private T dados;
}
