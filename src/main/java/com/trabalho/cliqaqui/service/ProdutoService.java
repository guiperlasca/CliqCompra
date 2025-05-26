package com.trabalho.cliqaqui.service;

import com.trabalho.cliqaqui.model.Produto;
import com.trabalho.cliqaqui.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    @Autowired
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto salvarProduto(Produto produto) {
        // Add any business logic before saving
        return produtoRepository.save(produto);
    }

    public Optional<Produto> buscarProdutoPorId(Integer id) {
        return produtoRepository.findById(id);
    }

    public List<Produto> listarTodosProdutos() {
        return produtoRepository.findAll();
    }

    public void deletarProduto(Integer id) {
        produtoRepository.deleteById(id);
    }
}
