package com.loja.service;

import com.loja.dto.DashboardDTO;
import com.loja.dto.RequestDTOs.*;
import com.loja.entity.*;
import com.loja.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;

    public List<Venda> listarTodas() {
        return vendaRepository.findAll();
    }

    public Venda buscarPorId(Long id) {
        return vendaRepository.findByIdComItens(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada: " + id));
    }

    public List<Venda> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return vendaRepository.findByDataVendaBetween(inicio, fim);
    }

    @Transactional
    public Venda realizarVenda(VendaRequest request) {
        Venda venda = new Venda();
        venda.setNumeroVenda(gerarNumeroVenda());
        venda.setNomeCliente(request.getNomeCliente());
        venda.setCpfCliente(request.getCpfCliente());
        venda.setFormaPagamento(Venda.FormaPagamento.valueOf(request.getFormaPagamento()));
        venda.setObservacao(request.getObservacao());
        venda.setDesconto(request.getDesconto() != null ? request.getDesconto() : BigDecimal.ZERO);
        venda.setStatus(Venda.StatusVenda.FINALIZADA);
        venda.setDataVenda(LocalDateTime.now());

        for (ItemVendaRequest itemReq : request.getItens()) {
            Produto produto = produtoRepository.findById(itemReq.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemReq.getProdutoId()));

            if (!produto.getAtivo()) {
                throw new RuntimeException("Produto inativo: " + produto.getNome());
            }

            if (produto.getQuantidadeEstoque() < itemReq.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para: " + produto.getNome()
                        + " (disponível: " + produto.getQuantidadeEstoque() + ")");
            }

            ItemVenda item = ItemVenda.builder()
                    .venda(venda)
                    .produto(produto)
                    .quantidade(itemReq.getQuantidade())
                    .precoUnitario(produto.getPrecoVenda())
                    .subtotal(produto.getPrecoVenda().multiply(BigDecimal.valueOf(itemReq.getQuantidade())))
                    .build();

            venda.getItens().add(item);

            // Baixa no estoque
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - itemReq.getQuantidade());
            produtoRepository.save(produto);
        }

        venda.calcularTotais();
        return vendaRepository.save(venda);
    }

    @Transactional
    public Venda cancelarVenda(Long id) {
        Venda venda = buscarPorId(id);

        if (venda.getStatus() == Venda.StatusVenda.CANCELADA) {
            throw new RuntimeException("Venda já está cancelada");
        }

        // Estornar estoque
        for (ItemVenda item : venda.getItens()) {
            Produto produto = item.getProduto();
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade());
            produtoRepository.save(produto);
        }

        venda.setStatus(Venda.StatusVenda.CANCELADA);
        return vendaRepository.save(venda);
    }

    public DashboardDTO getDashboard() {
        LocalDateTime inicioHoje = LocalDate.now().atStartOfDay();
        LocalDateTime fimHoje = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        List<Venda> ultimasVendas = vendaRepository
                .findTop10ByStatusOrderByDataVendaDesc(Venda.StatusVenda.FINALIZADA);

        List<DashboardDTO.VendaResumo> resumos = ultimasVendas.stream()
                .map(v -> DashboardDTO.VendaResumo.builder()
                        .id(v.getId())
                        .numeroVenda(v.getNumeroVenda())
                        .nomeCliente(v.getNomeCliente())
                        .valorFinal(v.getValorFinal())
                        .formaPagamento(v.getFormaPagamento().name())
                        .dataVenda(v.getDataVenda().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                        .build())
                .collect(Collectors.toList());

        BigDecimal valorEstoque = produtoRepository.calcularValorTotalEstoque();

        return DashboardDTO.builder()
                .totalProdutos(produtoRepository.countAtivos())
                .totalVendasHoje(vendaRepository.countVendasPeriodo(inicioHoje, fimHoje))
                .totalVendas(vendaRepository.countFinalizadas())
                .faturamentoHoje(vendaRepository.calcularFaturamento(inicioHoje, fimHoje))
                .faturamentoMes(vendaRepository.calcularFaturamento(inicioMes, fimHoje))
                .valorEstoque(valorEstoque != null ? valorEstoque : BigDecimal.ZERO)
                .produtosEstoqueBaixo((long) produtoRepository.findProdutosEstoqueBaixo().size())
                .ultimasVendas(resumos)
                .build();
    }

    private String gerarNumeroVenda() {
        String prefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "V" + prefix + "-" + suffix;
    }
}
