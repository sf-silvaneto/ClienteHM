package com.clientehm.controller;

import com.clientehm.entity.AdministradorEntity;
import com.clientehm.model.*;
import com.clientehm.service.ProntuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/prontuarios")
public class ProntuarioController {

    @Autowired
    private ProntuarioService prontuarioService;

    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("mensagem", message);
        body.put("codigo", status.value());
        return ResponseEntity.status(status).body(body);
    }

    @GetMapping
    public ResponseEntity<Page<ProntuarioDTO>> buscarProntuariosPaginado(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) String numeroProntuario,
            @RequestParam(defaultValue = "updatedAt,desc") String[] sort) {

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
        ConsultaDTO consultaSalvaDTO = prontuarioService.adicionarConsultaERetornarDTO(pacienteId, consultaDTO, adminLogado, medicoExecutorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(consultaSalvaDTO);
    }

    @PostMapping("/exames")
    public ResponseEntity<?> adicionarExame(
            @RequestParam Long pacienteId,
            @RequestParam Long medicoResponsavelExameId,
            @Valid @RequestBody CriarExameRequestDTO exameDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {
        if (adminLogado == null) return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        ExameRegistroDTO exameSalvoDTO = prontuarioService.adicionarExameERetornarDTO(pacienteId, exameDTO, adminLogado, medicoResponsavelExameId);
        return ResponseEntity.status(HttpStatus.CREATED).body(exameSalvoDTO);
    }

    @PostMapping("/procedimentos")
    public ResponseEntity<?> adicionarProcedimento(
            @RequestParam Long pacienteId,
            @Valid @RequestBody CriarProcedimentoRequestDTO procedimentoDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {
        if (adminLogado == null) return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        ProcedimentoRegistroDTO procedimentoSalvoDTO = prontuarioService.adicionarProcedimentoERetornarDTO(pacienteId, procedimentoDTO, adminLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(procedimentoSalvoDTO);
    }

    @PostMapping("/encaminhamentos")
    public ResponseEntity<?> adicionarEncaminhamento(
            @RequestParam Long pacienteId,
            @Valid @RequestBody CriarEncaminhamentoRequestDTO encaminhamentoDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {
        if (adminLogado == null) return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        EncaminhamentoRegistroDTO encaminhamentoSalvoDTO = prontuarioService.adicionarEncaminhamentoERetornarDTO(pacienteId, encaminhamentoDTO, adminLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(encaminhamentoSalvoDTO);
    }

    @PutMapping("/consultas/{consultaId}")
    public ResponseEntity<?> atualizarConsulta(
            @PathVariable Long consultaId,
            @Valid @RequestBody AtualizarConsultaRequestDTO consultaDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {
        if (adminLogado == null) return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        ConsultaDTO consultaAtualizadaDTO = prontuarioService.atualizarConsultaERetornarDTO(consultaId, consultaDTO, adminLogado);
        return ResponseEntity.ok(consultaAtualizadaDTO);
    }

    @PutMapping("/exames/{exameId}")
    public ResponseEntity<?> atualizarExame(
            @PathVariable Long exameId,
            @Valid @RequestBody AtualizarExameRequestDTO exameDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {
        if (adminLogado == null) return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        ExameRegistroDTO exameAtualizadoDTO = prontuarioService.atualizarExameERetornarDTO(exameId, exameDTO, adminLogado);
        return ResponseEntity.ok(exameAtualizadoDTO);
    }

    @PutMapping("/procedimentos/{procedimentoId}")
    public ResponseEntity<?> atualizarProcedimento(
            @PathVariable Long procedimentoId,
            @Valid @RequestBody AtualizarProcedimentoRequestDTO procedimentoDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {
        if (adminLogado == null) return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        ProcedimentoRegistroDTO procedimentoAtualizadoDTO = prontuarioService.atualizarProcedimentoERetornarDTO(procedimentoId, procedimentoDTO, adminLogado);
        return ResponseEntity.ok(procedimentoAtualizadoDTO);
    }

    @PutMapping("/encaminhamentos/{encaminhamentoId}")
    public ResponseEntity<?> atualizarEncaminhamento(
            @PathVariable Long encaminhamentoId,
            @Valid @RequestBody AtualizarEncaminhamentoRequestDTO encaminhamentoDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {
        if (adminLogado == null) return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        EncaminhamentoRegistroDTO encaminhamentoAtualizadoDTO = prontuarioService.atualizarEncaminhamentoERetornarDTO(encaminhamentoId, encaminhamentoDTO, adminLogado);
        return ResponseEntity.ok(encaminhamentoAtualizadoDTO);
    }

    @PutMapping("/{id}/dados-basicos")
    public ResponseEntity<?> atualizarDadosBasicosProntuario(
            @PathVariable Long id,
            @Valid @RequestBody ProntuarioUpdateDadosBasicosDTO updateDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {
        if (adminLogado == null) return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        ProntuarioDTO prontuarioAtualizadoDTO = prontuarioService.atualizarDadosBasicosProntuarioERetornarDTO(id, updateDTO.getMedicoResponsavelId());
        return ResponseEntity.ok(prontuarioAtualizadoDTO);
    }
}