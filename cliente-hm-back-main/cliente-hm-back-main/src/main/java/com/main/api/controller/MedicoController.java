package com.main.api.controller;

import com.main.api.model.MedicoCreateDTO;
import com.main.api.model.MedicoDTO;
import com.main.api.model.MedicoUpdateDTO;
import com.main.domain.service.MedicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

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
            medicosPage = medicoService.buscarMedicosPorStatus(status, pageable);
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

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<MedicoDTO> ativarMedico(@PathVariable Long id) {
        MedicoDTO medicoAtualizado = medicoService.atualizarStatusMedico(id, true);
        return ResponseEntity.ok(medicoAtualizado);
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<MedicoDTO> inativarMedico(@PathVariable Long id) {
        MedicoDTO medicoAtualizado = medicoService.atualizarStatusMedico(id, false);
        return ResponseEntity.ok(medicoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMedico(@PathVariable Long id) {
        medicoService.deletarMedico(id);
        return ResponseEntity.noContent().build();
    }
}