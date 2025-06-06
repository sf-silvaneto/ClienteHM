package com.clientehm.controller;

import com.clientehm.entity.StatusMedico;
import com.clientehm.exception.CrmAlreadyExistsException;
import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.model.MedicoCreateDTO;
import com.clientehm.model.MedicoDTO;
import com.clientehm.model.MedicoUpdateDTO;
import com.clientehm.model.StatusUpdateDTO;
import com.clientehm.service.MedicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("mensagem", message);
        body.put("codigo", status.value());
        return ResponseEntity.status(status).body(body);
    }

    @PostMapping
    public ResponseEntity<MedicoDTO> criarMedico(@Valid @RequestBody MedicoCreateDTO medicoCreateDTO) {
        MedicoDTO medicoCriado = medicoService.criarMedico(medicoCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(medicoCriado);
    }

    @GetMapping
    public ResponseEntity<Page<MedicoDTO>> listarMedicos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String crm,
            @RequestParam(required = false) String especialidade,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "nomeCompleto,asc") String[] sort) {

        String sortField = sort.length > 0 ? sort[0] : "nomeCompleto";
        String sortDirection = sort.length > 1 ? sort[1] : "asc";

        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Sort sortBy = Sort.by(direction, sortField);
        Pageable pageable = PageRequest.of(pagina, tamanho, sortBy);

        Page<MedicoDTO> medicosPage;

        if (nome != null && !nome.isEmpty()) {
            medicosPage = medicoService.buscarMedicosPorNome(nome, pageable);
        } else if (crm != null && !crm.isEmpty()) {
            medicosPage = medicoService.buscarMedicosPorCrm(crm, pageable);
        } else if (especialidade != null && !especialidade.isEmpty()) {
            medicosPage = medicoService.buscarMedicosPorEspecialidade(especialidade, pageable);
        } else if (status != null && !status.isEmpty()) {
            try {
                StatusMedico statusEnum = StatusMedico.valueOf(status.toUpperCase());
                medicosPage = medicoService.buscarMedicosPorStatus(statusEnum, pageable);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            medicosPage = medicoService.buscarTodosMedicos(pageable);
        }
        return ResponseEntity.ok(medicosPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicoDTO> buscarMedicoPorId(@PathVariable Long id) {
        MedicoDTO medicoDTO = medicoService.buscarMedicoPorId(id);
        return ResponseEntity.ok(medicoDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicoDTO> atualizarMedico(@PathVariable Long id, @Valid @RequestBody MedicoUpdateDTO medicoUpdateDTO) {
        MedicoDTO medicoAtualizado = medicoService.atualizarMedico(id, medicoUpdateDTO);
        return ResponseEntity.ok(medicoAtualizado);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MedicoDTO> atualizarStatusMedico(@PathVariable Long id, @Valid @RequestBody StatusUpdateDTO statusUpdateDTO) {
        MedicoDTO medicoAtualizado = medicoService.atualizarStatusMedico(id, statusUpdateDTO.getStatus());
        return ResponseEntity.ok(medicoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMedico(@PathVariable Long id) {
        medicoService.deletarMedico(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(CrmAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleCrmAlreadyExists(CrmAlreadyExistsException ex) {
        return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        Map<String, Object> body = new HashMap<>();
        body.put("mensagem", "Erro de validação nos dados fornecidos");
        body.put("codigo", HttpStatus.BAD_REQUEST.value());
        body.put("erros", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado no servidor.");
    }
}