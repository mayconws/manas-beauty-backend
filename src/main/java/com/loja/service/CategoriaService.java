package com.loja.service;

import com.loja.dto.RequestDTOs.CategoriaRequest;
import com.loja.entity.Categoria;
import com.loja.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;

    public List<Categoria> listarTodas() {
        return repository.findAll();
    }

    public List<Categoria> listarAtivas() {
        return repository.findByAtivaTrue();
    }

    public Categoria buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada: " + id));
    }

    @Transactional
    public Categoria criar(CategoriaRequest request) {
        Categoria categoria = Categoria.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .ativa(true)
                .build();
        return repository.save(categoria);
    }

    @Transactional
    public Categoria atualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscarPorId(id);
        categoria.setNome(request.getNome());
        categoria.setDescricao(request.getDescricao());
        return repository.save(categoria);
    }

    @Transactional
    public void alternarStatus(Long id) {
        Categoria categoria = buscarPorId(id);
        categoria.setAtiva(!categoria.getAtiva());
        repository.save(categoria);
    }
}
