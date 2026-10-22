package br.com.nataliasantos.distributedstockexchangesimulator.domain.entity.ativo;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ativo", uniqueConstraints = @UniqueConstraint(name = "uk_ativo_ticker", columnNames = "ticker"))
public class Ativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false, length = 20)
    private String ticker;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "preco_inicial", nullable = false, precision = 19, scale = 2)
    private BigDecimal precoInicial;

    @Column(name = "preco_atual", nullable = false, precision = 19, scale = 2)
    private BigDecimal precoAtual;

    @Column(name = "quantidade_total", nullable = false)
    private Integer quantidadeTotal;

    public Ativo(String ticker, String nome, BigDecimal precoInicial, Integer quantidadeTotal) {
        this.ticker = ticker;
        this.nome = nome;
        this.precoInicial = precoInicial;
        this.precoAtual = precoInicial;
        this.quantidadeTotal = quantidadeTotal;
    }
}
