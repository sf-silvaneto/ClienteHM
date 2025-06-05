package com.clientehm.controller;

import com.clientehm.exception.CpfAlreadyExistsException;
import com.clientehm.exception.EmailAlreadyExistsException;
import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.model.PacienteCreateDTO;
import com.clientehm.model.PacienteDTO; // Já espera DTO
import com.clientehm.model.PacienteUpdateDTO;
import com.clientehm.service.PacienteService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private static final Logger logger = LoggerFactory.getLogger(PacienteController.class);

    @Autowired
    private PacienteService pacienteService;

    // PacienteMapper não precisa ser injetado aqui se o serviço já retorna DTOs

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
        if (data != null) { // Adicionado para não incluir a chave "dados" se data for null
            body.put("dados", data);
        }
        return ResponseEntity.status(status).body(body);
    }

    @PostMapping
    public ResponseEntity<?> criarPaciente(@Valid @RequestBody PacienteCreateDTO pacienteCreateDTO) {
        logger.info("CONTROLLER: Recebida requisição POST para /api/pacientes");
        try {
            PacienteDTO pacienteCriado = pacienteService.criarPaciente(pacienteCreateDTO); // Serviço retorna DTO
            return createSuccessResponse(pacienteCriado, "Paciente criado com sucesso.", HttpStatus.CREATED);
        } catch (CpfAlreadyExistsException | EmailAlreadyExistsException e) {
            logger.warn("CONTROLLER: Erro ao criar paciente: {}", e.getMessage());
            return createErrorResponse(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("CONTROLLER: Erro de argumento ao criar paciente: {}", e.getMessage());
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        // Não é necessário um catch genérico aqui se os handlers de exceção globais do controller cuidarem disso
    }

    @GetMapping
    public ResponseEntity<Page<PacienteDTO>> listarPacientes(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(defaultValue = "nome,asc") String[] sort) {

        logger.info("CONTROLLER: GET /api/pacientes - pagina={}, tamanho={}, nome={}, cpf={}, sort={}",
                pagina, tamanho, nome, cpf, sort);

        String sortField = sort.length > 0 ? sort[0] : "nome";
        String sortDirection = sort.length > 1 ? sort[1].toLowerCase() : "asc";

        Sort.Direction direction = "desc".equals(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortBy = Sort.by(direction, sortField);
        Pageable pageable = PageRequest.of(pagina, tamanho, sortBy);

        Page<PacienteDTO> pacientesPage = pacienteService.buscarTodosPacientes(pageable, nome, cpf); // Serviço retorna Page<DTO>
        return ResponseEntity.ok(pacientesPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPacientePorId(@PathVariable Long id) {
        logger.info("CONTROLLER: Recebida requisição GET para /api/pacientes/{}", id);
        try {
            PacienteDTO pacienteDTO = pacienteService.buscarPacientePorId(id); // Serviço retorna DTO
            return createSuccessResponse(pacienteDTO, "Paciente encontrado.", HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPaciente(@PathVariable Long id, @Valid @RequestBody PacienteUpdateDTO pacienteUpdateDTO) {
        logger.info("CONTROLLER: Recebida requisição PUT para /api/pacientes/{}", id);
        try {
            PacienteDTO pacienteAtualizado = pacienteService.atualizarPaciente(id, pacienteUpdateDTO); // Serviço retorna DTO
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
        logger.info("CONTROLLER: Recebida requisição DELETE para /api/pacientes/{}", id);
        try {
            pacienteService.deletarPaciente(id);
            // Para NO_CONTENT, geralmente não se envia corpo, mas sua createSuccessResponse pode lidar com data=null
            return createSuccessResponse(null, "Paciente deletado com sucesso.", HttpStatus.NO_CONTENT);
        } catch (ResourceNotFoundException e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // Exception Handlers permanecem os mesmos
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

    @ExceptionHandler(CpfAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleCpfAlreadyExists(CpfAlreadyExistsException ex) {
        logger.warn("CpfAlreadyExistsException: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        logger.warn("EmailAlreadyExistsException: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("IllegalArgumentException: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logger.error("Exceção genérica não tratada no PacienteController:", ex);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado no servidor.");
    }
}