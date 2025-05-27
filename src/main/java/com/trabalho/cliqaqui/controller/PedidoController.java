package com.trabalho.cliqaqui.controller;

import com.trabalho.cliqaqui.dto.PedidoDTO;
import com.trabalho.cliqaqui.model.Pedido;
import com.trabalho.cliqaqui.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping // Finaliza o pedido [cite: 62]
    public ResponseEntity<Pedido> realizarPedido(@RequestBody PedidoDTO pedidoDTO) {
        Pedido novoPedido = pedidoService.processarPedido(pedidoDTO);
        return new ResponseEntity<>(novoPedido, HttpStatus.CREATED);
    }

    @GetMapping("/cliente/{userId}") // Permite ao cliente e vendedor visualizar seus pedidos [cite: 33]
    public ResponseEntity<List<Pedido>> getPedidosByCliente(@PathVariable Long userId) {
        List<Pedido> pedidos = pedidoService.getPedidosByUserId(userId);
        return ResponseEntity.ok(pedidos);
    }
}