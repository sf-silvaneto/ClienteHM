package com.clientehm.controller;

import com.clientehm.entity.AdministradorEntity;
import com.clientehm.entity.EntradaMedicaRegistroEntity; // Importar
import com.clientehm.entity.ProntuarioEntity;
import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.model.*; // Importar todos os DTOs do pacote
import com.clientehm.service.ProntuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils; // Para copiar propriedades
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
import java.util.ArrayList; // Importar ArrayList


@RestController
@RequestMapping("/api/prontuarios")
public class ProntuarioController {

    private static final Logger logger = LoggerFactory.getLogger(ProntuarioController.class);

    @Autowired
    private ProntuarioService prontuarioService;

    // Atualizar convertToDTO para incluir a lista de EntradasMedicasDTO
    // e os novos campos do PacienteDTO
    private ProntuarioDTO convertToDTO(ProntuarioEntity entity) {
        if (entity == null) return null;
        ProntuarioDTO dto = new ProntuarioDTO();
        dto.setId(entity.getId());
        dto.setNumeroProntuario(entity.getNumeroProntuario());

        if (entity.getPaciente() != null) {
            PacienteDTO pacienteDTO = new PacienteDTO();
            BeanUtils.copyProperties(entity.getPaciente(), pacienteDTO); // Copia campos comuns
            if (entity.getPaciente().getGenero() != null) {
                pacienteDTO.setGenero(entity.getPaciente().getGenero().name());
            }
            if (entity.getPaciente().getRacaCor() != null) {
                pacienteDTO.setRacaCor(entity.getPaciente().getRacaCor().name());
            }
            if (entity.getPaciente().getTipoSanguineo() != null) {
                pacienteDTO.setTipoSanguineo(entity.getPaciente().getTipoSanguineo().name());
            }
            // Mapear os novos campos de PacienteEntity para PacienteDTO
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
            medicoDTO.setId(entity.getMedicoResponsavel().getId());
            medicoDTO.setNomeCompleto(entity.getMedicoResponsavel().getNomeCompleto());
            medicoDTO.setCrm(entity.getMedicoResponsavel().getCrm());
            medicoDTO.setEspecialidade(entity.getMedicoResponsavel().getEspecialidade());
            dto.setMedicoResponsavel(medicoDTO);
        }

        if (entity.getAdministradorCriador() != null) {
            ProntuarioDTO.AdministradorBasicDTO adminDTO = new ProntuarioDTO.AdministradorBasicDTO();
            adminDTO.setId(entity.getAdministradorCriador().getId());
            adminDTO.setNome(entity.getAdministradorCriador().getNome());
            adminDTO.setEmail(entity.getAdministradorCriador().getEmail());
            dto.setAdministradorCriador(adminDTO);
        }

        dto.setTipoTratamento(entity.getTipoTratamento() != null ? entity.getTipoTratamento().name() : null);
        dto.setDataInicio(entity.getDataInicio());
        dto.setDataAlta(entity.getDataAlta());
        dto.setDataUltimaAtualizacao(entity.getDataUltimaAtualizacao());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getDataUltimaAtualizacao());

        if (entity.getHistoricoMedico() != null) {
            dto.setHistoricoMedico(entity.getHistoricoMedico().stream().map(hist -> {
                HistoricoMedicoDTO histDTO = new HistoricoMedicoDTO();
                BeanUtils.copyProperties(hist, histDTO);
                return histDTO;
            }).collect(Collectors.toList()));
        }

        // Mapear Entradas Médicas
        if (entity.getEntradasMedicas() != null) {
            dto.setEntradasMedicas(entity.getEntradasMedicas().stream().map(entradaEntity -> {
                EntradaMedicaRegistroDTO entradaDTO = new EntradaMedicaRegistroDTO();
                BeanUtils.copyProperties(entradaEntity, entradaDTO);
                // entradaDTO.setNomeResponsavelDisplay(entradaEntity.getNomeResponsavelDisplay()); // Já deve ser copiado pelo BeanUtils se os nomes baterem
                return entradaDTO;
            }).collect(Collectors.toList()));
        } else {
            dto.setEntradasMedicas(new ArrayList<>()); // Garante lista vazia
        }

