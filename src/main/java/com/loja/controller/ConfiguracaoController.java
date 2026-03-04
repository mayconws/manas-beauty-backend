package com.loja.controller;

import com.loja.dto.RequestDTOs.PixConfigRequest;
import com.loja.entity.PixConfig;
import com.loja.service.PixConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configuracoes")
@RequiredArgsConstructor
public class ConfiguracaoController {

    private final PixConfigService pixConfigService;

    @GetMapping("/pix")
    public ResponseEntity<PixConfig> buscarPix() {
        return ResponseEntity.ok(pixConfigService.buscar());
    }

    @PutMapping("/pix")
    public ResponseEntity<PixConfig> salvarPix(@RequestBody PixConfigRequest request) {
        return ResponseEntity.ok(pixConfigService.salvar(request));
    }
}
