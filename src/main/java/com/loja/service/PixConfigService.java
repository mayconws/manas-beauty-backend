package com.loja.service;

import com.loja.dto.RequestDTOs.PixConfigRequest;
import com.loja.entity.PixConfig;
import com.loja.repository.PixConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PixConfigService {

    private final PixConfigRepository repository;

    public PixConfig buscar() {
        return repository.findById(1L).orElse(new PixConfig(1L, null, null, null, null));
    }

    @Transactional
    public PixConfig salvar(PixConfigRequest request) {
        PixConfig config = repository.findById(1L).orElse(new PixConfig());
        config.setId(1L);
        config.setTipoChave(request.getTipoChave());
        config.setChave(request.getChave());
        config.setNomeRecebedor(request.getNomeRecebedor());
        config.setCidade(request.getCidade());
        return repository.save(config);
    }
}
