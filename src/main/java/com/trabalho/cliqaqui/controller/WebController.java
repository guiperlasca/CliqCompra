package com.trabalho.cliqaqui.controller;

import com.trabalho.cliqaqui.dto.UsuarioDTO;
import com.trabalho.cliqaqui.model.Produto;
import com.trabalho.cliqaqui.model.Usuario;
import com.trabalho.cliqaqui.service.ProdutoService;
import com.trabalho.cliqaqui.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class WebController {

    private final ProdutoService produtoService;
    private final UsuarioService usuarioService;

    @Autowired
    public WebController(ProdutoService produtoService, UsuarioService usuarioService) {
        this.produtoService = produtoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping({"/", "/home"})
    public String home() {
        return "home"; // This refers to home.html
    }

    @GetMapping("/registrar")
    public String registrar(Model model) {
        model.addAttribute("usuarioDto", new UsuarioDTO());
        return "registrar"; // Renders registrar.html
    }

    @PostMapping("/registrar")
    public String processRegistration(@ModelAttribute("usuarioDto") UsuarioDTO usuarioDto, Model model /*, RedirectAttributes redirectAttributes */) {
        // TODO: Add validation for usuarioDto (e.g., using @Valid and BindingResult)

        // Convert DTO to Entity
        // FIXME: This will cause an error because Usuario is abstract.
        // We need to decide whether to register a Cliente or Vendedor, or change Usuario to be concrete.
        // For now, let's assume we are registering a Cliente.
        // This part will need to be adjusted based on actual registration logic (e.g., if it's always a Cliente).
        Usuario novoUsuario = new com.trabalho.cliqaqui.model.Cliente(); // Example: creating a Cliente instance
        novoUsuario.setNome(usuarioDto.getNome());
        novoUsuario.setEmail(usuarioDto.getEmail());
        // FIXME: Store plain text password - INSECURE! Implement password hashing.
        novoUsuario.setSenhaHash(usuarioDto.getSenha()); // Storing plain text password temporarily
        novoUsuario.setCpfCnpj(usuarioDto.getCpfCnpj());
        // novoUsuario.setTelefones(new ArrayList<>()); // Initialize if needed, or handle from DTO if field was present

        try {
            usuarioService.salvarUsuario(novoUsuario);
            // redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.");
            return "redirect:/login"; // Redirect to login page on success
        } catch (Exception e) {
            // TODO: More specific error handling (e.g., for duplicate email)
            // For now, just re-render the form with a generic error message
            // model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            model.addAttribute("errorMessage", "Registration failed. Please try again."); // Simplified
            model.addAttribute("usuarioDto", usuarioDto); // Send the DTO back to repopulate the form
            return "registrar"; // Return to registration page with error
        }
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
