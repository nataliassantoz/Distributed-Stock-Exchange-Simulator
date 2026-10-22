package br.com.nataliasantos.distributedstockexchangesimulator.api.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

public enum UsuarioDTO {;
    public interface Nome {
        @Schema(example = "Maria Silva")
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
        String getNome();
    }

    public interface EmailUsuario {
        @Schema(example = "maria@example.com")
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 254)
        String getEmail();
    }

    public interface Senha {
        @Schema(accessMode = Schema.AccessMode.WRITE_ONLY)
        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, max = 72, message = "Senha deve ter entre 8 e 72 caracteres")
        String getSenha();
    }

    public enum Request {;
        @Data
        public static class Cadastro implements Nome, EmailUsuario, Senha {
            private String nome;
            private String email;
            @lombok.ToString.Exclude
            private String senha;
        }

        @Data
        public static class Login implements EmailUsuario, Senha {
            private String email;
            @lombok.ToString.Exclude
            private String senha;
        }
    }

    public enum Response {;
        @Data
        public static class Usuario implements Nome, EmailUsuario {
            private final Long id;
            private final String nome;
            private final String email;
        }

        @Data
        public static class Sessao {
            private final String idSessao;
            private final Usuario usuario;
        }
    }
}
