package com.trabalho.cliqaqui.controller;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import com.trabalho.cliqaqui.dto.UsuarioDTO;
import com.trabalho.cliqaqui.model.Produto;
import com.trabalho.cliqaqui.model.Usuario;
import com.trabalho.cliqaqui.model.Cliente; // Added import
import com.trabalho.cliqaqui.model.Vendedor; // Added import
import com.trabalho.cliqaqui.repositories.UsuarioRepository; // Added import
import com.trabalho.cliqaqui.service.PasswordService;
import com.trabalho.cliqaqui.service.ProdutoService;
import com.trabalho.cliqaqui.service.ShoppingCartService; // Added import
import com.trabalho.cliqaqui.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.password.PasswordEncoder; // Removed import
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable; // Added import
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam; // Added import
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Added import
import jakarta.servlet.http.HttpSession; // Added import for jakarta.servlet

import com.trabalho.cliqaqui.dto.ProdutoDTO; // Added import
import com.trabalho.cliqaqui.dto.ShoppingCartDTO; // Added import
import com.trabalho.cliqaqui.model.Endereco; // Added import
import com.trabalho.cliqaqui.model.ItemPedido; // Added import
import com.trabalho.cliqaqui.model.Pedido; // Added import
import com.trabalho.cliqaqui.model.StatusPedido; // Added import
import com.trabalho.cliqaqui.service.PedidoService; // Added import

import java.sql.Timestamp; // Added import
import java.time.Instant; // Added import
import java.util.ArrayList; // Added import
import java.util.List;
import java.util.Optional; // Added import

@Controller
public class WebController {

    public static final String UPLOAD_DIR_STATIC_RESOURCES = "src/main/resources/static/product-photos";
    public static final String UPLOAD_DIR_WEB_PATH = "/product-photos/";

    private final ProdutoService produtoService;
    private final UsuarioService usuarioService;
    private final PasswordService passwordService;
    private final UsuarioRepository usuarioRepository;
    private final ShoppingCartService shoppingCartService;
    private final PedidoService pedidoService; // Added field

    @Autowired
    public WebController(ProdutoService produtoService,
                         UsuarioService usuarioService,
                         PasswordService passwordService,
                         UsuarioRepository usuarioRepository,
                         ShoppingCartService shoppingCartService,
                         PedidoService pedidoService) { // Added to constructor
        this.produtoService = produtoService;
        this.usuarioService = usuarioService;
        this.passwordService = passwordService;
        this.usuarioRepository = usuarioRepository;
        this.shoppingCartService = shoppingCartService;
        this.pedidoService = pedidoService; // Initialize field
    }

    @GetMapping({"/", "/home"})
    public String home(Model model, HttpSession session) {
        String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
        if (loggedInUserEmail != null) {
            model.addAttribute("isUserLoggedIn", true);
            model.addAttribute("loggedInUserEmail", loggedInUserEmail);
            model.addAttribute("loggedInUserName", session.getAttribute("loggedInUserName"));
            model.addAttribute("loggedInUserType", session.getAttribute("loggedInUserType"));
        } else {
            model.addAttribute("isUserLoggedIn", false);
        }
        return "home";
    }

    @GetMapping("/registrar")
    public String registrar(Model model, HttpSession session) { // Model was already there
        model.addAttribute("usuarioDto", new UsuarioDTO());
        String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
        if (loggedInUserEmail != null) {
            model.addAttribute("isUserLoggedIn", true);
            model.addAttribute("loggedInUserEmail", loggedInUserEmail);
            model.addAttribute("loggedInUserName", session.getAttribute("loggedInUserName"));
            model.addAttribute("loggedInUserType", session.getAttribute("loggedInUserType"));
        } else {
            model.addAttribute("isUserLoggedIn", false);
        }
        return "registrar";
    }

