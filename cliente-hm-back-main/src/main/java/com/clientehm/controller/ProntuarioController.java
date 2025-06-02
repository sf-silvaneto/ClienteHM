package com.clientehm.controller;

import com.clientehm.entity.*;
import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.model.*;
import com.clientehm.service.ProntuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// Removido BeanUtils, pois a conversão principal agora está no Service
// import org.springframework.beans.BeanUtils;
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
// Removido Collectors, pois a conversão da Page é feita no service
// import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prontuarios")
public class ProntuarioController {

    private static final Logger logger = LoggerFactory.getLogger(ProntuarioController.class);

    @Autowired
    private ProntuarioService prontuarioService;

    // --- MÉTODOS DE CONVERSÃO DE ENTIDADES DE REGISTRO PARA DTOs DE REGISTRO ---
    // (Necessários aqui porque os métodos de adicionar evento no serviço retornam as ENTIDADES de registro)

    private ConsultaDTO convertConsultaEntityToDTO(EntradaMedicaRegistroEntity entity) {
        if (entity == null) return null;
        ConsultaDTO dto = new ConsultaDTO();
        // Copiar campos de EntradaMedicaRegistroEntity para ConsultaDTO
        // O BeanUtils.copyProperties pode ser usado aqui se os nomes dos campos forem compatíveis.
        // Exemplo:
        org.springframework.beans.BeanUtils.copyProperties(entity, dto, "prontuario"); // Ignora o prontuário para evitar ciclos
        dto.setId(entity.getId()); // Garante que o ID da consulta seja copiado

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
        // Se ConsultaDTO tiver um campo prontuarioId, você pode setá-lo aqui:
        // dto.setProntuarioId(entity.getProntuario().getId());
        return dto;
    }

    private ExameRegistroDTO convertExameRegistroEntityToDTO(ExameRegistroEntity entity) {
        if (entity == null) return null;
        ExameRegistroDTO dto = new ExameRegistroDTO();
        org.springframework.beans.BeanUtils.copyProperties(entity, dto, "prontuario", "medicoResponsavelExame");
        dto.setId(entity.getId());
        dto.setProntuarioId(entity.getProntuario().getId());
        if (entity.getMedicoResponsavelExame() != null) {
            dto.setMedicoResponsavelExameId(entity.getMedicoResponsavelExame().getId());
            dto.setMedicoResponsavelExameNome(entity.getMedicoResponsavelExame().getNomeCompleto());
        }
        return dto;
    }

    private ProcedimentoRegistroDTO convertProcedimentoRegistroEntityToDTO(ProcedimentoRegistroEntity entity) {
        if (entity == null) return null;
        ProcedimentoRegistroDTO dto = new ProcedimentoRegistroDTO();
        org.springframework.beans.BeanUtils.copyProperties(entity, dto, "prontuario", "medicoExecutor");
        dto.setId(entity.getId());
        dto.setProntuarioId(entity.getProntuario().getId());
        if (entity.getMedicoExecutor() != null) {
            dto.setMedicoExecutorId(entity.getMedicoExecutor().getId());
            dto.setMedicoExecutorNome(entity.getMedicoExecutor().getNomeCompleto());
        }
        return dto;
    }

    private EncaminhamentoRegistroDTO convertEncaminhamentoRegistroEntityToDTO(EncaminhamentoRegistroEntity entity) {
        if (entity == null) return null;
        EncaminhamentoRegistroDTO dto = new EncaminhamentoRegistroDTO();
        org.springframework.beans.BeanUtils.copyProperties(entity, dto, "prontuario", "medicoSolicitante");
        dto.setId(entity.getId());
        dto.setProntuarioId(entity.getProntuario().getId());
        if (entity.getMedicoSolicitante() != null) {
            dto.setMedicoSolicitanteId(entity.getMedicoSolicitante().getId());
            dto.setMedicoSolicitanteNome(entity.getMedicoSolicitante().getNomeCompleto());
            dto.setMedicoSolicitanteCRM(entity.getMedicoSolicitante().getCrm());
        }
        return dto;
    }

    // O método convertProntuarioEntityToDetailedDTO foi movido para o ProntuarioService,
    // mas os conversores de registros específicos (acima) são usados pelos endpoints de criação.


