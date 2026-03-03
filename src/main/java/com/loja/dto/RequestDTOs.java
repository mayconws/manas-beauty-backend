package com.loja.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

public class RequestDTOs {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CategoriaRequest {
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100)
        private String nome;

        @Size(max = 255)
        private String descricao;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ProdutoRequest {
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150)
        private String nome;

        @Size(max = 500)
        private String descricao;

        @Size(max = 50)
        private String codigo;

        @NotNull(message = "Preço de custo é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço de custo deve ser maior que zero")
        private BigDecimal precoCusto;

        @NotNull(message = "Preço de venda é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço de venda deve ser maior que zero")
        private BigDecimal precoVenda;

        @NotNull(message = "Quantidade em estoque é obrigatória")
        @Min(value = 0, message = "Quantidade não pode ser negativa")
        private Integer quantidadeEstoque;

        @Min(value = 0)
        private Integer estoqueMinimo = 5;

        private Long categoriaId;

        private String imagemUrl;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class VendaRequest {
        @Size(max = 150)
        private String nomeCliente;

        @Size(max = 14)
        private String cpfCliente;

        @NotEmpty(message = "A venda deve ter pelo menos um item")
        @Valid
        private List<ItemVendaRequest> itens;

        private BigDecimal desconto = BigDecimal.ZERO;

        @NotNull(message = "Forma de pagamento é obrigatória")
        private String formaPagamento;

        @Size(max = 500)
        private String observacao;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ItemVendaRequest {
        @NotNull(message = "Produto é obrigatório")
        private Long produtoId;

        @NotNull(message = "Quantidade é obrigatória")
        @Min(value = 1, message = "Quantidade deve ser pelo menos 1")
        private Integer quantidade;
    }
}
