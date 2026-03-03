package com.loja.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_venda", unique = true, nullable = false, length = 20)
    private String numeroVenda;

    @Column(length = 150)
    private String nomeCliente;

    @Column(length = 14)
    private String cpfCliente;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVenda> itens = new ArrayList<>();

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @Column(precision = 12, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorFinal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusVenda status = StatusVenda.FINALIZADA;

    @Column(length = 500)
    private String observacao;

    @Column(name = "data_venda", nullable = false)
    private LocalDateTime dataVenda;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (dataVenda == null) {
            dataVenda = LocalDateTime.now();
        }
    }

    public void calcularTotais() {
        this.valorTotal = itens.stream()
                .map(ItemVenda::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.valorFinal = this.valorTotal.subtract(
                this.desconto != null ? this.desconto : BigDecimal.ZERO);
    }

    public enum FormaPagamento {
        DINHEIRO, PIX, CARTAO_CREDITO, CARTAO_DEBITO
    }

    public enum StatusVenda {
        FINALIZADA, CANCELADA
    }
}
