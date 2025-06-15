package com.main.api.controller;

import com.main.api.model.PacienteCreateDTO;
import com.main.api.model.PacienteDTO;
import com.main.api.model.PacienteUpdateDTO;
import com.main.domain.service.PacienteService;
import com.main.util.ApiResponseUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private ApiResponseUtil apiResponseUtil;

    @PostMapping
    public ResponseEntity<?> criarPaciente(@Valid @RequestBody PacienteCreateDTO pacienteCreateDTO) {
        PacienteDTO pacienteCriado = pacienteService.criarPaciente(pacienteCreateDTO);
        return apiResponseUtil.createSuccessResponse(HttpStatus.CREATED, "Paciente criado com sucesso.", pacienteCriado);
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
        return apiResponseUtil.createSuccessResponse(HttpStatus.OK, "Paciente encontrado.", pacienteDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPaciente(@PathVariable Long id, @Valid @RequestBody PacienteUpdateDTO pacienteUpdateDTO) {
        PacienteDTO pacienteAtualizado = pacienteService.atualizarPaciente(id, pacienteUpdateDTO);
        return apiResponseUtil.createSuccessResponse(HttpStatus.OK, "Paciente atualizado com sucesso.", pacienteAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletarPaciente(@PathVariable Long id) {
        pacienteService.deletarPaciente(id);
        return apiResponseUtil.createSuccessResponse(HttpStatus.NO_CONTENT, "Paciente deletado com sucesso.", null);
    }
}