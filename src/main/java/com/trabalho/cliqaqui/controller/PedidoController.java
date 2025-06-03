package com.trabalho.cliqaqui.controller;

import com.trabalho.cliqaqui.model.Pedido;
import com.trabalho.cliqaqui.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @Autowired
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@RequestBody Pedido pedido) {
        // Simplified: In a real scenario, this would involve more complex logic
        // e.g., setting the authenticated user as the cliente, validating products, etc.
        Pedido novoPedido = pedidoService.criarPedido(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPedidoPorId(@PathVariable Integer id) {
        return pedidoService.buscarPedidoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> listarPedidosPorCliente(@PathVariable Integer clienteId) {
        List<Pedido> pedidos = pedidoService.listarPedidosPorCliente(clienteId);
        if (pedidos.isEmpty()) {
            // You might return notFound if the client doesn't exist or has no orders,
            // or ok with an empty list depending on desired behavior.
            return ResponseEntity.ok(pedidos); // Returning OK with empty list for now
        }
        return ResponseEntity.ok(pedidos);
    }
}
