package com.loja.service;

import com.loja.dto.RequestDTOs.ProdutoRequest;
import com.loja.entity.Categoria;
import com.loja.entity.Produto;
import com.loja.repository.CategoriaRepository;
import com.loja.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository repository;
    private final CategoriaRepository categoriaRepository;

    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    public List<Produto> listarAtivos() {
        return repository.findByAtivoTrue();
    }

    public Produto buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));
    }

    public List<Produto> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome);
    }

    public List<Produto> listarEstoqueBaixo() {
        return repository.findProdutosEstoqueBaixo();
    }

    @Transactional
    public Produto criar(ProdutoRequest request) {
        Categoria categoria = null;
        if (request.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        }

        Produto produto = Produto.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .codigo(request.getCodigo())
                .precoCusto(request.getPrecoCusto())
                .precoVenda(request.getPrecoVenda())
                .quantidadeEstoque(request.getQuantidadeEstoque())
                .estoqueMinimo(request.getEstoqueMinimo() != null ? request.getEstoqueMinimo() : 5)
                .categoria(categoria)
                .imagemUrl(request.getImagemUrl())
                .ativo(true)
                .build();
        return repository.save(produto);
    }

    @Transactional
    public Produto atualizar(Long id, ProdutoRequest request) {
        Produto produto = buscarPorId(id);

        Categoria categoria = null;
        if (request.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        }

        produto.setNome(request.getNome());
        produto.setDescricao(request.getDescricao());
        produto.setCodigo(request.getCodigo());
        produto.setPrecoCusto(request.getPrecoCusto());
        produto.setPrecoVenda(request.getPrecoVenda());
        produto.setQuantidadeEstoque(request.getQuantidadeEstoque());
        produto.setEstoqueMinimo(request.getEstoqueMinimo() != null ? request.getEstoqueMinimo() : 5);
        produto.setCategoria(categoria);
        produto.setImagemUrl(request.getImagemUrl());
        return repository.save(produto);
    }

    @Transactional
    public void alternarStatus(Long id) {
        Produto produto = buscarPorId(id);
        produto.setAtivo(!produto.getAtivo());
        repository.save(produto);
    }
}
