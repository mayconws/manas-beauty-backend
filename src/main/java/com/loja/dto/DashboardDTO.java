package com.loja.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardDTO {
    private Long totalProdutos;
    private Long totalVendasHoje;
    private Long totalVendas;
    private BigDecimal faturamentoHoje;
    private BigDecimal faturamentoMes;
    private BigDecimal valorEstoque;
    private Long produtosEstoqueBaixo;
    private List<VendaResumo> ultimasVendas;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class VendaResumo {
        private Long id;
        private String numeroVenda;
        private String nomeCliente;
        private BigDecimal valorFinal;
        private String formaPagamento;
        private String dataVenda;
    }
}
