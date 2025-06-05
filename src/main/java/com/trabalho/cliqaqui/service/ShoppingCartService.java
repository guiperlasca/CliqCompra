package com.trabalho.cliqaqui.service;

import com.trabalho.cliqaqui.dto.CartItemDTO;
import com.trabalho.cliqaqui.dto.ShoppingCartDTO;
import com.trabalho.cliqaqui.model.Produto;
import com.trabalho.cliqaqui.model.Usuario;
import com.trabalho.cliqaqui.model.Carrinho;
import com.trabalho.cliqaqui.model.ItemCarrinho;
import com.trabalho.cliqaqui.repositories.CarrinhoRepository;
import com.trabalho.cliqaqui.repositories.ItemCarrinhoRepository;
import com.trabalho.cliqaqui.repositories.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// Removed WebApplicationContext import as @Scope is removed

import java.math.BigDecimal;
import java.util.ArrayList;
// import java.util.HashMap; // Removed as items map is removed
import java.util.List;
// import java.util.Map; // Removed as items map is removed
import java.util.Optional;
import jakarta.persistence.EntityNotFoundException; // For product not found

@Service
// @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS) // Removed Session Scope
public class ShoppingCartService {

    // private Map<Integer, CartItemDTO> items = new HashMap<>(); // Removed In-Memory Storage

    private final CarrinhoRepository carrinhoRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final ProdutoService produtoService; // Assuming this is needed for product details
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public ShoppingCartService(CarrinhoRepository carrinhoRepository,
                               ItemCarrinhoRepository itemCarrinhoRepository,
                               ProdutoService produtoService,
                               UsuarioRepository usuarioRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.itemCarrinhoRepository = itemCarrinhoRepository;
        this.produtoService = produtoService;
        this.usuarioRepository = usuarioRepository;
    }

