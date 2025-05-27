package com.trabalho.cliqaqui.controller;

import com.trabalho.cliqaqui.dto.UserDTO;
import com.trabalho.cliqaqui.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping("/{userId}") // Permite ao usuário editar o perfil
    public ResponseEntity<UserDTO> updateUserProfile(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.editarPerfil(userId, userDTO);
        return ResponseEntity.ok(updatedUser);
    }
}