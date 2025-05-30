package com.clientehm.service;

import com.clientehm.entity.AdministradorEntity;
import com.clientehm.model.AdministradorLoginDTO;
import com.clientehm.model.AdministradorRegistroDTO;
import com.clientehm.model.RedefinirSenhaDTO;
import com.clientehm.model.VerificarPalavraChaveDTO;
import com.clientehm.repository.AdministradorRepository;
import com.clientehm.util.JwtUtil;
import com.clientehm.exception.AdminNotFoundException;
import com.clientehm.exception.InvalidCredentialsException;
import com.clientehm.exception.EmailAlreadyExistsException;
import com.clientehm.exception.WeakPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // Método para verificar a força da senha
    private boolean isStrongPassword(String password) {
        if (password == null) return false;
        // Pelo menos 6 caracteres, 1 maiúscula, 1 minúscula, 1 número, 1 símbolo
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{6,}$";
        return password.matches(regex);
    }

    public Map<String, Object> login(AdministradorLoginDTO loginDTO) {
        AdministradorEntity admin = administradorRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciais inválidas"));

        if (!passwordEncoder.matches(loginDTO.getSenha(), admin.getSenha())) {
            throw new InvalidCredentialsException("Credenciais inválidas");
        }

        String token = jwtUtil.generateToken(admin.getEmail());
        Map<String, Object> response = new HashMap<>();
        response.put("mensagem", "Login realizado com sucesso");
        response.put("token", token);
        response.put("nome", admin.getNome());
        response.put("email", admin.getEmail());
        return response;
    }

    public AdministradorEntity register(AdministradorRegistroDTO registroDTO) {
        if (administradorRepository.findByEmail(registroDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email já cadastrado");
        }
        if (!isStrongPassword(registroDTO.getSenha())) {
            throw new WeakPasswordException("Senha fraca! Use letras maiúsculas, minúsculas, número e símbolo.");
        }

        String encodedPassword = passwordEncoder.encode(registroDTO.getSenha());
        AdministradorEntity novoAdministrador = new AdministradorEntity(
                registroDTO.getNome(),
                registroDTO.getEmail(),
                encodedPassword,
                registroDTO.getPalavraChave()
        );
        return administradorRepository.save(novoAdministrador);
    }

    public boolean verifyKeyword(VerificarPalavraChaveDTO verificarDTO) {
        AdministradorEntity admin = administradorRepository.findByEmail(verificarDTO.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Email ou palavra-chave incorretos"));

        return verificarDTO.getPalavraChave().trim().equalsIgnoreCase(admin.getPalavraChave().trim());
    }

    public AdministradorEntity resetPassword(RedefinirSenhaDTO redefinirDTO) {
        AdministradorEntity admin = administradorRepository.findByEmail(redefinirDTO.getEmail())
                .orElseThrow(() -> new AdminNotFoundException("Administrador não encontrado"));

        if (!isStrongPassword(redefinirDTO.getNovaSenha())) {
            throw new WeakPasswordException("Senha fraca! Use letras maiúsculas, minúsculas, número e símbolo.");
        }

        admin.setSenha(passwordEncoder.encode(redefinirDTO.getNovaSenha()));
        return administradorRepository.save(admin);
    }
}