    @GetMapping
    public ResponseEntity<Page<ProntuarioDTO>> buscarProntuariosPaginado(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) String numeroProntuario,
            @RequestParam(defaultValue = "dataUltimaAtualizacao,desc") String[] sort
    ) {
        logger.info("CONTROLLER: GET /api/prontuarios - pagina={}, tamanho={}, termo={}, numeroProntuario={}, sort={}",
                pagina, tamanho, termo, numeroProntuario, sort);

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
        logger.info("CONTROLLER: GET /api/prontuarios/{}", id);
        ProntuarioDTO prontuarioDTO = prontuarioService.buscarProntuarioPorIdDetalhado(id);
        return ResponseEntity.ok(prontuarioDTO);
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
                    pacienteId, consultaDTO, adminLogado, medicoExecutorId, true
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(convertConsultaEntityToDTO(consultaSalva));
        } catch (ResourceNotFoundException e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado ao adicionar consulta:", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao adicionar consulta.");
        }
    }

    @PostMapping("/exames")
    public ResponseEntity<?> adicionarExame(
            @RequestParam Long pacienteId,
            @RequestParam Long medicoResponsavelExameId,
            @Valid @RequestBody CriarExameRequestDTO exameDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {

        String adminEmail = (adminLogado != null) ? adminLogado.getEmail() : "ANONYMOUS";
        logger.info("CONTROLLER: POST /api/prontuarios/exames - pacienteId={}, medicoResponsavelExameId={}, admin={}",
                pacienteId, medicoResponsavelExameId, adminEmail);

        if (adminLogado == null) {
            return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        }
        try {
            ExameRegistroEntity exameSalvo = prontuarioService.adicionarExame(
                    pacienteId, exameDTO, adminLogado, medicoResponsavelExameId, true
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(convertExameRegistroEntityToDTO(exameSalvo));
        } catch (ResourceNotFoundException e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado ao adicionar exame:", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao adicionar exame.");
        }
    }

    @PostMapping("/procedimentos")
    public ResponseEntity<?> adicionarProcedimento(
            @RequestParam Long pacienteId,
            @Valid @RequestBody CriarProcedimentoRequestDTO procedimentoDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {

        String adminEmail = (adminLogado != null) ? adminLogado.getEmail() : "ANONYMOUS";
        logger.info("CONTROLLER: POST /api/prontuarios/procedimentos - pacienteId={}, medicoExecutorId={}, admin={}",
                pacienteId, procedimentoDTO.getMedicoExecutorId(), adminEmail);

        if (adminLogado == null) {
            return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        }
        try {
            ProcedimentoRegistroEntity procedimentoSalvo = prontuarioService.adicionarProcedimento(
                    pacienteId, procedimentoDTO, adminLogado, true
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(convertProcedimentoRegistroEntityToDTO(procedimentoSalvo));
        } catch (ResourceNotFoundException e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado ao adicionar procedimento:", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao adicionar procedimento.");
        }
    }

    @PostMapping("/encaminhamentos")
    public ResponseEntity<?> adicionarEncaminhamento(
            @RequestParam Long pacienteId,
            @Valid @RequestBody CriarEncaminhamentoRequestDTO encaminhamentoDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {

        String adminEmail = (adminLogado != null) ? adminLogado.getEmail() : "ANONYMOUS";
        logger.info("CONTROLLER: POST /api/prontuarios/encaminhamentos - pacienteId={}, medicoSolicitanteId={}, admin={}",
                pacienteId, encaminhamentoDTO.getMedicoSolicitanteId(), adminEmail);

        if (adminLogado == null) {
            return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        }
        try {
            EncaminhamentoRegistroEntity encaminhamentoSalvo = prontuarioService.adicionarEncaminhamento(
                    pacienteId, encaminhamentoDTO, adminLogado, true
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(convertEncaminhamentoRegistroEntityToDTO(encaminhamentoSalvo));
        } catch (ResourceNotFoundException e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado ao adicionar encaminhamento:", e);
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao adicionar encaminhamento.");
        }
    }

    @PutMapping("/{id}/dados-basicos")
    public ResponseEntity<?> atualizarDadosBasicosProntuario(
            @PathVariable Long id,
            @Valid @RequestBody ProntuarioUpdateDadosBasicosDTO updateDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {

        String adminEmail = (adminLogado != null) ? adminLogado.getEmail() : "ANONYMOUS_UPDATE";
        logger.info("CONTROLLER: PUT /api/prontuarios/{}/dados-basicos - admin={}, medicoIdNovo={}",
                id, adminEmail, updateDTO.getMedicoResponsavelId());

        if (adminLogado == null) {
            return createErrorResponse(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou não autorizado.");
        }
        try {
            // O método do service retorna a entidade, então convertemos para o DTO detalhado aqui
            ProntuarioEntity prontuarioAtualizadoEntity = prontuarioService.atualizarDadosBasicosProntuario(
                    id,
                    updateDTO.getMedicoResponsavelId()
            );
            // Precisamos garantir que o DTO retornado aqui seja o detalhado e completo
            ProntuarioDTO prontuarioAtualizadoDTO = prontuarioService.buscarProntuarioPorIdDetalhado(prontuarioAtualizadoEntity.getId());
            return ResponseEntity.ok(prontuarioAtualizadoDTO);
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