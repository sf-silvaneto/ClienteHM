package com.clientehm.controller;

import com.clientehm.entity.AdministradorEntity;
import com.clientehm.entity.ProntuarioEntity;
// Importe as entidades específicas se precisar de acesso direto a elas aqui,
// mas geralmente o controller lida com DTOs.
// Ex: import com.clientehm.entity.PacienteEntity;
// import com.clientehm.entity.HistoricoMedicoEntity;

import com.clientehm.model.NovoProntuarioRequestDTO;
import com.clientehm.model.ProntuarioDTO;
import com.clientehm.model.PacienteDTO;
import com.clientehm.model.HistoricoMedicoDTO;
import com.clientehm.model.MedicacaoDTO;
import com.clientehm.model.ExameDTO;
import com.clientehm.model.AnotacaoDTO;
import com.clientehm.model.EnderecoDTO; // Adicionada importação para EnderecoDTO

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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
// Importe LocalDate e LocalDateTime se for usá-los diretamente aqui,
// embora eles venham principalmente das entidades e DTOs.
// import java.time.LocalDate;
// import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/prontuarios")
public class ProntuarioController {

    private static final Logger logger = LoggerFactory.getLogger(ProntuarioController.class);

    @Autowired
    private ProntuarioService prontuarioService;

    // Método para converter Entity para DTO
    // Este método assume que os DTOs foram atualizados para usar LocalDate/LocalDateTime
    private ProntuarioDTO convertToDTO(ProntuarioEntity entity) {
        if (entity == null) return null;
        ProntuarioDTO dto = new ProntuarioDTO();
        dto.setId(entity.getId());
        dto.setNumeroProntuario(entity.getNumeroProntuario());

        if (entity.getPaciente() != null) {
            PacienteDTO pacienteDTO = new PacienteDTO();
            pacienteDTO.setId(entity.getPaciente().getId());
            pacienteDTO.setNome(entity.getPaciente().getNome());
            pacienteDTO.setCpf(entity.getPaciente().getCpf());
            // Passando LocalDate diretamente
            pacienteDTO.setDataNascimento(entity.getPaciente().getDataNascimento());
            if (entity.getPaciente().getGenero() != null) {
                pacienteDTO.setGenero(entity.getPaciente().getGenero().name());
            }
            pacienteDTO.setTelefone(entity.getPaciente().getTelefone());
            pacienteDTO.setEmail(entity.getPaciente().getEmail());

            if (entity.getPaciente().getEndereco() != null) {
                EnderecoDTO enderecoDTO = new EnderecoDTO();
                enderecoDTO.setLogradouro(entity.getPaciente().getEndereco().getLogradouro());
                enderecoDTO.setNumero(entity.getPaciente().getEndereco().getNumero());
                enderecoDTO.setComplemento(entity.getPaciente().getEndereco().getComplemento());
                enderecoDTO.setBairro(entity.getPaciente().getEndereco().getBairro());
                enderecoDTO.setCidade(entity.getPaciente().getEndereco().getCidade());
                enderecoDTO.setEstado(entity.getPaciente().getEndereco().getEstado());
                enderecoDTO.setCep(entity.getPaciente().getEndereco().getCep());
                pacienteDTO.setEndereco(enderecoDTO);
            }
            // Passando LocalDateTime diretamente
            pacienteDTO.setCreatedAt(entity.getPaciente().getCreatedAt());
            pacienteDTO.setUpdatedAt(entity.getPaciente().getUpdatedAt());
            dto.setPaciente(pacienteDTO);
        }

        dto.setTipoTratamento(entity.getTipoTratamento() != null ? entity.getTipoTratamento().name() : null);
        // Passando LocalDate diretamente
        dto.setDataInicio(entity.getDataInicio());
        // Passando LocalDateTime diretamente
        dto.setDataUltimaAtualizacao(entity.getDataUltimaAtualizacao());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        // Passando LocalDateTime diretamente
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getDataUltimaAtualizacao()); // Mapeando dataUltimaAtualizacao para updatedAt do DTO

        if (entity.getHistoricoMedico() != null) {
            dto.setHistoricoMedico(entity.getHistoricoMedico().stream().map(hist -> {
                HistoricoMedicoDTO histDTO = new HistoricoMedicoDTO();
                histDTO.setId(hist.getId());
                // Passando LocalDateTime diretamente
                histDTO.setData(hist.getData()); // Esta era a linha aproximada do erro (87)
                histDTO.setDescricao(hist.getDescricao());
                histDTO.setResponsavel(hist.getResponsavel());
                // Passando LocalDateTime diretamente
                histDTO.setCreatedAt(hist.getCreatedAt());
                histDTO.setUpdatedAt(hist.getUpdatedAt());
                return histDTO;
            }).collect(Collectors.toList()));
        }

