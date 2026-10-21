package br.com.nataliasantos.distributedstockexchangesimulator.domain.entity;

import java.math.BigDecimal;
import br.com.nataliasantos.distributedstockexchangesimulator.core.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "usuario", uniqueConstraints = @UniqueConstraint(name = "uk_usuario_email", columnNames = "email"))
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoUsuario tipo;

    @Column(name = "saldo_caixa", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoCaixa = BigDecimal.ZERO;

    @Column(name = "saldo_reservado", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoReservado = BigDecimal.ZERO;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.PERSIST, optional = false)
    @Setter(AccessLevel.NONE)
    private Carteira carteira;

    public Usuario(String nome, String email, String senhaHash, TipoUsuario tipo) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.tipo = tipo;
        this.carteira = new Carteira(this);
    }
}
