package com.main.api.controller;

import com.main.api.model.AdministradorLoginDTO;
import com.main.api.model.AdministradorRegistroDTO;
import com.main.api.model.RedefinirSenhaDTO;
import com.main.api.model.VerificarPalavraChaveDTO;
import com.main.api.model.VerifiedProfileUpdateRequestDTO;
import com.main.api.model.AdministradorDadosDTO;
import com.main.domain.service.AdministradorService;
import com.main.mapper.AdministradorMapper;
import com.main.domain.entity.AdministradorEntity;
import com.main.util.ApiResponseUtil;
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

    @Autowired
    private ApiResponseUtil apiResponseUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AdministradorLoginDTO loginDTO) {
        Map<String, Object> loginData = administradorService.login(loginDTO);
        return ResponseEntity.status(HttpStatus.OK).body(loginData);
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@Valid @RequestBody AdministradorRegistroDTO registroDTO) {
        AdministradorDadosDTO adminDTO = administradorService.register(registroDTO);
        return apiResponseUtil.createSuccessResponse(HttpStatus.CREATED, "Administrador registrado com sucesso", adminDTO);
    }

    @PostMapping("/verificar-palavra-chave")
    public ResponseEntity<?> verificarPalavraChave(@Valid @RequestBody VerificarPalavraChaveDTO verificarDTO) {
        administradorService.verifyKeyword(verificarDTO);
        return apiResponseUtil.createSuccessResponse(HttpStatus.OK, "Palavra-chave correta.", null);
    }

    @PutMapping("/redefinir-senha")
    public ResponseEntity<?> redefinirSenha(@Valid @RequestBody RedefinirSenhaDTO redefinirDTO) {
        administradorService.resetPassword(redefinirDTO);
        return apiResponseUtil.createSuccessResponse(HttpStatus.OK, "Senha alterada com sucesso.", null);
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