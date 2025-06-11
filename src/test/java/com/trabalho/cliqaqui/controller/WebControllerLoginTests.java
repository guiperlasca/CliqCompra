package com.trabalho.cliqaqui.controller;

import com.trabalho.cliqaqui.model.Usuario;
import com.trabalho.cliqaqui.model.Cliente;
import com.trabalho.cliqaqui.repositories.CategoriaRepository; // Added
import com.trabalho.cliqaqui.repositories.UsuarioRepository;
import com.trabalho.cliqaqui.service.PasswordService;
import com.trabalho.cliqaqui.service.PedidoService; // Added
import com.trabalho.cliqaqui.service.ProdutoService; // Added
import com.trabalho.cliqaqui.service.ShoppingCartService;
import com.trabalho.cliqaqui.service.UsuarioService; // Added
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest; // Changed
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
// Removed @SpringBootTest import, kept MockMvcResultMatchers

import java.util.Optional;

// Removed static import for eq, anyString if not used elsewhere, or keep if preferred
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebController.class) // Changed from @SpringBootTest
public class WebControllerLoginTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private PasswordService passwordService;

    @MockBean
    private ShoppingCartService shoppingCartService;

    // Add other MockBeans required by WebController constructor
    @MockBean
    private ProdutoService produtoService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private PedidoService pedidoService;

    @MockBean
    private CategoriaRepository categoriaRepository;

    @Test
    public void testProcessLogin_Success() throws Exception {
        Usuario mockUser = new Cliente();
        mockUser.setId(1);
        mockUser.setEmail("test@example.com");
        mockUser.setNome("Test User");
        mockUser.setSenhaHash("hashedPassword");
        // Ensure this user type is consistent with what WebController expects/sets
        // If WebController sets "CLIENTE", this mock setup is fine.

        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(passwordService.checkPassword("password123", "hashedPassword")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .param("email", "test@example.com")
                .param("senha", "password123"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/home"))
            .andExpect(request().sessionAttribute("loggedInUserId", 1))
            .andExpect(request().sessionAttribute("loggedInUserEmail", "test@example.com"))
            .andExpect(request().sessionAttribute("loggedInUserName", "Test User"))
            .andExpect(request().sessionAttribute("loggedInUserType", "CLIENTE")) // Assuming Cliente maps to "CLIENTE"
            .andExpect(flash().attribute("successMessage", "Login realizado com sucesso! Bem-vindo(a) Test User"));
    }

    @Test
    public void testProcessLogin_IncorrectPassword() throws Exception {
        Usuario mockUser = new Cliente(); // Or any Usuario subtype
        mockUser.setId(1);
        mockUser.setEmail("test@example.com");
        mockUser.setSenhaHash("hashedPassword");

        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(passwordService.checkPassword("wrongpassword", "hashedPassword")).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .param("email", "test@example.com")
                .param("senha", "wrongpassword"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"))
            .andExpect(request().sessionAttributeDoesNotExist("loggedInUserId"))
            .andExpect(flash().attribute("loginError", "Email ou senha inválidos."));
    }

    @Test
    public void testProcessLogin_UserNotFound() throws Exception {
        when(usuarioRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .param("email", "nonexistent@example.com")
                .param("senha", "password123"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"))
            .andExpect(request().sessionAttributeDoesNotExist("loggedInUserId"))
            .andExpect(flash().attribute("loginError", "Email ou senha inválidos."));
    }
}
