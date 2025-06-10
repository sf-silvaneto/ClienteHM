package com.clientehm.controller;

import com.clientehm.model.AdministradorLoginDTO;
import com.clientehm.model.AdministradorRegistroDTO;
import com.clientehm.model.RedefinirSenhaDTO;
import com.clientehm.model.VerificarPalavraChaveDTO;
import com.clientehm.model.VerifiedProfileUpdateRequestDTO;
import com.clientehm.model.dto.AdministradorDadosDTO;
import com.clientehm.service.AdministradorService;
import com.clientehm.mapper.AdministradorMapper;
import com.clientehm.entity.AdministradorEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/administradores")
public class AdministradorController {

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private AdministradorMapper administradorMapper;

    private ResponseEntity<Map<String, Object>> createSuccessResponse(HttpStatus status, String message, Object data) {
        Map<String, Object> body = new HashMap<>();
        body.put("mensagem", message);
        body.put("codigo", status.value());
        if (data != null) {
            body.put("dados", data);
        }
        return ResponseEntity.status(status).body(body);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AdministradorLoginDTO loginDTO) {
        Map<String, Object> loginData = administradorService.login(loginDTO);
        return ResponseEntity.status(HttpStatus.OK).body(loginData);
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@Valid @RequestBody AdministradorRegistroDTO registroDTO) {
        AdministradorDadosDTO adminDTO = administradorService.register(registroDTO);
        return createSuccessResponse(HttpStatus.CREATED, "Administrador registrado com sucesso", adminDTO);
    }

    @PostMapping("/verificar-palavra-chave")
    public ResponseEntity<?> verificarPalavraChave(@Valid @RequestBody VerificarPalavraChaveDTO verificarDTO) {
        administradorService.verifyKeyword(verificarDTO);
        return createSuccessResponse(HttpStatus.OK, "Palavra-chave correta.", null);
    }

    @PutMapping("/redefinir-senha")
    public ResponseEntity<?> redefinirSenha(@Valid @RequestBody RedefinirSenhaDTO redefinirDTO) {
        administradorService.resetPassword(redefinirDTO);
        return createSuccessResponse(HttpStatus.OK, "Senha alterada com sucesso.", null);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentAdmin(@AuthenticationPrincipal AdministradorEntity admin) {
        if (admin == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("mensagem", "Nenhum administrador autenticado encontrado.");
            body.put("codigo", HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }
        AdministradorDadosDTO adminDataDTO = administradorMapper.toDadosDTO(admin);

        Map<String, Object> response = new HashMap<>();
        response.put("mensagem", "Dados do administrador recuperados com sucesso");
        response.put("adminData", adminDataDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile/verified-update")
    public ResponseEntity<?> updateVerifiedProfileDetails(
            @AuthenticationPrincipal AdministradorEntity currentAdmin,
            @Valid @RequestBody VerifiedProfileUpdateRequestDTO dto) {
        AdministradorDadosDTO updatedAdminDTO = administradorService.updateVerifiedProfileDetails(currentAdmin.getEmail(), dto);

        Map<String, Object> response = new HashMap<>();
        response.put("mensagem", "Dados atualizados com sucesso.");
        response.put("adminData", updatedAdminDTO);
        return ResponseEntity.ok(response);
    }
}