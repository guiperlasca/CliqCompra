package com.trabalho.cliqaqui.service;

import com.trabalho.cliqaqui.dto.ProdutoDTO; // Novo DTO para Produto
import com.trabalho.cliqaqui.dto.ProdutoMinDTO;
import com.trabalho.cliqaqui.model.Categoria;
import com.trabalho.cliqaqui.model.Produto; // Renomeado de CliqModel para Produto
import com.trabalho.cliqaqui.repositories.CategoriaRepository;
import com.trabalho.cliqaqui.repositories.ProdutoRepository; // Renomeado de CliqRepository para ProdutoRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository; // Renomeado
    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<ProdutoMinDTO> findAll() {
        return produtoRepository.findAll().stream().map(ProdutoMinDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public ProdutoDTO cadastrarProduto(ProdutoDTO dto) { // Permite cadastro de produtos [cite: 13]
        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());

        Set<Categoria> categorias = new HashSet<>();
        if (dto.getCategoriasIds() != null) {
            for (Long categoriaId : dto.getCategoriasIds()) {
                Categoria categoria = categoriaRepository.findById(categoriaId)
                        .orElseThrow(() -> new RuntimeException("Categoria não encontrada com ID: " + categoriaId));
                categorias.add(categoria);
            }
        }
        produto.setCategorias(categorias); // Um produto pode pertencer a uma ou mais categorias [cite: 16]

        produto = produtoRepository.save(produto);
        return new ProdutoDTO(produto); // Retorna o DTO do produto cadastrado
    }

    @Transactional(readOnly = true)
    public List<ProdutoMinDTO> buscarProdutos(String nome, String categoria) { // Permite busca de produtos por nome e categoria [cite: 35]
        if (nome != null && !nome.isEmpty()) {
            return produtoRepository.findByNomeContainingIgnoreCase(nome).stream().map(ProdutoMinDTO::new).collect(Collectors.toList());
        } else if (categoria != null && !categoria.isEmpty()) {
            return produtoRepository.findByCategoriaNome(categoria).stream().map(ProdutoMinDTO::new).collect(Collectors.toList());
        }
        return findAll();
    }
}