    public void addItem(Usuario usuario, Integer produtoId, int quantity) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo para adicionar item ao carrinho.");
        }
        if (produtoId == null) {
            throw new IllegalArgumentException("ID do produto não pode ser nulo.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        }

        Produto produto = produtoService.buscarProdutoPorId(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + produtoId));

        Optional<Carrinho> optionalCarrinho = carrinhoRepository.findByUsuario(usuario);
        Carrinho carrinho;

        if (optionalCarrinho.isEmpty()) {
            carrinho = new Carrinho();
            carrinho.setUsuario(usuario);
            // dataUltimaModificacao will be set by @UpdateTimestamp
            // No need to explicitly save here if we save at the end,
            // and cascade will handle new Carrinho if it's linked to Usuario properly.
            // However, to get an ID for ItemCarrinho if it were saved first, we might need to save.
            // For simplicity with cascade, let's ensure it's managed before adding items.
            // This also ensures that if usuario.setCarrinho is used, it has an ID.
             carrinho = carrinhoRepository.save(carrinho);
        } else {
            carrinho = optionalCarrinho.get();
        }

        Optional<ItemCarrinho> optionalItem = carrinho.getItens().stream()
            .filter(item -> item.getProduto().getId().equals(produto.getId()))
            .findFirst();

        if (optionalItem.isPresent()) {
            ItemCarrinho itemExistente = optionalItem.get();
            itemExistente.setQuantidade(itemExistente.getQuantidade() + quantity);
            // itemCarrinhoRepository.save(itemExistente); // Rely on cascade from carrinho save
        } else {
            ItemCarrinho novoItem = new ItemCarrinho();
            novoItem.setProduto(produto);
            novoItem.setQuantidade(quantity);
            novoItem.setCarrinho(carrinho); // Link back to cart
            carrinho.getItens().add(novoItem); // Add to cart's collection for cascading
            // itemCarrinhoRepository.save(novoItem); // Rely on cascade from carrinho save
        }
        carrinhoRepository.save(carrinho); // This should cascade to save new/updated ItemCarrinho
    }

    public void updateItemQuantity(Usuario usuario, Integer produtoId, int quantity) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo para atualizar item no carrinho.");
        }
        if (produtoId == null) {
            throw new IllegalArgumentException("ID do produto não pode ser nulo.");
        }

        Optional<Carrinho> optionalCarrinho = carrinhoRepository.findByUsuario(usuario);

        if (optionalCarrinho.isEmpty()) {
            // No cart found for user, so nothing to update.
            // Can log a warning or throw IllegalStateException if a cart is expected.
            // For now, just returning as no operation can be performed.
            return;
        }

        Carrinho carrinho = optionalCarrinho.get();
        boolean changed = false;

        Optional<ItemCarrinho> optionalItem = carrinho.getItens().stream()
            .filter(item -> item.getProduto() != null && item.getProduto().getId().equals(produtoId))
            .findFirst();

        if (optionalItem.isPresent()) {
            ItemCarrinho itemToUpdate = optionalItem.get();
            if (quantity > 0) {
                if (itemToUpdate.getQuantidade() != quantity) {
                    itemToUpdate.setQuantidade(quantity);
                    changed = true;
                }
            } else {
                // Quantity is zero or negative, so remove the item
                carrinho.getItens().remove(itemToUpdate);
                // No need to call itemCarrinhoRepository.delete(itemToUpdate);
                // if orphanRemoval=true is set on Carrinho.itens and CascadeType.ALL includes REMOVE.
                changed = true;
            }
        } else {
            // Product not in cart, so nothing to update.
            // Can log a warning if this scenario is unexpected.
            return;
        }

        if (changed) {
            carrinhoRepository.save(carrinho);
        }
    }

    public void removeItem(Usuario usuario, Integer produtoId) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo para remover item do carrinho.");
        }
        if (produtoId == null) {
            throw new IllegalArgumentException("ID do produto não pode ser nulo.");
        }

        Optional<Carrinho> optionalCarrinho = carrinhoRepository.findByUsuario(usuario);

        if (optionalCarrinho.isEmpty()) {
            // No cart found for user, so nothing to remove.
            return;
        }

        Carrinho carrinho = optionalCarrinho.get();
        boolean removed = false;

        Optional<ItemCarrinho> optionalItem = carrinho.getItens().stream()
            .filter(item -> item.getProduto() != null && item.getProduto().getId().equals(produtoId))
            .findFirst();

        if (optionalItem.isPresent()) {
            ItemCarrinho itemToRemove = optionalItem.get();
            carrinho.getItens().remove(itemToRemove);
            // itemToRemove.setCarrinho(null); // Not strictly necessary due to orphanRemoval, but good for consistency if object is used after.
            removed = true;
        } else {
            // Product not in cart, so nothing to remove.
            return;
        }

        if (removed) {
            carrinhoRepository.save(carrinho);
        }
    }

    public ShoppingCartDTO getCart(Usuario usuario) {
        if (usuario == null) {
            // Option 1: Throw exception
            // throw new IllegalArgumentException("Usuario não pode ser nulo para obter o carrinho.");
            // Option 2: Return an empty cart, which might be simpler for controllers
            return new ShoppingCartDTO();
        }

        Optional<Carrinho> optionalCarrinho = carrinhoRepository.findByUsuario(usuario);
        Carrinho carrinho;

        if (optionalCarrinho.isEmpty()) {
            carrinho = new Carrinho();
            carrinho.setUsuario(usuario);
            // dataUltimaModificacao will be set by @UpdateTimestamp on save
            carrinho = carrinhoRepository.save(carrinho); // Save and get the persisted cart with ID
            // Note: If the 'usuario' object is managed by Hibernate and this new 'carrinho'
            // needs to be reflected in 'usuario.getCarrinho()' within the same Hibernate session
            // without re-fetching 'usuario', then 'usuario.setCarrinho(carrinho);' might be needed here.
            // However, since Carrinho is the owner of the relationship via 'usuario_id',
            // saving the Carrinho with the correct Usuario foreign key is the primary concern for DB state.
        } else {
            carrinho = optionalCarrinho.get();
        }

        ShoppingCartDTO cartDto = new ShoppingCartDTO();
        List<CartItemDTO> itemDtos = new ArrayList<>();

        if (carrinho.getItens() != null) { // Ensure itens list is not null
            for (ItemCarrinho itemEntity : carrinho.getItens()) {
                Produto produto = itemEntity.getProduto();
                if (produto != null) { // Check if product is not null (robustness)
                    CartItemDTO itemDto = new CartItemDTO();
                    itemDto.setProductId(produto.getId());
                    itemDto.setProductName(produto.getNome());
                    itemDto.setQuantity(itemEntity.getQuantidade());
                    itemDto.setUnitPrice(BigDecimal.valueOf(produto.getPreco())); // Convert double to BigDecimal
                    // Subtotal is calculated by CartItemDTO's setters/constructor or getSubtotal
                    itemDtos.add(itemDto);
                }
            }
        }

        cartDto.setItems(itemDtos);
        cartDto.recalculateGrandTotal(); // Helper method in ShoppingCartDTO

        return cartDto;
    }

    public void clearCart(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo para limpar o carrinho.");
        }

        Optional<Carrinho> optionalCarrinho = carrinhoRepository.findByUsuario(usuario);

        if (optionalCarrinho.isPresent()) {
            Carrinho carrinho = optionalCarrinho.get();
            if (carrinho.getItens() != null && !carrinho.getItens().isEmpty()) {
                carrinho.getItens().clear(); // This will mark all items for orphan removal
                carrinhoRepository.save(carrinho); // Save to trigger orphan removal and update timestamp
            }
            // If cart is already empty, no need to save again just for timestamp (unless desired)
        }
        // If no cart exists, there's nothing to clear.
    }
    
    public int getItemCount(Usuario usuario) { // Added Usuario parameter
         // TODO: Reimplement with DB persistence
        return 0; // Return 0 for now
    }
}
