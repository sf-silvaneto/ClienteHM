package com.clientehm.service;

import com.clientehm.model.VerifiedProfileUpdateRequestDTO;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdministradorService {

    private static final Logger logger = LoggerFactory.getLogger(AdministradorService.class);

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private boolean isStrongPassword(String password) {
        if (password == null) return false;
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{6,}$";
        return password.matches(regex);
    }

    public Map<String, Object> login(AdministradorLoginDTO loginDTO) {
        // ... (código existente, parece correto)
        logger.debug("Tentativa de login para o email: {}", loginDTO.getEmail());
        AdministradorEntity admin = administradorRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Falha no login: Email não encontrado - {}", loginDTO.getEmail());
                    return new InvalidCredentialsException("Credenciais inválidas");
                });

        if (!passwordEncoder.matches(loginDTO.getSenha(), admin.getSenha())) {
            logger.warn("Falha no login: Senha incorreta para o email - {}", loginDTO.getEmail());
            throw new InvalidCredentialsException("Credenciais inválidas");
        }

        String token = jwtUtil.generateToken(admin.getEmail());
        Map<String, Object> response = new HashMap<>();
        response.put("mensagem", "Login realizado com sucesso");
        response.put("token", token);
        response.put("nome", admin.getNome());
        response.put("email", admin.getEmail());
        logger.info("Login bem-sucedido para o email: {}", loginDTO.getEmail());
        return response;
    }

    public AdministradorEntity register(AdministradorRegistroDTO registroDTO) {
        // ... (código existente, parece correto)
        logger.debug("Tentativa de registro para o email: {}", registroDTO.getEmail());
        if (administradorRepository.findByEmail(registroDTO.getEmail()).isPresent()) {
            logger.warn("Falha no registro: Email já cadastrado - {}", registroDTO.getEmail());
            throw new EmailAlreadyExistsException("Email já cadastrado");
        }
        if (!isStrongPassword(registroDTO.getSenha())) {
            logger.warn("Falha no registro: Senha fraca para o email - {}", registroDTO.getEmail());
            throw new WeakPasswordException("Senha fraca! Use letras maiúsculas, minúsculas, número e símbolo.");
        }

        String encodedPassword = passwordEncoder.encode(registroDTO.getSenha());
        AdministradorEntity novoAdministrador = new AdministradorEntity(
                registroDTO.getNome(),
                registroDTO.getEmail(),
                encodedPassword,
                registroDTO.getPalavraChave().trim()
        );
        AdministradorEntity adminSalvo = administradorRepository.save(novoAdministrador);
        logger.info("Registro bem-sucedido para o email: {}", registroDTO.getEmail());
        return adminSalvo;
    }

    public boolean verifyKeyword(VerificarPalavraChaveDTO verificarDTO) {
        // ... (código existente, parece correto e lança exceção em falha)
        logger.debug("Verificando palavra-chave para o email: {}", verificarDTO.getEmail());
        AdministradorEntity admin = administradorRepository.findByEmail(verificarDTO.getEmail())
                .orElseThrow(() -> {
                    logger.warn("verifyKeyword: Email não encontrado - {}", verificarDTO.getEmail());
                    return new InvalidCredentialsException("Email ou palavra-chave incorretos");
                });

        String palavraChaveArmazenada = admin.getPalavraChave() != null ? admin.getPalavraChave().trim() : "";
        String palavraChaveFornecida = verificarDTO.getPalavraChave() != null ? verificarDTO.getPalavraChave().trim() : "";

        boolean match = palavraChaveFornecida.equalsIgnoreCase(palavraChaveArmazenada);

        if (!match) {
            logger.warn("verifyKeyword: Palavra-chave incorreta para o email - {}. Fornecida: '{}', Esperada: '{}'", verificarDTO.getEmail(), palavraChaveFornecida, palavraChaveArmazenada);
            throw new InvalidCredentialsException("Email ou palavra-chave incorretos");
        } else {
            logger.debug("verifyKeyword: Palavra-chave correta para o email - {}", verificarDTO.getEmail());
        }
        return true;
    }

    public AdministradorEntity resetPassword(RedefinirSenhaDTO redefinirDTO) {
        // ... (código existente, parece correto)
        logger.debug("Tentativa de redefinir senha para o email: {}", redefinirDTO.getEmail());
        AdministradorEntity admin = administradorRepository.findByEmail(redefinirDTO.getEmail())
                .orElseThrow(() -> {
                    logger.warn("resetPassword: Admin não encontrado para o email - {}", redefinirDTO.getEmail());
                    return new AdminNotFoundException("Administrador não encontrado");
                });

        if (!isStrongPassword(redefinirDTO.getNovaSenha())) {
            logger.warn("resetPassword: Nova senha fraca para o email - {}", redefinirDTO.getEmail());
            throw new WeakPasswordException("Senha fraca! Use letras maiúsculas, minúsculas, número e símbolo.");
        }

        admin.setSenha(passwordEncoder.encode(redefinirDTO.getNovaSenha()));
        AdministradorEntity adminAtualizado = administradorRepository.save(admin);
        logger.info("Senha redefinida com sucesso para o email: {}", redefinirDTO.getEmail());
        return adminAtualizado;
    }

    // MÉTODO PARA ATUALIZAR NOME, EMAIL E/OU PALAVRA-CHAVE APÓS VERIFICAÇÃO
    public AdministradorEntity updateVerifiedProfileDetails(String adminEmail, VerifiedProfileUpdateRequestDTO dto) {
        logger.info("SERVICE: Iniciando updateVerifiedProfileDetails para email: {}", adminEmail);
        AdministradorEntity admin = administradorRepository.findByEmail(adminEmail)
                .orElseThrow(() -> {
                    logger.warn("SERVICE updateVerifiedProfileDetails: Administrador não encontrado para email: {}", adminEmail);
                    return new AdminNotFoundException("Administrador não encontrado.");
                });
        logger.debug("SERVICE updateVerifiedProfileDetails: Administrador encontrado: {}", admin.getEmail());

        boolean changed = false;

        // Atualizar Nome
        if (dto.getNome() != null && !dto.getNome().trim().isEmpty()) {
            if (!dto.getNome().trim().equals(admin.getNome())) {
                if (dto.getNome().trim().length() < 3) {
                    logger.warn("SERVICE updateVerifiedProfileDetails: Nome inválido (<3) para {}: '{}'", adminEmail, dto.getNome());
                    throw new IllegalArgumentException("Nome deve ter pelo menos 3 caracteres.");
                }
                admin.setNome(dto.getNome().trim());
                logger.debug("SERVICE updateVerifiedProfileDetails: Nome atualizado para '{}'", dto.getNome().trim());
                changed = true;
            }
        }

        // Atualizar Email
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            if (!dto.getEmail().trim().equalsIgnoreCase(admin.getEmail())) {
                logger.debug("SERVICE updateVerifiedProfileDetails: Tentando atualizar email de '{}' para '{}'", admin.getEmail(), dto.getEmail().trim());
                String newEmailTrimmed = dto.getEmail().trim();
                if (administradorRepository.findByEmail(newEmailTrimmed).filter(otherAdmin -> !otherAdmin.getId().equals(admin.getId())).isPresent()) {
                    logger.warn("SERVICE updateVerifiedProfileDetails: Email '{}' já está em uso.", newEmailTrimmed);
                    throw new EmailAlreadyExistsException("Este email já está em uso por outro administrador.");
                }
                admin.setEmail(newEmailTrimmed);
                logger.debug("SERVICE updateVerifiedProfileDetails: Email atualizado para '{}'", newEmailTrimmed);
                changed = true;
            }
        }

        // Atualizar Palavra-Chave
        if (dto.getNovaPalavraChave() != null && !dto.getNovaPalavraChave().trim().isEmpty()) {
            String novaPalavraChaveRecebida = dto.getNovaPalavraChave().trim();
            String palavraChaveArmazenada = admin.getPalavraChave() != null ? admin.getPalavraChave().trim() : "";

            logger.debug("SERVICE updateVerifiedProfileDetails: Tentando atualizar palavra-chave. Nova: '{}'", novaPalavraChaveRecebida);

            if (novaPalavraChaveRecebida.length() < 4) {
                logger.warn("SERVICE updateVerifiedProfileDetails: Nova palavra-chave inválida (<4 caracteres) para {}: '{}'", adminEmail, novaPalavraChaveRecebida);
                throw new WeakPasswordException("Nova palavra-chave deve ter no mínimo 4 caracteres.");
            }
            if (novaPalavraChaveRecebida.equalsIgnoreCase(palavraChaveArmazenada)) {
                logger.warn("SERVICE updateVerifiedProfileDetails: Nova palavra-chave é igual à atual para {}.", adminEmail);
                throw new IllegalArgumentException("A nova palavra-chave não pode ser igual à palavra-chave de recuperação atual.");
            }
            admin.setPalavraChave(novaPalavraChaveRecebida);
            logger.debug("SERVICE updateVerifiedProfileDetails: Palavra-chave atualizada para '{}'", novaPalavraChaveRecebida);
            changed = true;
        }

        if (!changed) {
            logger.info("SERVICE updateVerifiedProfileDetails: Nenhuma alteração solicitada ou dados idênticos para {}.", adminEmail);
            throw new IllegalArgumentException("Nenhuma alteração fornecida ou os dados são iguais aos atuais.");
        }

        logger.info("SERVICE: Dados do perfil (nome/email/palavra-chave) atualizados com sucesso para {}.", adminEmail);
        return administradorRepository.save(admin);
    }
}