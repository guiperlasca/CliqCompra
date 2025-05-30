package com.trabalho.cliqaqui.dto;

import java.math.BigDecimal; // Good for currency

public class CartItemDTO {
    private Integer productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice; // Use BigDecimal for price
    private BigDecimal subtotal;   // quantity * unitPrice

    public CartItemDTO() {
    }

    public CartItemDTO(Integer productId, String productName, int quantity, BigDecimal unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = unitPrice.multiply(new BigDecimal(quantity));
    }

    // Getters and Setters for all fields
    // (Make sure subtotal is updated if quantity or unitPrice changes via setters, or recalculate on get)
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        if (this.unitPrice != null) { // Recalculate subtotal
            this.subtotal = this.unitPrice.multiply(new BigDecimal(this.quantity));
        }
    }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        if (this.unitPrice != null) { // Recalculate subtotal
            this.subtotal = this.unitPrice.multiply(new BigDecimal(this.quantity));
        }
    }
    public BigDecimal getSubtotal() {
         if (this.unitPrice != null) {
            return this.unitPrice.multiply(new BigDecimal(this.quantity));
        }
        return BigDecimal.ZERO;
    }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; } // Usually calculated
}
