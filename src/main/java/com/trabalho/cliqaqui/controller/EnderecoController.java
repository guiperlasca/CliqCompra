package com.trabalho.cliqaqui.controller; // Adicionado pacote

import com.trabalho.cliqaqui.dto.EnderecoDTO;
import com.trabalho.cliqaqui.service.EnderecoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping; // Adicionado
import org.springframework.web.bind.annotation.RestController; // Adicionado

@RestController // Anotação para indicar que é um controlador REST
@RequestMapping("/enderecos") // Mapeia as requisições para /enderecos
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @PostMapping // Endpoint para adicionar um endereço
    public ResponseEntity<Void> adicionarEndereco(@RequestBody EnderecoDTO dto) {
        enderecoService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}