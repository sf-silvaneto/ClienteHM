package com.clientehm.controller;

import com.clientehm.entity.AdministradorEntity;
import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.model.*; // Seus DTOs de request e response
import com.clientehm.service.ProntuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/prontuarios")
public class ProntuarioController {

    private static final Logger logger = LoggerFactory.getLogger(ProntuarioController.class);

    @Autowired
    private ProntuarioService prontuarioService;

    // Os métodos conversores foram removidos daqui e agora estão nos Mappers,
    // e o ProntuarioService já retorna DTOs.

    @GetMapping
    public ResponseEntity<Page<ProntuarioDTO>> buscarProntuariosPaginado(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) String numeroProntuario,
            @RequestParam(defaultValue = "dataUltimaAtualizacao,desc") String[] sort) {

        String sortField = sort[0];
        String sortDirection = sort.length > 1 ? sort[1] : "desc";
        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Sort sortBy = Sort.by(direction, sortField);
        Pageable pageable = PageRequest.of(pagina, tamanho, sortBy);

        Page<ProntuarioDTO> prontuariosPageDTO = prontuarioService.buscarTodosProntuarios(pageable, termo, numeroProntuario);
        return ResponseEntity.ok(prontuariosPageDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProntuarioDTO> buscarProntuarioPorId(@PathVariable Long id) {
        ProntuarioDTO prontuarioDTO = prontuarioService.buscarProntuarioPorIdDetalhado(id);
        return ResponseEntity.ok(prontuarioDTO);
    }

    @PostMapping("/consultas")
    public ResponseEntity<?> adicionarConsulta(
            @RequestParam Long pacienteId,
            @RequestParam Long medicoExecutorId,
            @Valid @RequestBody CriarConsultaRequestDTO consultaDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {
        if (adminLogado == null) return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        try {
            ConsultaDTO consultaSalvaDTO = prontuarioService.adicionarConsultaERetornarDTO(pacienteId, consultaDTO, adminLogado, medicoExecutorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(consultaSalvaDTO);
        } catch (ResourceNotFoundException e) { return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) { return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) { logger.error("Erro inesperado ao adicionar consulta:", e); return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao adicionar consulta."); }
    }

    @PostMapping("/exames")
    public ResponseEntity<?> adicionarExame(
            @RequestParam Long pacienteId,
            @RequestParam Long medicoResponsavelExameId,
            @Valid @RequestBody CriarExameRequestDTO exameDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {
        if (adminLogado == null) return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        try {
            ExameRegistroDTO exameSalvoDTO = prontuarioService.adicionarExameERetornarDTO(pacienteId, exameDTO, adminLogado, medicoResponsavelExameId);
            return ResponseEntity.status(HttpStatus.CREATED).body(exameSalvoDTO);
        } catch (ResourceNotFoundException e) { return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) { return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) { logger.error("Erro inesperado ao adicionar exame:", e); return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao adicionar exame.");}
    }

    @PostMapping("/procedimentos")
    public ResponseEntity<?> adicionarProcedimento(
            @RequestParam Long pacienteId,
            @Valid @RequestBody CriarProcedimentoRequestDTO procedimentoDTO, // medicoExecutorId está dentro do DTO
            @AuthenticationPrincipal AdministradorEntity adminLogado) {
        if (adminLogado == null) return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        try {
            ProcedimentoRegistroDTO procedimentoSalvoDTO = prontuarioService.adicionarProcedimentoERetornarDTO(pacienteId, procedimentoDTO, adminLogado);
            return ResponseEntity.status(HttpStatus.CREATED).body(procedimentoSalvoDTO);
        } catch (ResourceNotFoundException e) { return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) { return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) { logger.error("Erro inesperado ao adicionar procedimento:", e); return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao adicionar procedimento.");}
    }

    @PostMapping("/encaminhamentos")
    public ResponseEntity<?> adicionarEncaminhamento(
            @RequestParam Long pacienteId,
            @Valid @RequestBody CriarEncaminhamentoRequestDTO encaminhamentoDTO, // medicoSolicitanteId está dentro do DTO
            @AuthenticationPrincipal AdministradorEntity adminLogado) {
        if (adminLogado == null) return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        try {
            EncaminhamentoRegistroDTO encaminhamentoSalvoDTO = prontuarioService.adicionarEncaminhamentoERetornarDTO(pacienteId, encaminhamentoDTO, adminLogado);
            return ResponseEntity.status(HttpStatus.CREATED).body(encaminhamentoSalvoDTO);
        } catch (ResourceNotFoundException e) { return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) { return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) { logger.error("Erro inesperado ao adicionar encaminhamento:", e); return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao adicionar encaminhamento.");}
    }

    @PutMapping("/consultas/{consultaId}")
    public ResponseEntity<?> atualizarConsulta(
            @PathVariable Long consultaId,
            @Valid @RequestBody AtualizarConsultaRequestDTO consultaDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {
        if (adminLogado == null) return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        try {
            ConsultaDTO consultaAtualizadaDTO = prontuarioService.atualizarConsultaERetornarDTO(consultaId, consultaDTO, adminLogado);
            return ResponseEntity.ok(consultaAtualizadaDTO);
        } catch (ResourceNotFoundException e) { return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) { return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) { logger.error("Erro inesperado ao atualizar consulta {}: ", consultaId, e); return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao atualizar consulta.");}
    }

    @PutMapping("/exames/{exameId}")
    public ResponseEntity<?> atualizarExame(
            @PathVariable Long exameId,
            @Valid @RequestBody AtualizarExameRequestDTO exameDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {
        if (adminLogado == null) return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        try {
            ExameRegistroDTO exameAtualizadoDTO = prontuarioService.atualizarExameERetornarDTO(exameId, exameDTO, adminLogado);
            return ResponseEntity.ok(exameAtualizadoDTO);
        } catch (ResourceNotFoundException e) { return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) { return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) { logger.error("Erro inesperado ao atualizar exame {}: ", exameId, e); return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao atualizar exame.");}
    }

    @PutMapping("/procedimentos/{procedimentoId}")
    public ResponseEntity<?> atualizarProcedimento(
            @PathVariable Long procedimentoId,
            @Valid @RequestBody AtualizarProcedimentoRequestDTO procedimentoDTO, // medicoExecutorId está dentro do DTO
            @AuthenticationPrincipal AdministradorEntity adminLogado) {
        if (adminLogado == null) return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        try {
            ProcedimentoRegistroDTO procedimentoAtualizadoDTO = prontuarioService.atualizarProcedimentoERetornarDTO(procedimentoId, procedimentoDTO, adminLogado);
            return ResponseEntity.ok(procedimentoAtualizadoDTO);
        } catch (ResourceNotFoundException e) { return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) { return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) { logger.error("Erro inesperado ao atualizar procedimento {}: ", procedimentoId, e); return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao atualizar procedimento.");}
    }

    @PutMapping("/encaminhamentos/{encaminhamentoId}")
    public ResponseEntity<?> atualizarEncaminhamento(
            @PathVariable Long encaminhamentoId,
            @Valid @RequestBody AtualizarEncaminhamentoRequestDTO encaminhamentoDTO, // medicoSolicitanteId está dentro do DTO
            @AuthenticationPrincipal AdministradorEntity adminLogado) {
        if (adminLogado == null) return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        try {
            EncaminhamentoRegistroDTO encaminhamentoAtualizadoDTO = prontuarioService.atualizarEncaminhamentoERetornarDTO(encaminhamentoId, encaminhamentoDTO, adminLogado);
            return ResponseEntity.ok(encaminhamentoAtualizadoDTO);
        } catch (ResourceNotFoundException e) { return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) { return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) { logger.error("Erro inesperado ao atualizar encaminhamento {}: ", encaminhamentoId, e); return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao atualizar encaminhamento.");}
    }

    @PutMapping("/{id}/dados-basicos")
    public ResponseEntity<?> atualizarDadosBasicosProntuario(
            @PathVariable Long id,
            @Valid @RequestBody ProntuarioUpdateDadosBasicosDTO updateDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {
        if (adminLogado == null) return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        try {
            ProntuarioDTO prontuarioAtualizadoDTO = prontuarioService.atualizarDadosBasicosProntuarioERetornarDTO(id, updateDTO.getMedicoResponsavelId());
            return ResponseEntity.ok(prontuarioAtualizadoDTO);
        } catch (ResourceNotFoundException e) { return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) { return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) { logger.error("Erro inesperado ao atualizar dados básicos do prontuário {}: ", id, e); return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao atualizar dados básicos do prontuário.");}
    }

    // --- HANDLERS DE EXCEÇÃO E MÉTODO createErrorResponse ---
    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("mensagem", message);
        body.put("codigo", status.value());
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        logger.warn("Erro de validação nos dados da requisição: {}", errors);
        Map<String, Object> body = new HashMap<>();
        body.put("mensagem", "Erro de validação nos dados fornecidos");
        body.put("codigo", HttpStatus.BAD_REQUEST.value());
        body.put("erros", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.warn("ResourceNotFoundException: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("IllegalArgumentException: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logger.error("Exceção genérica não tratada no ProntuarioController:", ex);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado no servidor.");
    }
}