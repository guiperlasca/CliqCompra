package com.trabalho.cliqaqui.service;

import com.trabalho.cliqaqui.dto.UserDTO;
import com.trabalho.cliqaqui.model.Role;
import com.trabalho.cliqaqui.model.User;
import com.trabalho.cliqaqui.repositories.RoleRepository;
import com.trabalho.cliqaqui.repositories.UserRepository;
import com.trabalho.cliqaqui.exception.CliqException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO cadastrarUsuario(UserDTO userDTO) { // Permite que o cliente cadastre-se [cite: 1]
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new CliqException("Nome de usuário já existe.");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Senhas devem ser armazenadas de forma segura [cite: 43]

        Set<Role> roles = new HashSet<>();
        // Por padrão, novos usuários são "CLIENTE"
        Role userRole = roleRepository.findByName("ROLE_CLIENTE")
                .orElseGet(() -> {
                    Role newRole = new Role("ROLE_CLIENTE");
                    return roleRepository.save(newRole);
                });
        roles.add(userRole);
        user.setRoles(roles);

        user = userRepository.save(user);
        return new UserDTO(user);
    }

    @Transactional
    public UserDTO editarPerfil(Long userId, UserDTO userDTO) { // Permite que o usuário altere seus dados pessoais [cite: 58]
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new CliqException("Usuário não encontrado com ID: " + userId));

        if (userDTO.getUsername() != null && !userDTO.getUsername().isEmpty() &&
                !existingUser.getUsername().equals(userDTO.getUsername()) &&
                userRepository.existsByUsername(userDTO.getUsername())) {
            throw new CliqException("Nome de usuário já existe.");
        }

        existingUser.setUsername(userDTO.getUsername() != null ? userDTO.getUsername() : existingUser.getUsername());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        // Adicionar lógica para atualizar outros campos como CPF/CNPJ, e-mail, telefones e endereços aqui
        // O sistema deve permitir que o cliente cadastre-se com nome, CPF/CNPJ, e-mail, telefones e endereços. [cite: 10]
        // Isso dependeria de como esses campos seriam modelados no User.java
        // Por enquanto, apenas o username e password são atualizados.

        existingUser = userRepository.save(existingUser);
        return new UserDTO(existingUser);
    }
}