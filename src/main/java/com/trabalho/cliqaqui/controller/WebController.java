package com.trabalho.cliqaqui.controller;

import com.trabalho.cliqaqui.model.Produto;
import com.trabalho.cliqaqui.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class WebController {

    private final ProdutoService produtoService;

    @Autowired
    public WebController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping({"/", "/home"})
    public String home() {
        return "home"; // This refers to home.html
    }

    @GetMapping("/registrar")
    public String registrar() {
        return "registrar";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/produtos")
    public String produtos(Model model) {
        List<Produto> productList = produtoService.listarTodosProdutos();
        model.addAttribute("produtos", productList);
        return "produtos";
    }
}
