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

    public List<Produto> listarProdutosPorUsuario(Integer usuarioId) {
        return produtoRepository.findByUsuarioId(usuarioId);
    }

    public List<Produto> listarProdutosPorCategoria(Integer categoriaId) {
        // The controller should ensure categoriaId is not null if filtering is intended.
        // If categoriaId is null here, findByCategorias_Id might behave unexpectedly
        // or throw an error depending on DB/JPA provider if the generated query is invalid.
        // It's safer if the controller handles the "all products" case explicitly.
        // Assuming categoriaId will be non-null if this method is called for filtering.
        return produtoRepository.findByCategorias_Id(categoriaId);
    }
}
