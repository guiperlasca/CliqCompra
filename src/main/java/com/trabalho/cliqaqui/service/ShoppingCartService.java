package com.trabalho.cliqaqui.service;

import com.trabalho.cliqaqui.dto.CartItemDTO;
import com.trabalho.cliqaqui.dto.ShoppingCartDTO;
import com.trabalho.cliqaqui.model.Produto;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext; // For SCOPE_SESSION

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// Optional is not used in the provided code for ShoppingCartService, so not importing for now.

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ShoppingCartService {

    private Map<Integer, CartItemDTO> items = new HashMap<>(); // productId -> CartItemDTO

    public void addItem(Produto produto, int quantity) {
        if (produto == null || quantity <= 0) {
            return; // Or throw exception
        }
        CartItemDTO cartItem = items.get(produto.getId());
        if (cartItem == null) {
            // Convert double from Produto.getPreco() to BigDecimal
            BigDecimal price = BigDecimal.valueOf(produto.getPreco());
            cartItem = new CartItemDTO(produto.getId(), produto.getNome(), quantity, price);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        items.put(produto.getId(), cartItem);
    }

    public void updateItemQuantity(Integer productId, int quantity) {
        CartItemDTO cartItem = items.get(productId);
        if (cartItem != null) {
            if (quantity > 0) {
                cartItem.setQuantity(quantity);
            } else {
                items.remove(productId); // Remove if quantity is zero or less
            }
        }
    }

    public void removeItem(Integer productId) {
        items.remove(productId);
    }

    public ShoppingCartDTO getCart() {
        ShoppingCartDTO cartDto = new ShoppingCartDTO();
        cartDto.setItems(new ArrayList<>(items.values()));
        cartDto.recalculateGrandTotal();
        return cartDto;
    }

    public void clearCart() {
        items.clear();
    }
    
    public int getItemCount() {
         return items.values().stream().mapToInt(CartItemDTO::getQuantity).sum();
    }
}
