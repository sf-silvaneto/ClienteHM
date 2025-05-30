package com.clientehm.controller;

import com.clientehm.model.AdministradorLoginDTO;
import com.clientehm.model.AdministradorRegistroDTO;
import com.clientehm.model.RedefinirSenhaDTO;
import com.clientehm.model.VerificarPalavraChaveDTO;
import com.clientehm.service.AdministradorService; // Import para o serviço

// IMPORTS ATUALIZADOS para as exceções do pacote com.clientehm.exception
import com.clientehm.exception.AdminNotFoundException;
import com.clientehm.exception.InvalidCredentialsException;
import com.clientehm.exception.EmailAlreadyExistsException;
import com.clientehm.exception.WeakPasswordException;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;
// O import java.util.stream.Collectors não está sendo usado neste arquivo, pode ser removido se desejar.

@RestController
@RequestMapping("/api/administradores")
// Você ajustou o CORS para localhost:8080, o que é bom para desenvolvimento se seu frontend estiver em outra porta
// ou se você precisar permitir acesso de scripts/ferramentas rodando nessa origem.
public class AdministradorController {

    @Autowired
    private AdministradorService administradorService;

    // Método utilitário para criar respostas (renomeado para inglês)
    private ResponseEntity<Map<String, Object>> createResponse(HttpStatus status, String message, Map<String, Object> additionalData) {
        Map<String, Object> body = new HashMap<>();
        body.put("mensagem", message);
        body.put("codigo", status.value());
        if (additionalData != null) {
            body.putAll(additionalData);
        }
        return ResponseEntity.status(status).body(body);
    }

    private ResponseEntity<Map<String, Object>> createSuccessResponse(HttpStatus status, String message, Map<String, Object> additionalData) {
        return createResponse(status, message, additionalData);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, String message) {
        return createResponse(status, message, null);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AdministradorLoginDTO loginDTO) {
        Map<String, Object> loginData = administradorService.login(loginDTO);
        // Adiciona o código HTTP ao mapa retornado pelo serviço para a resposta final
        return createSuccessResponse(HttpStatus.OK, (String) loginData.remove("mensagem"), loginData);
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@Valid @RequestBody AdministradorRegistroDTO registroDTO) {
        administradorService.register(registroDTO);
        return createSuccessResponse(HttpStatus.CREATED, "Administrador registrado com sucesso", null);
    }

    @PostMapping("/verificar-palavra-chave")
    public ResponseEntity<?> verificarPalavraChave(@Valid @RequestBody VerificarPalavraChaveDTO verificarDTO) {
        boolean isCorrect = administradorService.verifyKeyword(verificarDTO);
        if (isCorrect) {
            return createSuccessResponse(HttpStatus.OK, "Palavra-chave correta", null);
        } else {
            // Mude de HttpStatus.UNAUTHORIZED para HttpStatus.BAD_REQUEST
            return createErrorResponse(HttpStatus.BAD_REQUEST, "Email ou palavra-chave incorretos.");
        }
    }

    @PutMapping("/redefinir-senha")
    public ResponseEntity<?> redefinirSenha(@Valid @RequestBody RedefinirSenhaDTO redefinirDTO) {
        administradorService.resetPassword(redefinirDTO);
        return createSuccessResponse(HttpStatus.OK, "Senha alterada com sucesso", null);
    }

    // Manipulador de exceções para erros de validação do Bean Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("mensagem", "Erro de validação");
        responseBody.put("codigo", HttpStatus.BAD_REQUEST.value());
        responseBody.put("erros", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    @ExceptionHandler(AdminNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAdminNotFound(AdminNotFoundException ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailExists(EmailAlreadyExistsException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(WeakPasswordException.class)
    public ResponseEntity<Map<String, Object>> handleWeakPassword(WeakPasswordException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Um manipulador genérico para outras exceções não tratadas pode ser útil
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        // É uma boa prática logar a exceção aqui para depuração
        // ex: logger.error("Ocorreu um erro inesperado:", ex);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado.");
    }
}