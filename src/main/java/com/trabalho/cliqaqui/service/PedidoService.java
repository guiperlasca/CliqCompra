package com.trabalho.cliqaqui.service;

import com.trabalho.cliqaqui.dto.*;
import com.trabalho.cliqaqui.model.*;
import com.trabalho.cliqaqui.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    @Autowired private PedidoRepository pedidoRepo;
    @Autowired private ProdutoRepository produtoRepo; // Renomeado de CliqRepository para ProdutoRepository
    @Autowired private UserRepository userRepo;
    @Autowired private EnderecoRepository enderecoRepo;
    @Autowired private PagamentoService pagamentoService; // Injetar o serviço de pagamento

    public Pedido processarPedido(PedidoDTO dto) {
        Pedido pedido = new Pedido();
        pedido.setDataRealizacao(LocalDateTime.now()); // Registra o instante da realização do pedido [cite: 22]
        pedido.setStatus(StatusPedido.PENDENTE); // Define status inicial como pendente

        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + dto.getUserId()));
        pedido.setCliente(user);

        Endereco endereco = enderecoRepo.findById(dto.getEnderecoId())
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado com ID: " + dto.getEnderecoId()));
        pedido.setEnderecoEntrega(endereco); // Permite ao cliente escolher um endereço de entrega [cite: 24]

        List<ItemPedido> itens = new ArrayList<>();
        for (ItemPedidoDTO itemDTO : dto.getItens()) {
            Produto produto = produtoRepo.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + itemDTO.getProdutoId()));
            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(itemDTO.getQuantidade()); // Adiciona produtos ao carrinho com quantidade desejada [cite: 18]
            item.setPrecoUnitarioMomentoCompra(produto.getPreco());
            item.setDescontoAplicado(itemDTO.getDescontoAplicado()); // Permite aplicar desconto individual por produto [cite: 20]
            item.setPedido(pedido);
            itens.add(item);
        }
        pedido.setItens(itens);
        pedido.calcularTotal(); // Calcula o total do pedido

        // Processa o pagamento
        Pagamento pagamento = pagamentoService.processarPagamento(pedido, dto.getFormaPagamento(), dto.getNumeroParcelas());
        pedido.setPagamento(pagamento);
        pedido.setStatus(StatusPedido.PROCESSANDO); // Após o pagamento, o pedido está em processamento

        return pedidoRepo.save(pedido); // Salva o pedido completo
    }

    public List<Pedido> getPedidosByUserId(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        return pedidoRepo.findByCliente(user); // Permite ao cliente e vendedor visualizar seus pedidos [cite: 33]
    }
}