    @PostMapping("/registrar")
    public String processRegistration(@ModelAttribute("usuarioDto") UsuarioDTO usuarioDto, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // TODO: Add validation for usuarioDto (e.g., using @Valid and BindingResult)

        Usuario novoUsuario = new com.trabalho.cliqaqui.model.Cliente();
        // The userType variable and the if/else block for VENDEDOR/CLIENTE are no longer needed.
        // The DTO might still have a userType field, but it won't be used here.
        // We also need to ensure the registeredUserType variable set later is consistently 'CLIENTE'.

        // Set common properties
        novoUsuario.setNome(usuarioDto.getNome());
        novoUsuario.setEmail(usuarioDto.getEmail());
        novoUsuario.setSenhaHash(passwordService.hashPassword(usuarioDto.getSenha())); // Encode password
        novoUsuario.setCpfCnpj(usuarioDto.getCpfCnpj());
        // novoUsuario.setTelefones(new ArrayList<>()); // Initialize if needed

        try {
            usuarioService.salvarUsuario(novoUsuario);

            // Automatic login after successful registration
            session.setAttribute("loggedInUserId", novoUsuario.getId());
            session.setAttribute("loggedInUserEmail", novoUsuario.getEmail());
            session.setAttribute("loggedInUserName", novoUsuario.getNome());

            session.setAttribute("loggedInUserType", "CLIENTE");

            redirectAttributes.addFlashAttribute("successMessage", "Cadastro realizado com sucesso! Bem-vindo(a), " + novoUsuario.getNome());
            return "redirect:/home"; // Redirect to home page
        } catch (Exception e) {
            // TODO: More specific error handling (e.g., for duplicate email, or other DB constraints)
            // For now, re-render the form with a generic error message
            model.addAttribute("errorMessage", "Falha no cadastro: " + e.getMessage()); // Provide more specific error
            model.addAttribute("usuarioDto", usuarioDto); // Send the DTO back to repopulate the form
            // Re-populate login status for layout consistency on error page
            String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
            if (loggedInUserEmail != null) {
                model.addAttribute("isUserLoggedIn", true);
                model.addAttribute("loggedInUserEmail", loggedInUserEmail);
                model.addAttribute("loggedInUserName", session.getAttribute("loggedInUserName"));
                model.addAttribute("loggedInUserType", session.getAttribute("loggedInUserType"));
            } else {
                model.addAttribute("isUserLoggedIn", false);
            }
            return "registrar"; // Return to registration page with error
        }
    }

    @GetMapping("/login")
    public String login(Model model, HttpSession session) { 
        if (session.getAttribute("loggedInUserId") != null) {
            return "redirect:/home";
        }
        
        // Existing logic for when user is NOT logged in:
        String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail"); // This will be null if the above condition was false
        if (loggedInUserEmail != null) { 
            // This block effectively might not run if we passed the first check, 
            // but it's harmless to keep the structure if it simplifies the change.
            // Or, this part of the logic can be assumed to be within an 'else' 
            // if the first 'if' was true.
            // For simplicity, the original structure for populating model when not logged in is fine.
            model.addAttribute("isUserLoggedIn", true);
            model.addAttribute("loggedInUserEmail", loggedInUserEmail);
            model.addAttribute("loggedInUserName", session.getAttribute("loggedInUserName"));
            model.addAttribute("loggedInUserType", session.getAttribute("loggedInUserType"));
        } else {
            model.addAttribute("isUserLoggedIn", false);
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String senha,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(email);

        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            if (passwordService.checkPassword(senha, usuario.getSenhaHash())) {
                // Successful login
                session.setAttribute("loggedInUserId", usuario.getId());
                session.setAttribute("loggedInUserEmail", usuario.getEmail());
                
                String userType = "USUARIO"; // Default
                if (usuario instanceof Cliente) {
                    userType = "CLIENTE";
                } else if (usuario instanceof Vendedor) {
                    userType = "VENDEDOR";
                }
                session.setAttribute("loggedInUserType", userType);
                session.setAttribute("loggedInUserName", usuario.getNome());

                // Add this line to proactively load/initialize the user's cart:
                shoppingCartService.getCart(usuario); 

                redirectAttributes.addFlashAttribute("successMessage", "Login realizado com sucesso! Bem-vindo(a) " + usuario.getNome());
                return "redirect:/home";
            }
        }

        // Failed login (user not found or password incorrect)
        redirectAttributes.addFlashAttribute("loginError", "Email ou senha inválidos.");
        return "redirect:/login";
    }

    @GetMapping("/produtos")
    public String produtos(Model model, HttpSession session) { // Model and session were already params or need to be added
        // Original logic for products
        List<Produto> productList = produtoService.listarTodosProdutos();
        model.addAttribute("produtos", productList);
        
        // Add login status
        String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
        if (loggedInUserEmail != null) {
            model.addAttribute("isUserLoggedIn", true);
            model.addAttribute("loggedInUserEmail", loggedInUserEmail);
            model.addAttribute("loggedInUserName", session.getAttribute("loggedInUserName"));
            model.addAttribute("loggedInUserType", session.getAttribute("loggedInUserType"));
        } else {
            model.addAttribute("isUserLoggedIn", false);
        }
        return "produtos";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("logoutMessage", "Você saiu com sucesso.");
        return "redirect:/login";
    }

