package com.clientehm.controller;

import com.clientehm.model.AdministradorLoginDTO;
import com.clientehm.model.AdministradorRegistroDTO;
import com.clientehm.model.RedefinirSenhaDTO;
import com.clientehm.model.VerificarPalavraChaveDTO;
import com.clientehm.model.VerifiedProfileUpdateRequestDTO;
import com.clientehm.model.dto.AdministradorDadosDTO; // Importar DTO
import com.clientehm.service.AdministradorService;
import com.clientehm.mapper.AdministradorMapper; // Importar Mapper
import com.clientehm.exception.AdminNotFoundException;
import com.clientehm.exception.InvalidCredentialsException;
import com.clientehm.exception.EmailAlreadyExistsException;
import com.clientehm.exception.WeakPasswordException;
import com.clientehm.entity.AdministradorEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/administradores")
public class AdministradorController {

    private static final Logger logger = LoggerFactory.getLogger(AdministradorController.class);

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private AdministradorMapper administradorMapper; // Injetar o Mapper

    private ResponseEntity<Map<String, Object>> createResponse(HttpStatus status, String message, Map<String, Object> additionalData) {
        Map<String, Object> body = new HashMap<>();
        body.put("mensagem", message);
        body.put("codigo", status.value());
        if (additionalData != null) {
            body.putAll(additionalData);
        }
        return ResponseEntity.status(status).body(body);
    }

    private ResponseEntity<Map<String, Object>> createSuccessResponse(HttpStatus status, String message, Object data) {
        Map<String, Object> body = new HashMap<>();
        body.put("mensagem", message);
        body.put("codigo", status.value());
        if (data != null) {
            body.put("dados", data);
        }
        return ResponseEntity.status(status).body(body);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, String message) {
        return createResponse(status, message, null);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AdministradorLoginDTO loginDTO) {
        logger.info("CONTROLLER: Recebida requisição POST para /login com email: {}", loginDTO.getEmail());
        // O serviço já retorna o Map formatado pelo mapper
        Map<String, Object> loginData = administradorService.login(loginDTO);
        // A mensagem já está incluída no loginData pelo mapper/serviço
        return ResponseEntity.status(HttpStatus.OK).body(loginData);
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@Valid @RequestBody AdministradorRegistroDTO registroDTO) {
        logger.info("CONTROLLER: Recebida requisição POST para /registrar com email: {}", registroDTO.getEmail());
        AdministradorDadosDTO adminDTO = administradorService.register(registroDTO);
        return createSuccessResponse(HttpStatus.CREATED, "Administrador registrado com sucesso", adminDTO);
    }

    @PostMapping("/verificar-palavra-chave")
    public ResponseEntity<?> verificarPalavraChave(@Valid @RequestBody VerificarPalavraChaveDTO verificarDTO) {
        logger.info("CONTROLLER: Recebida requisição POST para /verificar-palavra-chave para o email: {}", verificarDTO.getEmail());
        administradorService.verifyKeyword(verificarDTO); // Serviço retorna boolean, mas o controller já trata o sucesso
        return createSuccessResponse(HttpStatus.OK, "Palavra-chave correta.", null);
    }

    @PutMapping("/redefinir-senha")
    public ResponseEntity<?> redefinirSenha(@Valid @RequestBody RedefinirSenhaDTO redefinirDTO) {
        logger.info("CONTROLLER: Recebida requisição PUT para /redefinir-senha para o email: {}", redefinirDTO.getEmail());
        administradorService.resetPassword(redefinirDTO); // Serviço agora pode retornar DTO, mas o controller só precisa do sucesso
        return createSuccessResponse(HttpStatus.OK, "Senha alterada com sucesso.", null);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentAdmin(@AuthenticationPrincipal AdministradorEntity admin) {
        logger.info("CONTROLLER: Recebida requisição GET para /me");
        if (admin == null) {
            logger.warn("CONTROLLER /me: Nenhum administrador autenticado encontrado na sessão.");
            return createErrorResponse(HttpStatus.UNAUTHORIZED, "Nenhum administrador autenticado encontrado.");
        }
        logger.info("CONTROLLER /me: Administrador autenticado: {}", admin.getEmail());
        AdministradorDadosDTO adminDataDTO = administradorMapper.toDadosDTO(admin); // Usar o mapper

        Map<String, Object> response = new HashMap<>();
        response.put("mensagem", "Dados do administrador recuperados com sucesso");
        response.put("adminData", adminDataDTO); // Retornar o DTO padronizado
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile/verified-update")
    public ResponseEntity<?> updateVerifiedProfileDetails(
            @AuthenticationPrincipal AdministradorEntity currentAdmin,
            @Valid @RequestBody VerifiedProfileUpdateRequestDTO dto) {
        logger.info("CONTROLLER: Recebida requisição PUT para /profile/verified-update para o usuário: {}", currentAdmin.getEmail());

        AdministradorDadosDTO updatedAdminDTO = administradorService.updateVerifiedProfileDetails(currentAdmin.getEmail(), dto);

        Map<String, Object> response = new HashMap<>();
        response.put("mensagem", "Dados atualizados com sucesso.");
        response.put("adminData", updatedAdminDTO);
        logger.info("CONTROLLER: Dados (nome/email/palavra-chave) atualizados com sucesso via verified-update para: {}", currentAdmin.getEmail());
        return ResponseEntity.ok(response);
    }

    // Exception Handlers permanecem os mesmos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        logger.warn("Erro de validação nos dados da requisição: {}", errors, ex);

        Map<String, Object> body = new HashMap<>();
        body.put("mensagem", "Erro de validação nos dados fornecidos");
        body.put("codigo", HttpStatus.BAD_REQUEST.value());
        body.put("erros", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(AdminNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAdminNotFound(AdminNotFoundException ex) {
        logger.warn("AdminNotFoundException: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        logger.warn("InvalidCredentialsException: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(WeakPasswordException.class)
    public ResponseEntity<Map<String, Object>> handleWeakPassword(WeakPasswordException ex) {
        logger.warn("WeakPasswordException: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailExists(EmailAlreadyExistsException ex) {
        logger.warn("EmailAlreadyExistsException: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("IllegalArgumentException: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logger.error("Exceção genérica não tratada no AdministradorController:", ex);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado no servidor.");
    }
}