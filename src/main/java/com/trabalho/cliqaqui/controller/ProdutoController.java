package com.trabalho.cliqaqui.controller;

import com.trabalho.cliqaqui.dto.ProdutoDTO; // Novo DTO
import com.trabalho.cliqaqui.dto.ProdutoMinDTO;
import com.trabalho.cliqaqui.service.ProdutoService; // Renomeado
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService; // Renomeado

    @GetMapping // Permite buscar produtos por nome e categoria
    public List<ProdutoMinDTO> getAll(@RequestParam(required = false) String nome,
                                      @RequestParam(required = false) String categoria) {
        return produtoService.buscarProdutos(nome, categoria);
    }

    @PostMapping // Permite o cadastro de produtos
    public ResponseEntity<ProdutoDTO> cadastrarProduto(@RequestBody ProdutoDTO dto) {
        ProdutoDTO novoProduto = produtoService.cadastrarProduto(dto);
        return new ResponseEntity<>(novoProduto, HttpStatus.CREATED);
    }
}