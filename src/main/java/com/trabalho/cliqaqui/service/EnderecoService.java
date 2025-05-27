package com.trabalho.cliqaqui.service;

import com.trabalho.cliqaqui.dto.EnderecoDTO;
import com.trabalho.cliqaqui.model.Endereco;
import com.trabalho.cliqaqui.model.User;
import com.trabalho.cliqaqui.repositories.EnderecoRepository;
import com.trabalho.cliqaqui.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnderecoService {

    @Autowired private EnderecoRepository enderecoRepo;
    @Autowired private UserRepository userRepo;

    public void salvar(EnderecoDTO dto) {
        User user = userRepo.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        Endereco e = new Endereco();
        e.setRua(dto.getRua());
        e.setNumero(dto.getNumero());
        e.setComplemento(dto.getComplemento());
        e.setBairro(dto.getBairro()); // CORREÇÃO AQUI: era um método incompleto, agora é setBairro
        e.setCidade(dto.getCidade());
        e.setEstado(dto.getEstado());
        e.setCep(dto.getCep());
        e.setUser(user);
        enderecoRepo.save(e);
    }
}