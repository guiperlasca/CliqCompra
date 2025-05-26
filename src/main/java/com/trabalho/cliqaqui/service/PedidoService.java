package com.trabalho.cliqaqui.service;

import com.trabalho.cliqaqui.model.Cliente;
import com.trabalho.cliqaqui.model.Pedido;
import com.trabalho.cliqaqui.repositories.ClienteRepository;
import com.trabalho.cliqaqui.repositories.PedidoRepository;
import com.trabalho.cliqaqui.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository; // For stock validation, etc.
    private final ClienteRepository clienteRepository; // To fetch cliente for listing pedidos

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository,
                         ProdutoRepository produtoRepository,
                         ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
    }

    public Pedido criarPedido(Pedido pedido) {
        // Placeholder for business logic:
        // - Validate stock of products in pedido.getItens() using produtoRepository
        // - Calculate valorTotal
        // - Set status, dataRealizacao
        // - ...
        return pedidoRepository.save(pedido);
    }

    public Optional<Pedido> buscarPedidoPorId(Integer id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> listarPedidosPorCliente(Integer clienteId) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(clienteId);
        if (clienteOptional.isPresent()) {
            // Assuming Pedido has a direct or indirect way to query by Cliente ID
            // This might require a custom query in PedidoRepository, e.g., findByClienteId(Integer clienteId)
            // For now, if Cliente entity holds a list of its pedidos:
             return clienteOptional.get().getPedidos();
        }
        return Collections.emptyList(); // Or throw exception
    }

    // Additional methods for PedidoService as needed, e.g.,
    // public Pedido adicionarItemAoPedido(Integer pedidoId, Integer produtoId, int quantidade)
    // public Pedido atualizarStatusPedido(Integer pedidoId, StatusPedido novoStatus)
}