        // Implementar mapeamento similar para Medicacao, Exame, Anotacao se eles forem incluídos no ProntuarioDTO
        // Exemplo para MedicacaoDTO (se existir e tiver campos de data):
        // if (entity.getMedicacoes() != null && dto.getMedicacoes() != null) { // Supondo que ProntuarioEntity e DTO têm getMedicacoes()
        //     dto.setMedicacoes(entity.getMedicacoes().stream().map(medEntity -> {
        //         MedicacaoDTO medDTO = new MedicacaoDTO();
        //         medDTO.setId(medEntity.getId());
        //         medDTO.setNome(medEntity.getNome());
        //         // ... outros campos ...
        //         // if (medEntity.getDataInicio() != null) medDTO.setDataInicio(medEntity.getDataInicio()); // Se for LocalDate
        //         // if (medEntity.getDataFim() != null) medDTO.setDataFim(medEntity.getDataFim()); // Se for LocalDate
        //         return medDTO;
        //     }).collect(Collectors.toList()));
        // }

        return dto;
    }


    @GetMapping
    public ResponseEntity<Map<String, Object>> buscarProntuarios(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) String numeroProntuario,
            @RequestParam(required = false) String tipoTratamento,
            @RequestParam(required = false) String status
            // Adicionar outros parâmetros de filtro aqui
    ) {
        logger.info("Recebida requisição GET para /api/prontuarios com pagina={}, tamanho={}, termo={}", pagina, tamanho, termo);
        // Adapte o PageRequest para incluir ordenação se necessário, ex: Sort.by("dataUltimaAtualizacao").descending()
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("dataUltimaAtualizacao").descending());

        // Aqui você passaria os parâmetros de filtro para o service
        // Ex: Page<ProntuarioEntity> prontuariosPage = prontuarioService.buscarTodosComFiltros(termo, numeroProntuario, tipoTratamento, status, pageable);
        Page<ProntuarioEntity> prontuariosPage = prontuarioService.buscarTodos(pageable); // Chamada simples por enquanto

        Map<String, Object> response = new HashMap<>();
        response.put("content", prontuariosPage.getContent().stream().map(this::convertToDTO).collect(Collectors.toList()));

        Map<String, Object> pageableResponse = new HashMap<>();
        pageableResponse.put("pageNumber", prontuariosPage.getNumber());
        pageableResponse.put("pageSize", prontuariosPage.getSize());
        pageableResponse.put("totalPages", prontuariosPage.getTotalPages());
        pageableResponse.put("totalElements", prontuariosPage.getTotalElements());
        // Adicione outros campos do Pageable se o frontend precisar
        // pageableResponse.put("sort", prontuariosPage.getSort().toString());
        // pageableResponse.put("first", prontuariosPage.isFirst());
        // pageableResponse.put("last", prontuariosPage.isLast());
        // pageableResponse.put("empty", prontuariosPage.isEmpty());
        response.put("pageable", pageableResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProntuarioDTO> buscarProntuarioPorId(@PathVariable Long id) {
        logger.info("Recebida requisição GET para /api/prontuarios/{}", id);
        ProntuarioEntity prontuario = prontuarioService.buscarPorId(id);
        return ResponseEntity.ok(convertToDTO(prontuario));
    }

    @PostMapping
    public ResponseEntity<ProntuarioDTO> criarProntuario(
            @Valid @RequestBody NovoProntuarioRequestDTO novoProntuarioDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {
        String adminEmail = (adminLogado != null) ? adminLogado.getEmail() : "ANONYMOUS";
        logger.info("Recebida requisição POST para /api/prontuarios pelo admin: {}", adminEmail);

        if (adminLogado == null) {
            logger.warn("Tentativa de criar prontuário por usuário não autenticado.");
            // O Spring Security deve bloquear isso antes, mas é uma verificação adicional.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        ProntuarioEntity prontuarioCriado = prontuarioService.criarProntuario(novoProntuarioDTO, adminLogado.getNome());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(prontuarioCriado));
    }

    // Implementar endpoints para:
    // @PutMapping("/{id}") - Atualizar Prontuário (geral)
    // @PatchMapping("/{id}/status") - Mudar Status
    // @PostMapping("/{prontuarioId}/historico-medico") - Adicionar Histórico
    // @PostMapping("/{prontuarioId}/medicacoes") - Adicionar Medicação
    // @PostMapping("/{prontuarioId}/exames") - Adicionar Exame (com upload de arquivo)
    // @PostMapping("/{prontuarioId}/anotacoes") - Adicionar Anotação
}