// src/main/java/com/clientehm/controller/ProntuarioController.java
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException; // <<< IMPORT ADICIONADO AQUI

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/prontuarios")
public class ProntuarioController {

    private static final Logger logger = LoggerFactory.getLogger(ProntuarioController.class);

    @Autowired
    private ProntuarioService prontuarioService;

    // MedicoService não é mais injetado aqui se ProntuarioService lida com a busca do MedicoEntity

    // --- CONVERSORES DTO ---
    private ProntuarioDTO convertProntuarioToDTO(ProntuarioEntity entity) {
        if (entity == null) return null;
        ProntuarioDTO dto = new ProntuarioDTO();
        BeanUtils.copyProperties(entity, dto, "historicoGeral", "consultas", "internacoes");
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        dto.setDataAltaAdministrativa(entity.getDataAltaAdministrativa());
        dto.setDataUltimaAtualizacao(entity.getDataUltimaAtualizacao());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getDataUltimaAtualizacao());

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
        if (entity.getInternacoes() != null) {
            dto.setInternacoes(entity.getInternacoes().stream()
                    .map(this::convertInternacaoToDTO).collect(Collectors.toList()));
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
        BeanUtils.copyProperties(entity, dto, "dataHoraEntrada", "motivoEntrada");

        dto.setDataHoraConsulta(entity.getDataHoraConsulta());
        dto.setMotivoConsulta(entity.getMotivoConsulta());
        dto.setExameFisico(entity.getExameFisico());
        dto.setHipoteseDiagnostica(entity.getHipoteseDiagnostica());
        dto.setCondutaPlanoTerapeutico(entity.getCondutaPlanoTerapeutico());

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
        if (entity.getAnexos() != null) {
            dto.setAnexos(entity.getAnexos().stream()
                    .filter(anexoEntrada -> anexoEntrada.getAnexo() != null)
                    .map(anexoEntrada -> {
                        AnexoDTO anexoDTO = new AnexoDTO();
                        BeanUtils.copyProperties(anexoEntrada.getAnexo(), anexoDTO);
                        return anexoDTO;
                    }).collect(Collectors.toList()));
        }
        return dto;
    }

    private InternacaoDTO convertInternacaoToDTO(InternacaoEntity entity) {
        if (entity == null) return null;
        InternacaoDTO dto = new InternacaoDTO();
        BeanUtils.copyProperties(entity, dto);
        if(entity.getProntuario()!=null) dto.setProntuarioId(entity.getProntuario().getId());

        if (entity.getResponsavelAdmissaoMedico() != null) {
            dto.setTipoResponsavelAdmissao("MEDICO");
            dto.setResponsavelAdmissaoId(entity.getResponsavelAdmissaoMedico().getId());
            dto.setResponsavelAdmissaoNomeCompleto(entity.getResponsavelAdmissaoMedico().getNomeCompleto());
            dto.setResponsavelAdmissaoEspecialidade(entity.getResponsavelAdmissaoMedico().getEspecialidade());
            dto.setResponsavelAdmissaoCRM(entity.getResponsavelAdmissaoMedico().getCrm());
        } else if (entity.getResponsavelAdmissaoAdmin() != null) {
            dto.setTipoResponsavelAdmissao("ADMINISTRADOR");
            dto.setResponsavelAdmissaoId(entity.getResponsavelAdmissaoAdmin().getId());
            dto.setResponsavelAdmissaoNomeCompleto(entity.getResponsavelAdmissaoAdmin().getNome());
        } else if (entity.getNomeResponsavelAdmissaoDisplay() != null) {
            dto.setResponsavelAdmissaoNomeCompleto(entity.getNomeResponsavelAdmissaoDisplay());
        }


        if (entity.getMedicoResponsavelAlta() != null) {
            dto.setMedicoResponsavelAltaId(entity.getMedicoResponsavelAlta().getId());
            dto.setMedicoResponsavelAltaNome(entity.getMedicoResponsavelAlta().getNomeCompleto());
        }
        // Mapear anexos de internação se houver
        return dto;
    }

    // Endpoint GET /api/prontuarios (buscarTodosPaginado)
    @GetMapping
    public ResponseEntity<Map<String, Object>> buscarProntuariosPaginado(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) String numeroProntuario,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "dataUltimaAtualizacao,desc") String[] sort
    ) {
        logger.info("CONTROLLER: GET /api/prontuarios - pagina={}, tamanho={}, termo={}, numeroProntuario={}, status={}, sort={}",
                pagina, tamanho, termo, numeroProntuario, status, sort);

        String sortField = sort[0];
        String sortDirection = sort.length > 1 ? sort[1] : "desc";
        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Sort sortBy = Sort.by(direction, sortField);
        Pageable pageable = PageRequest.of(pagina, tamanho, sortBy);

        Page<ProntuarioEntity> prontuariosPage = prontuarioService.buscarTodosProntuarios(pageable, termo, numeroProntuario, status);

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
                    true
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

    @PostMapping("/internacoes")
    public ResponseEntity<?> adicionarInternacao(
            @Valid @RequestBody InternacaoRequestDTO internacaoDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {

        String adminEmail = (adminLogado != null) ? adminLogado.getEmail() : "ANONYMOUS";
        logger.info("CONTROLLER: POST /api/prontuarios/internacoes - pacienteId={}, medicoAdmissaoId={}, admin={}",
                internacaoDTO.getPacienteId(), internacaoDTO.getMedicoResponsavelAdmissaoId(), adminEmail);

        if (adminLogado == null) {
            return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        }
        try {
            InternacaoEntity internacaoSalva = prontuarioService.adicionarInternacao(
                    internacaoDTO.getPacienteId(),
                    internacaoDTO,
                    adminLogado,
                    true
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(convertInternacaoToDTO(internacaoSalva));
        } catch (ResourceNotFoundException e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado ao adicionar internação:", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao adicionar internação.");
        }
    }

    @PutMapping("/internacoes/{internacaoId}/alta")
    public ResponseEntity<?> registrarAlta(
            @PathVariable Long internacaoId,
            @Valid @RequestBody RegistrarAltaInternacaoDTO altaDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {

        String adminEmail = (adminLogado != null) ? adminLogado.getEmail() : "ANONYMOUS";
        logger.info("CONTROLLER: PUT /api/prontuarios/internacoes/{}/alta - admin={}", internacaoId, adminEmail);

        if (adminLogado == null) {
            return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        }
        try {
            InternacaoEntity internacaoComAlta = prontuarioService.registrarAltaInternacao(internacaoId, altaDTO, adminLogado);
            return ResponseEntity.ok(convertInternacaoToDTO(internacaoComAlta));
        } catch (ResourceNotFoundException e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado ao registrar alta da internação:", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao registrar alta.");
        }
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("mensagem", message);
        body.put("codigo", status.value());
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // Linha 310 (aproximadamente)
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