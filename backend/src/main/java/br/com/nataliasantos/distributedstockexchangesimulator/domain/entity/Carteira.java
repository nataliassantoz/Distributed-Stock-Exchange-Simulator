package br.com.nataliasantos.distributedstockexchangesimulator.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "carteira", uniqueConstraints = @UniqueConstraint(name = "uk_carteira_usuario", columnNames = "usuario_id"))
public class Carteira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_carteira_usuario"))
    private Usuario usuario;

    Carteira(Usuario usuario) {
        this.usuario = usuario;
    }
}
