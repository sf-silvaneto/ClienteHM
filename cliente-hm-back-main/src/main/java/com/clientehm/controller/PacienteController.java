package com.clientehm.controller;

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
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

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
        PacienteDTO pacienteCriado = pacienteService.criarPaciente(pacienteCreateDTO);
        return createSuccessResponse(pacienteCriado, "Paciente criado com sucesso.", HttpStatus.CREATED);
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
        PacienteDTO pacienteDTO = pacienteService.buscarPacientePorId(id);
        return createSuccessResponse(pacienteDTO, "Paciente encontrado.", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPaciente(@PathVariable Long id, @Valid @RequestBody PacienteUpdateDTO pacienteUpdateDTO) {
        PacienteDTO pacienteAtualizado = pacienteService.atualizarPaciente(id, pacienteUpdateDTO);
        return createSuccessResponse(pacienteAtualizado, "Paciente atualizado com sucesso.", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletarPaciente(@PathVariable Long id) {
        pacienteService.deletarPaciente(id);
        return createSuccessResponse(null, "Paciente deletado com sucesso.", HttpStatus.NO_CONTENT);
    }
}