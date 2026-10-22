package br.com.nataliasantos.distributedstockexchangesimulator.api.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ApiResultUtil<T> {
    private static final String MSG_USUARIO = "Operação realizada com sucesso";

    public ResponseEntity<ApiResult<T>> resultSucesso(HttpStatus status) {
        return resultSucesso(status, MSG_USUARIO, null);
    }

    public ResponseEntity<ApiResult<T>> resultSucesso(HttpStatus status, T dados) {
        return resultSucesso(status, MSG_USUARIO, dados);
    }

    public ResponseEntity<ApiResult<T>> resultSucesso(HttpStatus status, String msgUsuario, T dados) {
        return ResponseEntity.status(status).body(entidadeResult(status.value(), null, msgUsuario, dados));
    }

    public ResponseEntity<ApiResult<T>> resultErro(HttpStatus status, String msgTecnica, String msgUsuario) {
        return resultErro(status, msgTecnica, msgUsuario, null);
    }

    public ResponseEntity<ApiResult<T>> resultErro(HttpStatus status, String msgTecnica, String msgUsuario, T dados) {
        return ResponseEntity.status(status).body(entidadeResult(status.value(), msgTecnica, msgUsuario, dados));
    }

    private ApiResult<T> entidadeResult(int codigo, String msgTecnica, String msgUsuario, T dados) {
        var result = new ApiResult<T>();
        result.setCdRetorno(codigo);
        result.setMsgTecnica(msgTecnica);
        result.setMsgUsuario(msgUsuario);
        result.setDados(dados);
        return result;
    }
}