        // Mapear outras listas (medicacoes, exames, anotacoes) quando implementadas
        return dto;
    }

    // ... (endpoints GET existentes - buscarProntuarios, buscarProntuarioPorId) ...
    @GetMapping
    public ResponseEntity<Map<String, Object>> buscarProntuarios(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) String numeroProntuario,
            @RequestParam(required = false) String tipoTratamento,
            @RequestParam(required = false) String status
    ) {
        logger.info("Recebida requisição GET para /api/prontuarios com pagina={}, tamanho={}, termo={}", pagina, tamanho, termo);
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("dataUltimaAtualizacao").descending());
        Page<ProntuarioEntity> prontuariosPage = prontuarioService.buscarTodos(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", prontuariosPage.getContent().stream().map(this::convertToDTO).collect(Collectors.toList()));
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
        ProntuarioEntity prontuario = prontuarioService.buscarPorId(id); // Service deve carregar as coleções necessárias
        return ResponseEntity.ok(convertToDTO(prontuario));
    }


    @PostMapping
    public ResponseEntity<?> criarProntuario(
            @Valid @RequestBody NovoProntuarioRequestDTO novoProntuarioDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {
        // ... (lógica do POST de criarProntuario como antes)
        String adminEmail = (adminLogado != null) ? adminLogado.getEmail() : "ANONYMOUS_OR_UNAUTHENTICATED";
        logger.info("Recebida requisição POST para /api/prontuarios pelo admin: {}", adminEmail);

        if (adminLogado == null) {
            logger.warn("Tentativa de criar prontuário por usuário não autenticado ou admin não encontrado na sessão.");
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("mensagem", "Usuário não autenticado ou não autorizado.");
            errorBody.put("codigo", HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorBody);
        }
        try {
            ProntuarioEntity prontuarioCriado = prontuarioService.criarProntuario(novoProntuarioDTO, adminLogado.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(prontuarioCriado));
        } catch (ResourceNotFoundException e) {
            logger.warn("Erro ao criar prontuário: {}", e.getMessage());
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("mensagem", e.getMessage());
            errorBody.put("codigo", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
        } catch (IllegalArgumentException e) {
            logger.warn("Erro de argumento inválido ao criar prontuário: {}", e.getMessage());
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("mensagem", e.getMessage());
            errorBody.put("codigo", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
        } catch (Exception e) {
            logger.error("Erro inesperado ao criar prontuário:", e);
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("mensagem", "Erro interno ao processar a solicitação.");
            errorBody.put("codigo", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }
    }

    // NOVO ENDPOINT para adicionar Entrada Médica
    @PostMapping("/{prontuarioId}/entradas-medicas")
    public ResponseEntity<?> adicionarEntradaMedica(
            @PathVariable Long prontuarioId,
            @Valid @RequestBody CriarEntradaMedicaRequestDTO entradaMedicaDTO,
            @AuthenticationPrincipal AdministradorEntity adminLogado) {

        logger.info("CONTROLLER: POST /api/prontuarios/{}/entradas-medicas pelo admin: {}", prontuarioId, adminLogado.getEmail());
        if (adminLogado == null) {
            // Mesmo tratamento de erro de autenticação
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("mensagem", "Usuário não autenticado ou não autorizado.");
            errorBody.put("codigo", HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorBody);
        }
        try {
            EntradaMedicaRegistroEntity entradaSalva = prontuarioService.adicionarEntradaMedica(prontuarioId, entradaMedicaDTO, adminLogado);
            // Converter entradaSalva para DTO se necessário retornar o objeto criado
            EntradaMedicaRegistroDTO dtoResposta = new EntradaMedicaRegistroDTO();
            BeanUtils.copyProperties(entradaSalva, dtoResposta);
            dtoResposta.setNomeResponsavelDisplay(adminLogado.getNome()); // ou do médico se aplicável

            return ResponseEntity.status(HttpStatus.CREATED).body(dtoResposta);
        } catch (ResourceNotFoundException e) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("mensagem", e.getMessage());
            errorBody.put("codigo", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("mensagem", e.getMessage());
            errorBody.put("codigo", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
        } catch (Exception e) {
            logger.error("Erro inesperado ao adicionar entrada médica:", e);
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("mensagem", "Erro interno ao processar a solicitação de adicionar entrada médica.");
            errorBody.put("codigo", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }
    }
    // Adicionar outros Handlers de Exceção se necessário
}