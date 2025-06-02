// sf-silvaneto/clientehm/ClienteHM-057824fed8786ee29c7b4f9a2010aca3a83abc37/cliente-hm-back-main/src/main/java/com/clientehm/controller/ProntuarioController.java
package com.clientehm.controller;

import com.clientehm.entity.*;
import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.model.*;
import com.clientehm.service.ProntuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prontuarios")
public class ProntuarioController {

    private static final Logger logger = LoggerFactory.getLogger(ProntuarioController.class);

    @Autowired
    private ProntuarioService prontuarioService;

    // --- CONVERSORES DTO ---
    private ProntuarioDTO convertProntuarioToDTO(ProntuarioEntity entity) {
        if (entity == null) return null;
        ProntuarioDTO dto = new ProntuarioDTO();
        BeanUtils.copyProperties(entity, dto, "historicoGeral", "consultas");
        // Campos removidos: status, dataAltaAdministrativa, internacoes
        dto.setDataUltimaAtualizacao(entity.getDataUltimaAtualizacao());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getDataUltimaAtualizacao()); // Correto é updatedAt da entidade

        if (entity.getPaciente() != null) {
            PacienteDTO pacienteDTO = new PacienteDTO();
            BeanUtils.copyProperties(entity.getPaciente(), pacienteDTO);
            if (entity.getPaciente().getGenero() != null) {
                pacienteDTO.setGenero(entity.getPaciente().getGenero().name());
            }
            if (entity.getPaciente().getRacaCor() != null) {
                pacienteDTO.setRacaCor(entity.getPaciente().getRacaCor().name());
            }
            if (entity.getPaciente().getTipoSanguineo() != null) {
                pacienteDTO.setTipoSanguineo(entity.getPaciente().getTipoSanguineo().name());
            }
            pacienteDTO.setAlergiasDeclaradas(entity.getPaciente().getAlergiasDeclaradas());
            pacienteDTO.setComorbidadesDeclaradas(entity.getPaciente().getComorbidadesDeclaradas());
            pacienteDTO.setMedicamentosContinuos(entity.getPaciente().getMedicamentosContinuos());

            if (entity.getPaciente().getEndereco() != null) {
                EnderecoDTO enderecoDTO = new EnderecoDTO();
                BeanUtils.copyProperties(entity.getPaciente().getEndereco(), enderecoDTO);
                pacienteDTO.setEndereco(enderecoDTO);
            }
            dto.setPaciente(pacienteDTO);
        }

        if (entity.getMedicoResponsavel() != null) {
            ProntuarioDTO.MedicoBasicDTO medicoDTO = new ProntuarioDTO.MedicoBasicDTO();
            BeanUtils.copyProperties(entity.getMedicoResponsavel(), medicoDTO);
            dto.setMedicoResponsavel(medicoDTO);
        }
        if (entity.getAdministradorCriador() != null) {
            ProntuarioDTO.AdministradorBasicDTO adminDTO = new ProntuarioDTO.AdministradorBasicDTO();
            BeanUtils.copyProperties(entity.getAdministradorCriador(), adminDTO);
            dto.setAdministradorCriador(adminDTO);
        }

