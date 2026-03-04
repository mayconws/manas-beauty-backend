package com.loja.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pix_config")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PixConfig {

    @Id
    private Long id = 1L;

    @Column(length = 20)
    private String tipoChave;

    @Column(length = 140)
    private String chave;

    @Column(name = "nome_recebedor", length = 100)
    private String nomeRecebedor;

    @Column(length = 50)
    private String cidade;
}
