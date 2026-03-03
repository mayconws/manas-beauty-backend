package com.loja.repository;

import com.loja.entity.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    Optional<Venda> findByNumeroVenda(String numeroVenda);

    List<Venda> findByDataVendaBetween(LocalDateTime inicio, LocalDateTime fim);

    List<Venda> findByStatus(Venda.StatusVenda status);

    @Query("SELECT COALESCE(SUM(v.valorFinal), 0) FROM Venda v WHERE v.status = 'FINALIZADA' AND v.dataVenda BETWEEN :inicio AND :fim")
    BigDecimal calcularFaturamento(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COUNT(v) FROM Venda v WHERE v.status = 'FINALIZADA' AND v.dataVenda BETWEEN :inicio AND :fim")
    Long countVendasPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COUNT(v) FROM Venda v WHERE v.status = 'FINALIZADA'")
    Long countFinalizadas();

    @Query("SELECT v FROM Venda v LEFT JOIN FETCH v.itens WHERE v.id = :id")
    Optional<Venda> findByIdComItens(@Param("id") Long id);

    List<Venda> findTop10ByStatusOrderByDataVendaDesc(Venda.StatusVenda status);
}
