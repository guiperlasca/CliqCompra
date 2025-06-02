package com.trabalho.cliqaqui.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCartDTO {
    private List<CartItemDTO> items = new ArrayList<>();
    private BigDecimal grandTotal = BigDecimal.ZERO;

    public ShoppingCartDTO() {
    }

    // Getters and Setters
    public List<CartItemDTO> getItems() { return items; }
    public void setItems(List<CartItemDTO> items) { this.items = items; }
    public BigDecimal getGrandTotal() { return grandTotal; }
    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }
    
    // Helper to recalculate grand total if needed
    public void recalculateGrandTotal() {
        this.grandTotal = items.stream()
                               .map(CartItemDTO::getSubtotal)
                               .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
