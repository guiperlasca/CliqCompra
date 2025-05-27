package com.trabalho.cliqaqui.dto;

import com.trabalho.cliqaqui.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDTO {
    private Long id;

    @NotBlank(message = "Nome de usuário é obrigatório")
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    private String password;

    @NotBlank(message = "CPF/CNPJ é obrigatório")
    private String cpfCnpj; // Adicionado de acordo com o requisito [cite: 10]

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email; // Adicionado de acordo com o requisito [cite: 10]

    private String telefone; // Adicionado de acordo com o requisito [cite: 10]


    public UserDTO() {
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        // Não expor a senha no DTO ao retornar
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}