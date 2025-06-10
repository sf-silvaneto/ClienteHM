package com.clientehm.service;

import com.clientehm.entity.AdministradorEntity;
import com.clientehm.exception.AdminNotFoundException;
import com.clientehm.exception.EmailAlreadyExistsException;
import com.clientehm.exception.InvalidCredentialsException;
import com.clientehm.exception.WeakPasswordException;
import com.clientehm.mapper.AdministradorMapper;
import com.clientehm.model.AdministradorLoginDTO;
import com.clientehm.model.AdministradorRegistroDTO;
import com.clientehm.model.RedefinirSenhaDTO;
import com.clientehm.model.VerifiedProfileUpdateRequestDTO;
import com.clientehm.model.VerificarPalavraChaveDTO;
import com.clientehm.model.dto.AdministradorDadosDTO;
import com.clientehm.repository.AdministradorRepository;
import com.clientehm.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AdministradorMapper administradorMapper;

    private boolean isStrongPassword(String password) {
        if (password == null) return false;
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{6,}$";
        return password.matches(regex);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> login(AdministradorLoginDTO loginDTO) {
        AdministradorEntity admin = administradorRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciais inválidas"));

        if (!passwordEncoder.matches(loginDTO.getSenha(), admin.getSenha())) {
            throw new InvalidCredentialsException("Credenciais inválidas");
        }
        String token = jwtUtil.generateToken(admin.getEmail());
        return administradorMapper.toLoginResponseMap(admin, token);
    }

    @Transactional
    public AdministradorDadosDTO register(AdministradorRegistroDTO registroDTO) {
        if (administradorRepository.findByEmail(registroDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email já cadastrado");
        }
        if (!isStrongPassword(registroDTO.getSenha())) {
            throw new WeakPasswordException("Senha fraca! Use letras maiúsculas, minúsculas, número e símbolo.");
        }

        AdministradorEntity novoAdministrador = administradorMapper.toEntity(registroDTO);
        novoAdministrador.setSenha(passwordEncoder.encode(registroDTO.getSenha()));

        AdministradorEntity adminSalvo = administradorRepository.save(novoAdministrador);
        return administradorMapper.toDadosDTO(adminSalvo);
    }

    @Transactional(readOnly = true)
    public boolean verifyKeyword(VerificarPalavraChaveDTO verificarDTO) {
        AdministradorEntity admin = administradorRepository.findByEmail(verificarDTO.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Email ou palavra-chave incorretos"));

        String palavraChaveArmazenada = admin.getPalavraChave() != null ? admin.getPalavraChave().trim() : "";
        String palavraChaveFornecida = verificarDTO.getPalavraChave() != null ? verificarDTO.getPalavraChave().trim() : "";

        if (!palavraChaveFornecida.equalsIgnoreCase(palavraChaveArmazenada)) {
            throw new InvalidCredentialsException("Email ou palavra-chave incorretos");
        }
        return true;
    }

    @Transactional
    public AdministradorDadosDTO resetPassword(RedefinirSenhaDTO redefinirDTO) { // Alterado para retornar AdministradorDadosDTO
        AdministradorEntity admin = administradorRepository.findByEmail(redefinirDTO.getEmail())
                .orElseThrow(() -> new AdminNotFoundException("Administrador não encontrado"));

        if (!isStrongPassword(redefinirDTO.getNovaSenha())) {
            throw new WeakPasswordException("Senha fraca! Use letras maiúsculas, minúsculas, número e símbolo.");
        }

        admin.setSenha(passwordEncoder.encode(redefinirDTO.getNovaSenha()));
        AdministradorEntity adminAtualizado = administradorRepository.save(admin);
        return administradorMapper.toDadosDTO(adminAtualizado); // Usar o mapper
    }

    @Transactional(readOnly = true)
    public AdministradorEntity findByEmail(String email) { // Método auxiliar se precisar da entidade
        return administradorRepository.findByEmail(email)
                .orElseThrow(() -> new AdminNotFoundException("Administrador não encontrado com email: " + email));
    }

    @Transactional
    public AdministradorDadosDTO updateVerifiedProfileDetails(String adminEmail, VerifiedProfileUpdateRequestDTO dto) {
        AdministradorEntity admin = administradorRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new AdminNotFoundException("Administrador não encontrado."));

        boolean changed = false;

        if (dto.getNome() != null && !dto.getNome().trim().isEmpty()) {
            if (!dto.getNome().trim().equals(admin.getNome())) {
                if (dto.getNome().trim().length() < 3) {
                    throw new IllegalArgumentException("Nome deve ter pelo menos 3 caracteres.");
                }
                admin.setNome(dto.getNome().trim());
                changed = true;
            }
        }

        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            if (!dto.getEmail().trim().equalsIgnoreCase(admin.getEmail())) {
                String newEmailTrimmed = dto.getEmail().trim();
                administradorRepository.findByEmail(newEmailTrimmed).ifPresent(otherAdmin -> {
                    if (!otherAdmin.getId().equals(admin.getId())) {
                        throw new EmailAlreadyExistsException("Este email já está em uso por outro administrador.");
                    }
                });
                admin.setEmail(newEmailTrimmed);
                changed = true;
            }
        }

        if (dto.getNovaPalavraChave() != null && !dto.getNovaPalavraChave().trim().isEmpty()) {
            String novaPalavraChaveRecebida = dto.getNovaPalavraChave().trim();
            String palavraChaveArmazenada = admin.getPalavraChave() != null ? admin.getPalavraChave().trim() : "";

            if (novaPalavraChaveRecebida.length() < 4) {
                throw new WeakPasswordException("Nova palavra-chave deve ter no mínimo 4 caracteres.");
            }
            if (novaPalavraChaveRecebida.equalsIgnoreCase(palavraChaveArmazenada)) {
            }
            admin.setPalavraChave(novaPalavraChaveRecebida);
            changed = true;
        }


        if (!changed) {
            throw new IllegalArgumentException("Nenhuma alteração fornecida ou os dados são iguais aos atuais.");
        }

        AdministradorEntity updatedAdmin = administradorRepository.save(admin);
        return administradorMapper.toDadosDTO(updatedAdmin);
    }
}