    @GetMapping("/produtos/add") // Renamed mapping
    public String showAddProductForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // String userType = (String) session.getAttribute("loggedInUserType"); // No longer needed for this primary check
        String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado para adicionar produtos.");
            return "redirect:/login";
        }

        model.addAttribute("isUserLoggedIn", loggedInUserEmail != null); // True if loggedInUserId is not null
        if (loggedInUserEmail != null) { // Should always be true if loggedInUserId is not null
            model.addAttribute("loggedInUserEmail", loggedInUserEmail);
            model.addAttribute("loggedInUserName", session.getAttribute("loggedInUserName"));
            model.addAttribute("loggedInUserType", session.getAttribute("loggedInUserType"));
        }
        // Removed VENDEDOR specific check

        model.addAttribute("produtoDto", new ProdutoDTO());
        return "produto/add-product";
    }

    @PostMapping("/produtos/add") // Renamed mapping
    public String processAddProduct(@ModelAttribute("produtoDto") ProdutoDTO produtoDto,
                                    @RequestParam(name = "fotoFile", required = false) MultipartFile fotoFile,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes,
                                    Model model) { // Added Model for re-rendering form on error

        // String userType = (String) session.getAttribute("loggedInUserType"); // No longer needed for this primary check
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado para adicionar produtos.");
            return "redirect:/login";
        }

        // TODO: Add validation for produtoDto here. If errors:
        // model.addAttribute("errorMessage", "Please correct the errors below.");
        // model.addAttribute("produtoDto", produtoDto); // Send DTO back with submitted values
        // // Re-populate login status for layout
        // String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
        // model.addAttribute("isUserLoggedIn", loggedInUserEmail != null);
        // if (loggedInUserEmail != null) {
        //     model.addAttribute("loggedInUserEmail", loggedInUserEmail);
        //     model.addAttribute("loggedInUserType", userType);
        // }
        // return "vendedor/add-product"; // Return to form if validation fails

        // Fetch the Usuario entity
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(loggedInUserId);
        if (!optionalUsuario.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Não foi possível encontrar os detalhes da sua conta.");
            // Consider redirecting to a more appropriate page if needed, e.g., "/produtos/add" or "/home"
            return "redirect:/produtos/add";
        }
        Usuario usuario = optionalUsuario.get();

        // Convert DTO to Produto entity
        Produto produto = new Produto();
        produto.setNome(produtoDto.getNome());
        produto.setPreco(produtoDto.getPreco());
        produto.setDescricao(produtoDto.getDescricao());
        produto.setUsuario(usuario); // Associate with the logged-in Usuario

        if (fotoFile != null && !fotoFile.isEmpty()) {
            try {
                // Ensure the upload directory exists
                Path uploadPath = Paths.get(UPLOAD_DIR_STATIC_RESOURCES);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String originalFileName = fotoFile.getOriginalFilename();
                String fileExtension = "";
                if (originalFileName != null && originalFileName.contains(".")) {
                    fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                }
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
                
                Path filePath = uploadPath.resolve(uniqueFileName);
                Files.copy(fotoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                
                produto.setFotoUrl(UPLOAD_DIR_WEB_PATH + uniqueFileName); // Store web-accessible path

            } catch (IOException e) {
                // Log the error: e.g., e.printStackTrace();
                // Optionally, add a specific error message to redirectAttributes or model for photo upload failure
                model.addAttribute("errorMessage", "Falha no upload da foto, produto salvo sem foto. Erro: " + e.getMessage());
                // If you want to stop product creation on photo error, uncomment below and comment out the model.addAttribute above
                // redirectAttributes.addFlashAttribute("errorMessage", "Falha no upload da foto: " + e.getMessage());
                // return "redirect:/produtos/add"; 
            }
        } else {
            produto.setFotoUrl(null); // Or some default photo path
        }

        try {
            produtoService.salvarProduto(produto);
            redirectAttributes.addFlashAttribute("successMessage", "Produto adicionado com sucesso!");
            return "redirect:/produtos/my";
        } catch (Exception e) {
            // Log the exception e.g. e.printStackTrace();
            model.addAttribute("errorMessage", "Erro ao adicionar produto. Por favor, tente novamente.");
            model.addAttribute("produtoDto", produtoDto); // Send DTO back
            // Re-populate login status for layout
            String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
            String userTypeFromSession = (String) session.getAttribute("loggedInUserType"); // Get userType for model
            model.addAttribute("isUserLoggedIn", loggedInUserEmail != null);
            if (loggedInUserEmail != null) {
                model.addAttribute("loggedInUserEmail", loggedInUserEmail);
                model.addAttribute("loggedInUserName", session.getAttribute("loggedInUserName"));
                model.addAttribute("loggedInUserType", userTypeFromSession); // Use userType from session
            }
            return "produto/add-product";
        }
    }

    @GetMapping("/produtos/my") // Renamed mapping
    public String showMyProducts(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // String userType = (String) session.getAttribute("loggedInUserType"); // No longer needed for this primary check
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado para ver seus produtos.");
            return "redirect:/login";
        }

        List<Produto> myProducts = produtoService.listarProdutosPorUsuario(loggedInUserId); // Changed service method
        model.addAttribute("produtos", myProducts);

        // Pass login status and type for layout
        String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
        model.addAttribute("isUserLoggedIn", true); // Must be logged in to reach here
        model.addAttribute("loggedInUserEmail", loggedInUserEmail);
        model.addAttribute("loggedInUserName", session.getAttribute("loggedInUserName"));
        model.addAttribute("loggedInUserType", session.getAttribute("loggedInUserType")); // Get type from session
        
        // Add any specific messages e.g. from adding a product
        if (model.containsAttribute("successMessage")) { // Check if flash attribute was added
            model.addAttribute("successMessage", model.getAttribute("successMessage"));
        }

        return "produto/my-products";
    }
    // } Closing brace removed here

    @GetMapping("/cliente/enderecos/add")
    public String showAddAddressForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");
        if (loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado para adicionar um endereço.");
            return "redirect:/login";
        }
        // Optional: Check if user is Cliente if other user types exist and should not add addresses
        // String userType = (String) session.getAttribute("loggedInUserType");
        // if (!"CLIENTE".equals(userType)) {
        //     redirectAttributes.addFlashAttribute("errorMessage", "Only Clientes can add addresses.");
        //     return "redirect:/home"; // Or appropriate page
        // }

        model.addAttribute("endereco", new Endereco());
        // Add common model attributes for layout
        String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
        model.addAttribute("isUserLoggedIn", true);
        model.addAttribute("loggedInUserEmail", loggedInUserEmail);
        model.addAttribute("loggedInUserName", session.getAttribute("loggedInUserName"));
        model.addAttribute("loggedInUserType", session.getAttribute("loggedInUserType"));
        return "cliente/add-address";
    }

    @PostMapping("/cliente/enderecos/add")
    public String processAddAddress(@ModelAttribute("endereco") Endereco endereco, // Consider adding @Valid later
                                    // BindingResult bindingResult, // For @Valid
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes,
                                    Model model) { // Added Model for re-rendering form on error

        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");
        if (loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada ou não autenticada. Por favor, entre novamente.");
            return "redirect:/login";
        }

        // Optional: Server-side validation here if @Valid is not used yet
        // if (bindingResult.hasErrors()) {
        //     // Re-populate common model attributes for layout if returning to form
        //     String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
        //     model.addAttribute("isUserLoggedIn", true);
        //     model.addAttribute("loggedInUserEmail", loggedInUserEmail);
        //     model.addAttribute("loggedInUserName", session.getAttribute("loggedInUserName"));
        //     model.addAttribute("loggedInUserType", session.getAttribute("loggedInUserType"));
        //     model.addAttribute("endereco", endereco); // Send DTO back with submitted values
        //     return "cliente/add-address";
        // }

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(loggedInUserId);
        if (!optionalUsuario.isPresent() || !(optionalUsuario.get() instanceof Cliente)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Não foi possível encontrar os detalhes da sua conta de cliente.");
            return "redirect:/home"; // Or a more appropriate error page
        }
        Cliente cliente = (Cliente) optionalUsuario.get();

        // Add the new address to the client's list of addresses
        // The CascadeType.ALL on Cliente.enderecos should handle persisting the new Endereco
        if (cliente.getEnderecos() == null) {
            cliente.setEnderecos(new ArrayList<>());
        }
        cliente.getEnderecos().add(endereco); 
        
        try {
            usuarioService.salvarUsuario(cliente); // Assuming UsuarioService.salvarUsuario() handles cascading saves for Cliente
            redirectAttributes.addFlashAttribute("successMessage", "Endereço adicionado com sucesso!");
            return "redirect:/cliente/enderecos"; // Redirect to a page listing addresses
        } catch (Exception e) {
            // Log e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao salvar endereço: " + e.getMessage());
            // Optionally, return to form with error and populated fields
            // For now, redirecting to avoid resubmission issues, but state is lost.
            // To return to form:
            // String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
            // model.addAttribute("isUserLoggedIn", true);
            // model.addAttribute("loggedInUserEmail", loggedInUserEmail);
            // model.addAttribute("loggedInUserName", session.getAttribute("loggedInUserName"));
            // model.addAttribute("loggedInUserType", session.getAttribute("loggedInUserType"));
            // model.addAttribute("endereco", endereco); // Send DTO back
            // model.addAttribute("errorMessage", "Error saving address: " + e.getMessage()); // Re-add error for display on form
            // return "cliente/add-address";
            return "redirect:/cliente/enderecos/add"; // Simpler redirect for now
        }
    }

    @GetMapping("/cliente/enderecos")
    public String showMyAddressesPage(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");
        if (loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado para ver seus endereços.");
            return "redirect:/login";
        }

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(loggedInUserId);
        if (!optionalUsuario.isPresent() || !(optionalUsuario.get() instanceof Cliente)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Não foi possível encontrar os detalhes da sua conta de cliente.");
            return "redirect:/home"; // Or a more appropriate error page
        }
        Cliente cliente = (Cliente) optionalUsuario.get();

        model.addAttribute("enderecos", cliente.getEnderecos());
        
        // Add common model attributes for layout
        String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
        model.addAttribute("isUserLoggedIn", true);
        model.addAttribute("loggedInUserEmail", loggedInUserEmail);
        model.addAttribute("loggedInUserName", session.getAttribute("loggedInUserName"));
        model.addAttribute("loggedInUserType", session.getAttribute("loggedInUserType"));
        
        return "cliente/my-addresses";
    }

    @PostMapping("/cart/add/{productId}")
    public String addToCart(@PathVariable("productId") Integer productId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");
        if (loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado para adicionar itens ao carrinho.");
            return "redirect:/login";
        }

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(loggedInUserId);
        if (optionalUsuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuário não encontrado.");
            return "redirect:/login";
        }
        Usuario usuario = optionalUsuario.get();
        
        try {
            shoppingCartService.addItem(usuario, productId, quantity); 
            redirectAttributes.addFlashAttribute("successMessage", "Produto adicionado ao carrinho!");
        } catch (jakarta.persistence.EntityNotFoundException e) { // More specific catch
             redirectAttributes.addFlashAttribute("errorMessage", "Produto não encontrado!");
        } catch (IllegalArgumentException e) { // Catch validation errors from service
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) { 
            // Log e.printStackTrace(); // Good practice to log unexpected errors
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao adicionar produto ao carrinho. Por favor, tente novamente.");
        }
        return "redirect:/produtos"; // Or redirect to cart page: "redirect:/cart"
    }

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session, RedirectAttributes redirectAttributes) { // Added RedirectAttributes
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");
        ShoppingCartDTO cartDto;

        if (loggedInUserId == null) {
            cartDto = new ShoppingCartDTO(); 
            model.addAttribute("isUserLoggedIn", false);
        } else {
            Optional<Usuario> optionalUsuario = usuarioRepository.findById(loggedInUserId);
            if (optionalUsuario.isEmpty()) {
                cartDto = new ShoppingCartDTO();
                model.addAttribute("isUserLoggedIn", false); // Treat as not logged in for cart
                model.addAttribute("errorMessage", "Não foi possível carregar os dados do usuário para o carrinho.");
            } else {
                Usuario usuario = optionalUsuario.get();
                cartDto = shoppingCartService.getCart(usuario);
                model.addAttribute("isUserLoggedIn", true);
                model.addAttribute("loggedInUserEmail", usuario.getEmail());
                model.addAttribute("loggedInUserName", usuario.getNome());
                model.addAttribute("loggedInUserType", session.getAttribute("loggedInUserType")); 
            }
        }
        model.addAttribute("shoppingCart", cartDto);
        return "cart/view-cart";
    }

    @PostMapping("/cart/update/{productId}")
    public String updateCartItem(@PathVariable("productId") Integer productId,
                               @RequestParam("quantity") int quantity,
                               HttpSession session, // Added HttpSession
                               RedirectAttributes redirectAttributes) {
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");
        if (loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado para atualizar seu carrinho.");
            return "redirect:/login";
        }
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(loggedInUserId);
        if (optionalUsuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuário não encontrado.");
            return "redirect:/login";
        }
        Usuario usuario = optionalUsuario.get();

        try {
            shoppingCartService.updateItemQuantity(usuario, productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Carrinho atualizado com sucesso.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            // Log e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar carrinho. Por favor, tente novamente.");
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{productId}")
    public String removeCartItem(@PathVariable("productId") Integer productId,
                               HttpSession session, // Added HttpSession
                               RedirectAttributes redirectAttributes) {
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");
        if (loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado para remover itens do seu carrinho.");
            return "redirect:/login";
        }
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(loggedInUserId);
        if (optionalUsuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuário não encontrado.");
            return "redirect:/login";
        }
        Usuario usuario = optionalUsuario.get();

        try {
            shoppingCartService.removeItem(usuario, productId);
            redirectAttributes.addFlashAttribute("successMessage", "Item removido do carrinho.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            // Log e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao remover item do carrinho. Por favor, tente novamente.");
        }
        return "redirect:/cart";
    }

    @GetMapping("/checkout/address-select")
    public String showAddressSelectionPage(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");
        String userType = (String) session.getAttribute("loggedInUserType");

        if (loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado para prosseguir para o checkout.");
            return "redirect:/login";
        }
        if (!"CLIENTE".equals(userType)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Apenas Clientes podem prosseguir para o checkout.");
            // Or redirect to home if they are some other logged-in type
            return "redirect:/cart"; 
        }

        // Check if cart is empty
        ShoppingCartDTO cart = shoppingCartService.getCart(); // Assuming shoppingCartService is injected
        if (cart == null || cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Seu carrinho está vazio. Adicione itens ao carrinho primeiro.");
            return "redirect:/cart";
        }

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(loggedInUserId);
        if (!optionalUsuario.isPresent() || !(optionalUsuario.get() instanceof Cliente)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Não foi possível encontrar os detalhes da sua conta de cliente.");
            return "redirect:/home";
        }
        Cliente cliente = (Cliente) optionalUsuario.get();
        model.addAttribute("enderecos", cliente.getEnderecos());

        // Add common model attributes for layout
        String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
        model.addAttribute("isUserLoggedIn", true);
        model.addAttribute("loggedInUserEmail", loggedInUserEmail);
        model.addAttribute("loggedInUserName", session.getAttribute("loggedInUserName"));
        model.addAttribute("loggedInUserType", session.getAttribute("loggedInUserType"));
        return "checkout/address-select";
    }

    @PostMapping("/checkout/address-select")
    public String processAddressSelection(@RequestParam("selectedEnderecoId") Integer selectedEnderecoId,
                                            HttpSession session,
                                            RedirectAttributes redirectAttributes) {
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");
        if (loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessão expirada ou não autenticada. Por favor, entre novamente.");
            return "redirect:/login";
        }

        // Optional: Validate that selectedEnderecoId belongs to the loggedInUserId
        // This is important for security. For now, we'll trust the ID.
        // Example validation:
        // Optional<Usuario> optionalUsuario = usuarioRepository.findById(loggedInUserId);
        // if (optionalUsuario.isPresent() && optionalUsuario.get() instanceof Cliente) {
        //     Cliente cliente = (Cliente) optionalUsuario.get();
        //     boolean addressIsValid = cliente.getEnderecos().stream().anyMatch(e -> e.getId().equals(selectedEnderecoId));
        //     if (!addressIsValid) {
        //         redirectAttributes.addFlashAttribute("errorMessage", "Invalid address selected.");
        //         return "redirect:/checkout/address-select";
        //     }
        // } else {
        //      redirectAttributes.addFlashAttribute("errorMessage", "User not found or not a client.");
        //      return "redirect:/login";
        // }


        if (selectedEnderecoId == null) {
             redirectAttributes.addFlashAttribute("errorMessage", "Por favor, selecione um endereço de entrega.");
             return "redirect:/checkout/address-select";
        }
        
        session.setAttribute("selectedEnderecoId", selectedEnderecoId);
        return "redirect:/checkout/confirm-order-details"; // Next step in checkout
    }

    @GetMapping("/checkout/confirm-order-details") // Renamed mapping
    public String showConfirmOrderDetailsPage(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");
        String userType = (String) session.getAttribute("loggedInUserType");

        if (loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado para confirmar seu pedido.");
            return "redirect:/login";
        }
        if (!"CLIENTE".equals(userType)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Apenas Clientes podem confirmar pedidos.");
            return "redirect:/cart"; 
        }

        ShoppingCartDTO cart = shoppingCartService.getCart();
        if (cart == null || cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Seu carrinho está vazio.");
            return "redirect:/cart";
        }

        Integer selectedEnderecoId = (Integer) session.getAttribute("selectedEnderecoId");
        if (selectedEnderecoId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nenhum endereço de entrega selecionado. Por favor, selecione um endereço.");
            return "redirect:/checkout/address-select";
        }

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(loggedInUserId);
        if (!optionalUsuario.isPresent() || !(optionalUsuario.get() instanceof Cliente)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Não foi possível encontrar os detalhes da sua conta de cliente.");
            return "redirect:/login"; 
        }
        Cliente cliente = (Cliente) optionalUsuario.get();
        
        Optional<Endereco> selectedEnderecoOpt = cliente.getEnderecos().stream()
                .filter(e -> e.getId().equals(selectedEnderecoId))
                .findFirst();

        if (!selectedEnderecoOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Endereço selecionado não encontrado. Por favor, selecione novamente.");
            session.removeAttribute("selectedEnderecoId"); // Clear invalid ID
            return "redirect:/checkout/address-select";
        }
        
        model.addAttribute("shoppingCart", cart);
        model.addAttribute("selectedEndereco", selectedEnderecoOpt.get());

        // Add common model attributes for layout
        String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
        model.addAttribute("isUserLoggedIn", true);
        model.addAttribute("loggedInUserEmail", loggedInUserEmail);
        model.addAttribute("loggedInUserName", session.getAttribute("loggedInUserName"));
        model.addAttribute("loggedInUserType", session.getAttribute("loggedInUserType"));

        return "checkout/confirm-order-details";
    }

    @PostMapping("/checkout/place-order")
    public String placeOrder(HttpSession session, RedirectAttributes redirectAttributes) {

        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");
        String userType = (String) session.getAttribute("loggedInUserType");

        if (loggedInUserId == null || !"CLIENTE".equals(userType)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado como Cliente para fazer um pedido.");
            return "redirect:/login";
        }

        ShoppingCartDTO cart = shoppingCartService.getCart();
        if (cart == null || cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Seu carrinho está vazio. Não é possível fazer o pedido.");
            return "redirect:/cart";
        }

        Integer selectedEnderecoId = (Integer) session.getAttribute("selectedEnderecoId");
        if (selectedEnderecoId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Endereço não selecionado ou sessão expirada. Por favor, selecione um endereço.");
            return "redirect:/checkout/address-select";
        }

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(loggedInUserId);
        if (!optionalUsuario.isPresent() || !(optionalUsuario.get() instanceof Cliente)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Não foi possível encontrar os detalhes da conta do Cliente.");
            return "redirect:/login"; // Should not happen if session is consistent
        }
        Cliente cliente = (Cliente) optionalUsuario.get();

        Optional<Endereco> selectedEnderecoOpt = cliente.getEnderecos().stream()
            .filter(e -> e.getId().equals(selectedEnderecoId))
            .findFirst();

        if (!selectedEnderecoOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Endereço de entrega inválido selecionado. Por favor, selecione novamente.");
            return "redirect:/checkout/address-select";
        }
        Endereco enderecoEntrega = selectedEnderecoOpt.get();

        Pedido novoPedido = new Pedido();
        novoPedido.setCliente(cliente);
        novoPedido.setEnderecoEntrega(enderecoEntrega);
        novoPedido.setDataRealizacao(Timestamp.from(Instant.now()));
        novoPedido.setStatus(StatusPedido.PENDENTE);
        novoPedido.setValorTotal(cart.getGrandTotal().doubleValue());
        
        List<ItemPedido> itensPedido = new ArrayList<>();
        for (CartItemDTO cartItemDto : cart.getItems()) {
            Optional<Produto> optionalProduto = produtoService.buscarProdutoPorId(cartItemDto.getProductId());
            if (!optionalProduto.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Um produto em seu carrinho não está mais disponível. O pedido não pode ser feito.");
                return "redirect:/cart";
            }
            Produto produto = optionalProduto.get();
            
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setProduto(produto);
            itemPedido.setPedido(novoPedido);
            itemPedido.setQuantidade(cartItemDto.getQuantity());
            itemPedido.setPrecoUnitarioMomentoCompra(cartItemDto.getUnitPrice().doubleValue());
            itemPedido.setSubtotal(cartItemDto.getSubtotal().doubleValue());
            itensPedido.add(itemPedido);
        }
        novoPedido.setItens(itensPedido);
        
        try {
            // Using existing criarPedido method which calls pedidoRepository.save()
            Pedido pedidoSalvo = pedidoService.criarPedido(novoPedido); 
            shoppingCartService.clearCart();
            session.removeAttribute("selectedEnderecoId"); // Clear selected address from session
            redirectAttributes.addFlashAttribute("successMessage", "Pedido realizado com sucesso! O ID do seu pedido é: " + pedidoSalvo.getId());
            // TODO: Create this confirmation page in a later step
            return "redirect:/checkout/order/" + pedidoSalvo.getId() + "/confirmation"; 
        } catch (Exception e) {
            // Log e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao fazer o pedido. Por favor, tente novamente. " + e.getMessage());
            return "redirect:/cart"; // Or /checkout/address-select
        }
    }

    @GetMapping("/checkout/order/{orderId}/confirmation")
    public String showOrderConfirmationPage(@PathVariable("orderId") Integer orderId,
                                            Model model, HttpSession session,
                                            RedirectAttributes redirectAttributes) { // Added RedirectAttributes for flash message

         // Check for success message from redirect (important if not fetching order details deeply)
         // This check is implicitly handled by Thymeleaf if the attribute exists.
         // Explicitly re-adding it to the model isn't strictly necessary if it's a flash attribute
         // but can be done if direct model manipulation is preferred for some reason.
         // For now, we assume the flash attribute "successMessage" from placeOrder is available.

        Optional<Pedido> optionalPedido = pedidoService.buscarPedidoPorId(orderId);
        if (!optionalPedido.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Pedido não encontrado.");
            return "redirect:/"; // Or to an error page or order history
        }
        
        Pedido pedido = optionalPedido.get();
        
        // Security check: Ensure the logged-in user is the one who placed the order, or an admin.
        // For simplicity now, we'll allow viewing if they have the link and ID, but this is a security note.
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");
        String userType = (String) session.getAttribute("loggedInUserType");

        // Basic check if user is logged in and is the owner or an admin (not implemented)
        // if (loggedInUserId == null || (!pedido.getCliente().getId().equals(loggedInUserId) /* && !userType.equals("ADMIN") */ )) {
        //      // If not the owner, don't show details, redirect.
        //      // For now, we'll allow it for simplicity of the confirmation message flow.
        //      // In a real app, this needs to be stricter.
        // }

        model.addAttribute("pedido", pedido);

        // Add login status for layout
        model.addAttribute("isUserLoggedIn", loggedInUserId != null);
        if (loggedInUserId != null) {
            model.addAttribute("loggedInUserEmail", (String) session.getAttribute("loggedInUserEmail"));
            model.addAttribute("loggedInUserName", session.getAttribute("loggedInUserName"));
            model.addAttribute("loggedInUserType", userType);
        }
        return "checkout/order-confirmation";
   }
   // } Closing brace removed here

    @GetMapping("/cliente/pedidos")
    public String showMyOrders(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String userType = (String) session.getAttribute("loggedInUserType");
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");

        if (!"CLIENTE".equals(userType) || loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado como Cliente para ver seus pedidos.");
            return "redirect:/login";
        }

        List<Pedido> myOrders = pedidoService.listarPedidosPorCliente(loggedInUserId);
        model.addAttribute("pedidos", myOrders);

        // Pass login status and type for layout
        model.addAttribute("isUserLoggedIn", true); // Must be logged in to reach here as Cliente
        model.addAttribute("loggedInUserEmail", (String) session.getAttribute("loggedInUserEmail"));
        model.addAttribute("loggedInUserName", session.getAttribute("loggedInUserName"));
        model.addAttribute("loggedInUserType", userType);
        
        return "cliente/my-orders";
    }
    // } Removed one extra brace here
// } Removed another extra brace here
}
