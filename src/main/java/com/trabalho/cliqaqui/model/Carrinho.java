package com.trabalho.cliqaqui.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType; // For lazy/eager loading if specified
// import jakarta.persistence.Timestamp; // For dataUltimaModificacao, or use java.sql.Timestamp (switched to LocalDateTime)
import org.hibernate.annotations.UpdateTimestamp; // For automatic timestamp update

import java.time.LocalDateTime; // Alternative for timestamp
import java.util.ArrayList;
import java.util.List;

@Entity
public class Carrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY) // Owning side of the relationship
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", unique = true, nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemCarrinho> itens = new ArrayList<>();

    @UpdateTimestamp // Automatically updates when the entity is modified
    private LocalDateTime dataUltimaModificacao; // Using LocalDateTime

    // Default constructor
    public Carrinho() {
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<ItemCarrinho> getItens() {
        return itens;
    }

    public void setItens(List<ItemCarrinho> itens) {
        this.itens = itens;
    }

    public LocalDateTime getDataUltimaModificacao() {
        return dataUltimaModificacao;
    }

    public void setDataUltimaModificacao(LocalDateTime dataUltimaModificacao) {
        this.dataUltimaModificacao = dataUltimaModificacao;
    }

    // Convenience methods (optional, can be in service)
    public void adicionarItem(ItemCarrinho item) {
        this.itens.add(item);
        item.setCarrinho(this);
    }

    public void removerItem(ItemCarrinho item) {
        this.itens.remove(item);
        item.setCarrinho(null);
    }
}
