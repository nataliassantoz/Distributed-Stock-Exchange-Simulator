package br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.ordem;

import br.com.nataliasantos.distributedstockexchangesimulator.core.enums.StatusOrdem;
import br.com.nataliasantos.distributedstockexchangesimulator.core.enums.TipoOrdem;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.ativo.Ativo;
import br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Entity
@Table(name = "ordem")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ordem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario investidor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ativo_id", nullable = false)
    private Ativo ativo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 6)
    private TipoOrdem tipo;

    @Column(name = "quantidade_original", nullable = false)
    private Integer quantidade;

    @Column(name = "quantidade_restante", nullable = false)
    private Integer quantidadeRestante;

    @Column(name = "preco_limite", nullable = false, precision = 19, scale = 2)
    private BigDecimal preco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusOrdem status;

    @Column(name = "criada_em", nullable = false)
    private Instant dataHora;

    public Ordem(Usuario investidor, Ativo ativo, TipoOrdem tipo, Integer quantidade, BigDecimal preco) {
        this.investidor = investidor;
        this.ativo = ativo;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.quantidadeRestante = quantidade;
        this.preco = preco;
        this.status = StatusOrdem.PENDENTE;
        this.dataHora = Instant.now();
    }
}
