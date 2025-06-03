package com.trabalho.cliqaqui.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
// import com.trabalho.cliqaqui.model.Produto; // Already in same package
// import com.trabalho.cliqaqui.model.Carrinho; // Already in same package

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String email;
    protected String senhaHash;
    private String cpfCnpj;

    @ElementCollection
    @CollectionTable(name = "usuario_telefones", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "telefone")
    private List<String> telefones = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Produto> produtos = new ArrayList<>();

    @OneToOne(mappedBy="usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Carrinho carrinho;

    public Usuario() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public List<String> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<String> telefones) {
        this.telefones = telefones;
    }

    public boolean fazerLogin(String email, String senha) {
        // Placeholder
        return false;
    }

    public void editarPerfil() {
        // Placeholder
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    public Carrinho getCarrinho() {
        return carrinho;
    }

    public void setCarrinho(Carrinho carrinho) {
        this.carrinho = carrinho;
        // Optional: for true bidirectionality, ensure the other side is set if not null
        // if (carrinho != null && carrinho.getUsuario() != this) {
        //     carrinho.setUsuario(this);
        // }
    }
}
