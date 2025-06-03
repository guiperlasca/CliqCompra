package com.trabalho.cliqaqui.service;

import com.trabalho.cliqaqui.model.Usuario;
import com.trabalho.cliqaqui.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario salvarUsuario(Usuario usuario) {
        // Add any business logic before saving
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public void deletarUsuario(Integer id) {
        usuarioRepository.deleteById(id);
    }
}
