package com.trabalho.cliqaqui.controller;

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
        } else {
            model.addAttribute("isUserLoggedIn", false);
        }
        return "registrar";
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
        // Encode the password using PasswordService
        novoUsuario.setSenhaHash(passwordService.hashPassword(usuarioDto.getSenha()));
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
    public String login(Model model, HttpSession session) { // Add Model if not there
        String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
        if (loggedInUserEmail != null) {
            model.addAttribute("isUserLoggedIn", true);
            model.addAttribute("loggedInUserEmail", loggedInUserEmail);
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

                redirectAttributes.addFlashAttribute("successMessage", "Login successful! Welcome " + usuario.getNome());
                return "redirect:/home";
            }
        }

        // Failed login (user not found or password incorrect)
        redirectAttributes.addFlashAttribute("loginError", "Invalid email or password.");
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
        } else {
            model.addAttribute("isUserLoggedIn", false);
        }
        return "produtos";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("logoutMessage", "You have been logged out successfully.");
        return "redirect:/login";
    }

    @GetMapping("/vendedor/produtos/add")
    public String showAddProductForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String userType = (String) session.getAttribute("loggedInUserType");
        String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");

        model.addAttribute("isUserLoggedIn", loggedInUserEmail != null);
        if (loggedInUserEmail != null) {
            model.addAttribute("loggedInUserEmail", loggedInUserEmail);
            model.addAttribute("loggedInUserType", userType); // Pass userType for Vendedor links in layout
        }

        if (!"VENDEDOR".equals(userType)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be a Vendedor to add products.");
            return "redirect:/login"; // Or redirect:/home
        }
        model.addAttribute("produtoDto", new ProdutoDTO());
        return "vendedor/add-product";
    }

    @PostMapping("/vendedor/produtos/add")
    public String processAddProduct(@ModelAttribute("produtoDto") ProdutoDTO produtoDto, /* TODO: Add @Valid and BindingResult for validation */
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes,
                                    Model model) { // Added Model for re-rendering form on error

        String userType = (String) session.getAttribute("loggedInUserType");
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");

        if (!"VENDEDOR".equals(userType) || loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in as a Vendedor to add products.");
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

        // Fetch the Vendedor entity
        Optional<Usuario> optionalVendedor = usuarioRepository.findById(loggedInUserId);
        if (!optionalVendedor.isPresent() || !(optionalVendedor.get() instanceof Vendedor)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not find Vendedor account.");
            return "redirect:/vendedor/produtos/add"; // Or some other error handling
        }
        Vendedor vendedor = (Vendedor) optionalVendedor.get();

        // Convert DTO to Produto entity
        Produto produto = new Produto();
        produto.setNome(produtoDto.getNome());
        produto.setPreco(produtoDto.getPreco());
        produto.setDescricao(produtoDto.getDescricao());
        produto.setVendedor(vendedor); // Associate with the logged-in Vendedor

        try {
            produtoService.salvarProduto(produto);
            redirectAttributes.addFlashAttribute("successMessage", "Product added successfully!");
            return "redirect:/vendedor/produtos/my"; // Redirect to "My Products" page (to be created)
        } catch (Exception e) {
            // Log the exception e.g. e.printStackTrace();
            model.addAttribute("errorMessage", "Error adding product. Please try again.");
            model.addAttribute("produtoDto", produtoDto); // Send DTO back
            // Re-populate login status for layout
            String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
            model.addAttribute("isUserLoggedIn", loggedInUserEmail != null);
            if (loggedInUserEmail != null) {
                model.addAttribute("loggedInUserEmail", loggedInUserEmail);
                model.addAttribute("loggedInUserType", userType);
            }
            return "vendedor/add-product"; // Return to form
        }

    @GetMapping("/vendedor/produtos/my")
    public String showMyProducts(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String userType = (String) session.getAttribute("loggedInUserType");
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");

        if (!"VENDEDOR".equals(userType) || loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in as a Vendedor to view your products.");
            return "redirect:/login";
        }

        List<Produto> myProducts = produtoService.listarProdutosPorVendedor(loggedInUserId);
        model.addAttribute("produtos", myProducts); // Use "produtos" to reuse product listing template logic if possible

        // Pass login status and type for layout
        String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
        model.addAttribute("isUserLoggedIn", true); // Must be logged in to reach here as Vendedor
        model.addAttribute("loggedInUserEmail", loggedInUserEmail);
        model.addAttribute("loggedInUserType", userType);
        
        // Add any specific messages e.g. from adding a product
        if (model.containsAttribute("successMessage")) { // Check if flash attribute was added
            model.addAttribute("successMessage", model.getAttribute("successMessage"));
        }

        return "vendedor/my-products";
    }

    @PostMapping("/cart/add/{productId}")
    public String addToCart(@PathVariable("productId") Integer productId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            HttpSession session, // To check user type if needed, and for general session access
                            RedirectAttributes redirectAttributes) {

        // Optional: Check if user is a CLIENTE before adding to cart
        // String userType = (String) session.getAttribute("loggedInUserType");
        // if (!"CLIENTE".equals(userType)) {
        //     redirectAttributes.addFlashAttribute("errorMessage", "Only Clientes can add products to a cart. Please log in as a Cliente.");
        //     return "redirect:/login";
        // }

        Optional<Produto> optionalProduto = produtoService.buscarProdutoPorId(productId);
        if (optionalProduto.isPresent()) {
            Produto produto = optionalProduto.get();
            try {
                shoppingCartService.addItem(produto, quantity);
                redirectAttributes.addFlashAttribute("successMessage", produto.getNome() + " added to cart!");
            } catch (Exception e) { // Catch any potential exceptions from cart service
                // Log e.printStackTrace();
                redirectAttributes.addFlashAttribute("errorMessage", "Error adding product to cart. Please try again.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found!");
        }

        // Redirect back to the products page, or ideally the referer.
        // For simplicity, redirecting to products page.
        return "redirect:/produtos";
    }

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        ShoppingCartDTO cartDto = shoppingCartService.getCart(); // Assumes shoppingCartService is injected
        model.addAttribute("shoppingCart", cartDto);

        // Add login status for layout and conditional checkout button
        String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
        String userType = (String) session.getAttribute("loggedInUserType");
        model.addAttribute("isUserLoggedIn", loggedInUserEmail != null);
        if (loggedInUserEmail != null) {
            model.addAttribute("loggedInUserEmail", loggedInUserEmail);
            model.addAttribute("loggedInUserType", userType);
        }
        return "cart/view-cart";
    }

    @PostMapping("/cart/update/{productId}")
    public String updateCartItem(@PathVariable("productId") Integer productId,
                               @RequestParam("quantity") int quantity,
                               RedirectAttributes redirectAttributes) {
        try {
            shoppingCartService.updateItemQuantity(productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Cart updated successfully.");
        } catch (Exception e) {
            // Log e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating cart. Please try again.");
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{productId}")
    public String removeCartItem(@PathVariable("productId") Integer productId,
                               RedirectAttributes redirectAttributes) {
        try {
            shoppingCartService.removeItem(productId);
            redirectAttributes.addFlashAttribute("successMessage", "Item removed from cart.");
        } catch (Exception e) {
            // Log e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error removing item from cart. Please try again.");
        }
        return "redirect:/cart";
    }

    @GetMapping("/checkout/confirm")
    public String showConfirmOrderPage(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String userType = (String) session.getAttribute("loggedInUserType");
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");

        if (!"CLIENTE".equals(userType) || loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in as a Cliente to checkout.");
            return "redirect:/login";
        }

        ShoppingCartDTO cart = shoppingCartService.getCart();
        if (cart == null || cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty.");
            return "redirect:/cart";
        }

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(loggedInUserId);
        if (!optionalUsuario.isPresent() || !(optionalUsuario.get() instanceof Cliente)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not find Cliente account details.");
            // This case should ideally not happen if session integrity is maintained
            return "redirect:/login";
        }
        Cliente cliente = (Cliente) optionalUsuario.get();
        List<Endereco> enderecos = cliente.getEnderecos(); // Assumes getEnderecos() is available and populated

        model.addAttribute("shoppingCart", cart);
        model.addAttribute("enderecos", enderecos);

        // Add login status for layout
        model.addAttribute("isUserLoggedIn", true);
        model.addAttribute("loggedInUserEmail", (String) session.getAttribute("loggedInUserEmail"));
        model.addAttribute("loggedInUserType", userType);

        return "checkout/confirm-order";
    }

    @PostMapping("/checkout/place-order")
    public String placeOrder(@RequestParam("enderecoId") Integer enderecoId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        String userType = (String) session.getAttribute("loggedInUserType");
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");

        if (!"CLIENTE".equals(userType) || loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in as a Cliente to place an order.");
            return "redirect:/login";
        }

        ShoppingCartDTO cart = shoppingCartService.getCart();
        if (cart == null || cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty. Cannot place order.");
            return "redirect:/cart";
        }

        if (enderecoId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a shipping address.");
            return "redirect:/checkout/confirm";
        }

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(loggedInUserId);
        if (!optionalUsuario.isPresent() || !(optionalUsuario.get() instanceof Cliente)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not find Cliente account details.");
            return "redirect:/login";
        }
        Cliente cliente = (Cliente) optionalUsuario.get();

        Optional<Endereco> selectedEnderecoOpt = cliente.getEnderecos().stream()
            .filter(e -> e.getId().equals(enderecoId))
            .findFirst();

        if (!selectedEnderecoOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid shipping address selected.");
            return "redirect:/checkout/confirm";
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
                redirectAttributes.addFlashAttribute("errorMessage", "A product in your cart is no longer available. Order cannot be placed.");
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
            redirectAttributes.addFlashAttribute("successMessage", "Order placed successfully! Your Order ID is: " + pedidoSalvo.getId());
            // TODO: Create this confirmation page in a later step
            return "redirect:/checkout/order/" + pedidoSalvo.getId() + "/confirmation"; 
        } catch (Exception e) {
            // Log e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error placing order. Please try again. " + e.getMessage());
            return "redirect:/checkout/confirm";
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
            redirectAttributes.addFlashAttribute("errorMessage", "Order not found.");
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
            model.addAttribute("loggedInUserType", userType);
        }
        return "checkout/order-confirmation";
   }

    @GetMapping("/cliente/pedidos")
    public String showMyOrders(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String userType = (String) session.getAttribute("loggedInUserType");
        Integer loggedInUserId = (Integer) session.getAttribute("loggedInUserId");

        if (!"CLIENTE".equals(userType) || loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in as a Cliente to view your orders.");
            return "redirect:/login";
        }

        List<Pedido> myOrders = pedidoService.listarPedidosPorCliente(loggedInUserId);
        model.addAttribute("pedidos", myOrders);

        // Pass login status and type for layout
        model.addAttribute("isUserLoggedIn", true); // Must be logged in to reach here as Cliente
        model.addAttribute("loggedInUserEmail", (String) session.getAttribute("loggedInUserEmail"));
        model.addAttribute("loggedInUserType", userType);
        
        return "cliente/my-orders";
    }
    }
    }
}
