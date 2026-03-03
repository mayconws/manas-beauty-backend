package com.loja.repository;

import com.loja.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByAtivoTrue();

    Optional<Produto> findByCodigo(String codigo);

    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);

    List<Produto> findByCategoriaIdAndAtivoTrue(Long categoriaId);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.quantidadeEstoque <= p.estoqueMinimo")
    List<Produto> findProdutosEstoqueBaixo();

    @Query("SELECT COUNT(p) FROM Produto p WHERE p.ativo = true")
    Long countAtivos();

    @Query("SELECT SUM(p.quantidadeEstoque * p.precoCusto) FROM Produto p WHERE p.ativo = true")
    java.math.BigDecimal calcularValorTotalEstoque();
}
