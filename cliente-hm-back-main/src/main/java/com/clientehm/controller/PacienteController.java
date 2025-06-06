package com.clientehm.controller;

import com.clientehm.exception.CpfAlreadyExistsException;
import com.clientehm.exception.EmailAlreadyExistsException;
import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.model.PacienteCreateDTO;
import com.clientehm.model.PacienteDTO;
import com.clientehm.model.PacienteUpdateDTO;
import com.clientehm.service.PacienteService;
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
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("mensagem", message);
        body.put("codigo", status.value());
        return ResponseEntity.status(status).body(body);
    }

    private ResponseEntity<Map<String, Object>> createSuccessResponse(Object data, String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("mensagem", message);
        body.put("codigo", status.value());
        if (data != null) {
            body.put("dados", data);
        }
        return ResponseEntity.status(status).body(body);
    }

    @PostMapping
    public ResponseEntity<?> criarPaciente(@Valid @RequestBody PacienteCreateDTO pacienteCreateDTO) {
        try {
            PacienteDTO pacienteCriado = pacienteService.criarPaciente(pacienteCreateDTO);
            return createSuccessResponse(pacienteCriado, "Paciente criado com sucesso.", HttpStatus.CREATED);
        } catch (CpfAlreadyExistsException | EmailAlreadyExistsException e) {
            return createErrorResponse(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<PacienteDTO>> listarPacientes(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(defaultValue = "nome,asc") String[] sort) {

        String sortField = sort.length > 0 ? sort[0] : "nome";
        String sortDirection = sort.length > 1 ? sort[1].toLowerCase() : "asc";

        Sort.Direction direction = "desc".equals(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortBy = Sort.by(direction, sortField);
        Pageable pageable = PageRequest.of(pagina, tamanho, sortBy);

        Page<PacienteDTO> pacientesPage = pacienteService.buscarTodosPacientes(pageable, nome, cpf);
        return ResponseEntity.ok(pacientesPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPacientePorId(@PathVariable Long id) {
        try {
            PacienteDTO pacienteDTO = pacienteService.buscarPacientePorId(id);
            return createSuccessResponse(pacienteDTO, "Paciente encontrado.", HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPaciente(@PathVariable Long id, @Valid @RequestBody PacienteUpdateDTO pacienteUpdateDTO) {
        try {
            PacienteDTO pacienteAtualizado = pacienteService.atualizarPaciente(id, pacienteUpdateDTO);
            return createSuccessResponse(pacienteAtualizado, "Paciente atualizado com sucesso.", HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EmailAlreadyExistsException e) {
            return createErrorResponse(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletarPaciente(@PathVariable Long id) {
        try {
            pacienteService.deletarPaciente(id);
            return createSuccessResponse(null, "Paciente deletado com sucesso.", HttpStatus.NO_CONTENT);
        } catch (ResourceNotFoundException e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        }
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

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CpfAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleCpfAlreadyExists(CpfAlreadyExistsException ex) {
        return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
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