        if (entity.getHistoricoGeral() != null) {
            dto.setHistoricoGeral(entity.getHistoricoGeral().stream()
                    .map(this::convertHistoricoToDTO).collect(Collectors.toList()));
        }
        if (entity.getConsultas() != null) {
            dto.setConsultas(entity.getConsultas().stream()
                    .map(this::convertConsultaToDTO).collect(Collectors.toList()));
        }
        return dto;
    }

    private HistoricoMedicoDTO convertHistoricoToDTO(HistoricoMedicoEntity entity) {
        if (entity == null) return null;
        HistoricoMedicoDTO dto = new HistoricoMedicoDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private ConsultaDTO convertConsultaToDTO(EntradaMedicaRegistroEntity entity) {
        if (entity == null) return null;
        ConsultaDTO dto = new ConsultaDTO();
        BeanUtils.copyProperties(entity, dto); // Copia a maioria dos campos

        // Se os nomes em EntradaMedicaRegistroEntity e ConsultaDTO forem diferentes,
        // pode ser necessário mapeamento explícito, mas assumindo que são iguais ou BeanUtils.copyProperties lida com isso.

        if (entity.getResponsavelMedico() != null) {
            dto.setTipoResponsavel("MEDICO");
            dto.setResponsavelId(entity.getResponsavelMedico().getId());
            dto.setResponsavelNomeCompleto(entity.getResponsavelMedico().getNomeCompleto());
            dto.setResponsavelEspecialidade(entity.getResponsavelMedico().getEspecialidade());
            dto.setResponsavelCRM(entity.getResponsavelMedico().getCrm());
        } else if (entity.getResponsavelAdmin() != null) {
            dto.setTipoResponsavel("ADMINISTRADOR");
            dto.setResponsavelId(entity.getResponsavelAdmin().getId());
            dto.setResponsavelNomeCompleto(entity.getResponsavelAdmin().getNome());
        } else {
            dto.setResponsavelNomeCompleto(entity.getNomeResponsavelDisplay());
        }
        // Remoção de anexos de ConsultaDTO já foi feita no DTO em si
        return dto;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> buscarProntuariosPaginado(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) String numeroProntuario,
            // REMOVIDO: @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "dataUltimaAtualizacao,desc") String[] sort
    ) {
        logger.info("CONTROLLER: GET /api/prontuarios - pagina={}, tamanho={}, termo={}, numeroProntuario={}, sort={}",
                pagina, tamanho, termo, numeroProntuario, sort);

        String sortField = sort[0];
        String sortDirection = sort.length > 1 ? sort[1] : "desc";
        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Sort sortBy = Sort.by(direction, sortField);
        Pageable pageable = PageRequest.of(pagina, tamanho, sortBy);

        Page<ProntuarioEntity> prontuariosPage = prontuarioService.buscarTodosProntuarios(pageable, termo, numeroProntuario);

        Map<String, Object> response = new HashMap<>();
        response.put("content", prontuariosPage.getContent().stream().map(this::convertProntuarioToDTO).collect(Collectors.toList()));
        Map<String, Object> pageableResponse = new HashMap<>();
        pageableResponse.put("pageNumber", prontuariosPage.getNumber());
        pageableResponse.put("pageSize", prontuariosPage.getSize());
        pageableResponse.put("totalPages", prontuariosPage.getTotalPages());
        pageableResponse.put("totalElements", prontuariosPage.getTotalElements());
        response.put("pageable", pageableResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProntuarioDTO> buscarProntuarioPorId(@PathVariable Long id) {
        logger.info("CONTROLLER: GET /api/prontuarios/{}", id);
        ProntuarioEntity prontuario = prontuarioService.buscarProntuarioPorId(id);
        return ResponseEntity.ok(convertProntuarioToDTO(prontuario));
    }

    @PostMapping("/consultas")
    public ResponseEntity<?> adicionarConsulta(
            @RequestParam Long pacienteId,
            @RequestParam Long medicoExecutorId,
            @Valid @RequestBody CriarConsultaRequestDTO consultaDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {

        String adminEmail = (adminLogado != null) ? adminLogado.getEmail() : "ANONYMOUS";
        logger.info("CONTROLLER: POST /api/prontuarios/consultas - pacienteId={}, medicoExecutorId={}, admin={}",
                pacienteId, medicoExecutorId, adminEmail);

        if (adminLogado == null) {
            return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        }
        try {
            EntradaMedicaRegistroEntity consultaSalva = prontuarioService.adicionarConsulta(
                    pacienteId,
                    consultaDTO,
                    adminLogado,
                    medicoExecutorId,
                    true // criarProntuarioSeNaoExistir
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(convertConsultaToDTO(consultaSalva));
        } catch (ResourceNotFoundException e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado ao adicionar consulta:", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao adicionar consulta.");
        }
    }

    @PutMapping("/{id}/dados-basicos")
    public ResponseEntity<?> atualizarDadosBasicosProntuario(
            @PathVariable Long id,
            @Valid @RequestBody ProntuarioUpdateDadosBasicosDTO updateDTO, // DTO já foi simplificado
            @AuthenticationPrincipal AdministradorEntity adminLogado) {

        String adminEmail = (adminLogado != null) ? adminLogado.getEmail() : "ANONYMOUS_UPDATE";
        logger.info("CONTROLLER: PUT /api/prontuarios/{}/dados-basicos - admin={}, medicoIdNovo={}",
                id, adminEmail, updateDTO.getMedicoResponsavelId());

        if (adminLogado == null) {
            return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        }
        try {
            ProntuarioEntity prontuarioAtualizado = prontuarioService.atualizarDadosBasicosProntuario(
                    id,
                    updateDTO.getMedicoResponsavelId()
                    // Não há mais status ou dataAltaAdministrativa aqui
            );
            return ResponseEntity.ok(convertProntuarioToDTO(prontuarioAtualizado));
        } catch (ResourceNotFoundException e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado ao atualizar dados básicos do prontuário {}: ", id, e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao atualizar dados básicos do prontuário.");
        }
    }

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