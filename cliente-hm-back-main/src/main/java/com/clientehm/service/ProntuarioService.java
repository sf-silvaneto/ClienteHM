package com.clientehm.service;

import com.clientehm.entity.*;
import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.model.*;
import com.clientehm.repository.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProntuarioService {

    private static final Logger logger = LoggerFactory.getLogger(ProntuarioService.class);

    @Autowired private ProntuarioRepository prontuarioRepository;
    @Autowired private PacienteRepository pacienteRepository;
    @Autowired private MedicoRepository medicoRepository;
    @Autowired private AdministradorRepository administradorRepository;
    @Autowired private EntradaMedicaRegistroRepository consultaRepository;
    @Autowired private ExameRegistroRepository exameRepository;
    @Autowired private ProcedimentoRegistroRepository procedimentoRepository;
    @Autowired private EncaminhamentoRegistroRepository encaminhamentoRepository;

    private ProntuarioDTO.MedicoBasicDTO convertMedicoToBasicDTO(MedicoEntity medico) {
        if (medico == null) return null;
        ProntuarioDTO.MedicoBasicDTO dto = new ProntuarioDTO.MedicoBasicDTO();
        BeanUtils.copyProperties(medico, dto);
        return dto;
    }

    private ProntuarioDTO.AdministradorBasicDTO convertAdminToBasicDTO(AdministradorEntity admin) {
        if (admin == null) return null;
        ProntuarioDTO.AdministradorBasicDTO dto = new ProntuarioDTO.AdministradorBasicDTO();
        dto.setId(admin.getId());
        dto.setNome(admin.getNome());
        dto.setEmail(admin.getEmail());
        return dto;
    }

    private PacienteDTO convertPacienteToDTO(PacienteEntity paciente) {
        if (paciente == null) return null;
        PacienteDTO dto = new PacienteDTO();
        BeanUtils.copyProperties(paciente, dto, "endereco");
        if (paciente.getGenero() != null) dto.setGenero(paciente.getGenero().name());
        if (paciente.getRacaCor() != null) dto.setRacaCor(paciente.getRacaCor().name());
        if (paciente.getTipoSanguineo() != null) dto.setTipoSanguineo(paciente.getTipoSanguineo().name());
        dto.setAlergiasDeclaradas(paciente.getAlergiasDeclaradas());
        dto.setComorbidadesDeclaradas(paciente.getComorbidadesDeclaradas());
        dto.setMedicamentosContinuos(paciente.getMedicamentosContinuos());
        if (paciente.getEndereco() != null) {
            EnderecoDTO enderecoDTO = new EnderecoDTO();
            BeanUtils.copyProperties(paciente.getEndereco(), enderecoDTO);
            dto.setEndereco(enderecoDTO);
        }
        return dto;
    }

    private ProntuarioDTO convertProntuarioEntityToBasicListDTO(ProntuarioEntity entity) {
        if (entity == null) return null;
        ProntuarioDTO dto = new ProntuarioDTO();

        dto.setId(entity.getId());
        dto.setNumeroProntuario(entity.getNumeroProntuario());
        dto.setDataInicio(entity.getDataInicio());
        dto.setDataUltimaAtualizacao(entity.getDataUltimaAtualizacao());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getDataUltimaAtualizacao()); // ou entity.getUpdatedAt() se existir

        if (entity.getPaciente() != null) {
            dto.setPaciente(convertPacienteToDTO(entity.getPaciente()));
        }
        if (entity.getMedicoResponsavel() != null) {
            dto.setMedicoResponsavel(convertMedicoToBasicDTO(entity.getMedicoResponsavel()));
        }
        return dto;
    }

    private ProntuarioDTO convertProntuarioEntityToDetailedDTO(ProntuarioEntity entity) {
        if (entity == null) return null;
        ProntuarioDTO dto = convertProntuarioEntityToBasicListDTO(entity); // Reutiliza o básico

        if (entity.getAdministradorCriador() != null) {
            dto.setAdministradorCriador(convertAdminToBasicDTO(entity.getAdministradorCriador()));
        }

        if (entity.getHistoricoGeral() != null && !entity.getHistoricoGeral().isEmpty()) {
            dto.setHistoricoGeral(entity.getHistoricoGeral().stream()
                    .map(this::convertHistoricoEntityToDTO).collect(Collectors.toList()));
        }
        if (entity.getConsultas() != null && !entity.getConsultas().isEmpty()) {
            dto.setConsultas(entity.getConsultas().stream()
                    .map(this::convertConsultaEntityToDTO).collect(Collectors.toList()));
        }
        if (entity.getExamesRegistrados() != null && !entity.getExamesRegistrados().isEmpty()) {
            dto.setExamesRegistrados(entity.getExamesRegistrados().stream()
                    .map(this::convertExameRegistroEntityToDTO).collect(Collectors.toList()));
        }
        if (entity.getProcedimentosRegistrados() != null && !entity.getProcedimentosRegistrados().isEmpty()) {
            dto.setProcedimentosRegistrados(entity.getProcedimentosRegistrados().stream()
                    .map(this::convertProcedimentoRegistroEntityToDTO).collect(Collectors.toList()));
        }
        if (entity.getEncaminhamentosRegistrados() != null && !entity.getEncaminhamentosRegistrados().isEmpty()) {
            dto.setEncaminhamentosRegistrados(entity.getEncaminhamentosRegistrados().stream()
                    .map(this::convertEncaminhamentoRegistroEntityToDTO).collect(Collectors.toList()));
        }
        return dto;
    }

    private HistoricoMedicoDTO convertHistoricoEntityToDTO(HistoricoMedicoEntity entity) {
        if (entity == null) return null;
        HistoricoMedicoDTO dto = new HistoricoMedicoDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private ConsultaDTO convertConsultaEntityToDTO(EntradaMedicaRegistroEntity entity) {
        if (entity == null) return null;
        ConsultaDTO dto = new ConsultaDTO();
        BeanUtils.copyProperties(entity, dto, "prontuario");
        dto.setId(entity.getId());

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
        return dto;
    }

    private ExameRegistroDTO convertExameRegistroEntityToDTO(ExameRegistroEntity entity) {
        if (entity == null) return null;
        ExameRegistroDTO dto = new ExameRegistroDTO();
        BeanUtils.copyProperties(entity, dto, "prontuario", "medicoResponsavelExame");
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
        BeanUtils.copyProperties(entity, dto, "prontuario", "medicoExecutor");
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
        BeanUtils.copyProperties(entity, dto, "prontuario", "medicoSolicitante");
        dto.setId(entity.getId());
        dto.setProntuarioId(entity.getProntuario().getId());
        if (entity.getMedicoSolicitante() != null) {
            dto.setMedicoSolicitanteId(entity.getMedicoSolicitante().getId());
            dto.setMedicoSolicitanteNome(entity.getMedicoSolicitante().getNomeCompleto());
            dto.setMedicoSolicitanteCRM(entity.getMedicoSolicitante().getCrm());
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public Page<ProntuarioDTO> buscarTodosProntuarios(Pageable pageable, String termo, String numeroProntuarioFilter) {
        logger.info("SERVICE: Iniciando busca de prontuários (retornando DTO básico). Termo: '{}', NumProntuario: '{}'", termo, numeroProntuarioFilter);

        Specification<ProntuarioEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            root.fetch("paciente", JoinType.LEFT);
            root.fetch("medicoResponsavel", JoinType.LEFT);

            if (StringUtils.hasText(termo)) {
                String termoLike = "%" + termo.toLowerCase() + "%";
                Join<ProntuarioEntity, PacienteEntity> pacienteJoinForFilter = root.join("paciente", JoinType.LEFT);
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("numeroProntuario")), termoLike),
                        cb.like(cb.lower(pacienteJoinForFilter.get("nome")), termoLike),
                        cb.like(pacienteJoinForFilter.get("cpf"), termoLike)
                ));
            }
            if (StringUtils.hasText(numeroProntuarioFilter)) {
                predicates.add(cb.like(cb.lower(root.get("numeroProntuario")), "%" + numeroProntuarioFilter.toLowerCase() + "%"));
            }
            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageableComSort = pageable;
        if (pageable.getSort().isUnsorted()) {
            pageableComSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("dataUltimaAtualizacao").descending());
        }

        Page<ProntuarioEntity> resultadoEntities = prontuarioRepository.findAll(spec, pageableComSort);
        Page<ProntuarioDTO> resultadoDTOs = resultadoEntities.map(this::convertProntuarioEntityToBasicListDTO); // Usa o conversor básico

        logger.info("SERVICE: Busca DTO (básico) concluída. Encontrados {} prontuários na página {} de {}.", resultadoDTOs.getNumberOfElements(), resultadoDTOs.getNumber() + 1, resultadoDTOs.getTotalPages());
        return resultadoDTOs;
    }

    @Transactional(readOnly = true)
    public ProntuarioDTO buscarProntuarioPorIdDetalhado(Long id) {
        ProntuarioEntity prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado com ID: " + id));

        prontuario.getConsultas().size();
        prontuario.getExamesRegistrados().size();
        prontuario.getProcedimentosRegistrados().size();
        prontuario.getEncaminhamentosRegistrados().size();
        prontuario.getHistoricoGeral().size();
        if (prontuario.getAdministradorCriador() != null) {
            prontuario.getAdministradorCriador().getNome();
        }

        return convertProntuarioEntityToDetailedDTO(prontuario);
    }

    private ProntuarioEntity findOrCreateProntuario(Long pacienteId, Long medicoResponsavelProntuarioId, AdministradorEntity adminLogado) {
        logger.debug("Procurando ou criando prontuário para paciente ID: {} com médico responsável ID: {}", pacienteId, medicoResponsavelProntuarioId);

        PacienteEntity paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com ID: " + pacienteId));

        Optional<ProntuarioEntity> prontuarioExistenteOpt = prontuarioRepository.findByPacienteId(pacienteId);

        if (prontuarioExistenteOpt.isPresent()) {
            ProntuarioEntity prontuarioExistente = prontuarioExistenteOpt.get();
            logger.info("Prontuário existente ID: {} encontrado para o paciente ID: {}", prontuarioExistente.getId(), pacienteId);
            return prontuarioExistente;
        } else {
            logger.info("Nenhum prontuário existente para o paciente ID: {}. Criando um novo.", pacienteId);
            MedicoEntity medicoResponsavel = medicoRepository.findById(medicoResponsavelProntuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Médico responsável pelo prontuário não encontrado com ID: " + medicoResponsavelProntuarioId));
            if (medicoResponsavel.getStatus() != StatusMedico.ATIVO) {
                throw new IllegalArgumentException("Médico selecionado ("+ medicoResponsavel.getNomeCompleto() +") para ser responsável pelo prontuário não está ativo.");
            }

            ProntuarioEntity novoProntuario = new ProntuarioEntity();
            novoProntuario.setPaciente(paciente);
            novoProntuario.setMedicoResponsavel(medicoResponsavel);
            novoProntuario.setAdministradorCriador(adminLogado);
            return prontuarioRepository.save(novoProntuario);
        }
    }

    @Transactional
    public EntradaMedicaRegistroEntity adicionarConsulta(
            Long pacienteId,
            CriarConsultaRequestDTO dto,
            AdministradorEntity adminLogado,
            Long medicoExecutorId,
            boolean criarProntuarioSeNaoExistir) {

        MedicoEntity medicoExecutor = medicoRepository.findById(medicoExecutorId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico executor da consulta não encontrado com ID: " + medicoExecutorId));
        if (medicoExecutor.getStatus() != StatusMedico.ATIVO) {
            throw new IllegalArgumentException("Médico executor da consulta ("+ medicoExecutor.getNomeCompleto() +") não está ativo.");
        }

        ProntuarioEntity prontuario = findOrCreateProntuario(pacienteId, medicoExecutorId, adminLogado);

        logger.info("Adicionando consulta ao prontuário ID: {} pelo admin: {}", prontuario.getId(), adminLogado.getEmail());

        EntradaMedicaRegistroEntity novaConsulta = new EntradaMedicaRegistroEntity();
        novaConsulta.setProntuario(prontuario);
        BeanUtils.copyProperties(dto, novaConsulta);
        novaConsulta.setResponsavelMedico(medicoExecutor);
        novaConsulta.setNomeResponsavelDisplay(medicoExecutor.getNomeCompleto());

        prontuario.setDataUltimaAtualizacao(LocalDateTime.now());
        return consultaRepository.save(novaConsulta);
    }

    @Transactional
    public ExameRegistroEntity adicionarExame(
            Long pacienteId,
            CriarExameRequestDTO dto,
            AdministradorEntity adminLogado,
            Long medicoResponsavelExameId,
            boolean criarProntuarioSeNaoExistir) {

        MedicoEntity medicoExame = medicoRepository.findById(medicoResponsavelExameId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico responsável pelo exame não encontrado com ID: " + medicoResponsavelExameId));
        if (medicoExame.getStatus() != StatusMedico.ATIVO) {
            throw new IllegalArgumentException("Médico responsável pelo exame ("+ medicoExame.getNomeCompleto() +") não está ativo.");
        }

        ProntuarioEntity prontuario = findOrCreateProntuario(pacienteId, medicoResponsavelExameId, adminLogado);

        logger.info("Adicionando exame ao prontuário ID: {} pelo admin: {}", prontuario.getId(), adminLogado.getEmail());

        ExameRegistroEntity novoExame = new ExameRegistroEntity();
        novoExame.setProntuario(prontuario);
        novoExame.setNome(dto.getNome());
        novoExame.setDataExame(dto.getData());
        novoExame.setResultado(dto.getResultado());
        novoExame.setObservacoes(dto.getObservacoes());
        novoExame.setMedicoResponsavelExame(medicoExame);
        novoExame.setNomeResponsavelDisplay(adminLogado.getNome());

        prontuario.setDataUltimaAtualizacao(LocalDateTime.now());
        return exameRepository.save(novoExame);
    }

    @Transactional
    public ProcedimentoRegistroEntity adicionarProcedimento(
            Long pacienteId,
            CriarProcedimentoRequestDTO dto,
            AdministradorEntity adminLogado,
            boolean criarProntuarioSeNaoExistir) {

        MedicoEntity medicoExecutor = medicoRepository.findById(dto.getMedicoExecutorId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico executor do procedimento não encontrado com ID: " + dto.getMedicoExecutorId()));
        if (medicoExecutor.getStatus() != StatusMedico.ATIVO) {
            throw new IllegalArgumentException("Médico executor do procedimento ("+ medicoExecutor.getNomeCompleto() +") não está ativo.");
        }

        ProntuarioEntity prontuario = findOrCreateProntuario(pacienteId, dto.getMedicoExecutorId(), adminLogado);

        logger.info("Adicionando procedimento ao prontuário ID: {} pelo admin: {}", prontuario.getId(), adminLogado.getEmail());

        ProcedimentoRegistroEntity novoProcedimento = new ProcedimentoRegistroEntity();
        novoProcedimento.setProntuario(prontuario);
        BeanUtils.copyProperties(dto, novoProcedimento, "medicoExecutorId");
        novoProcedimento.setMedicoExecutor(medicoExecutor);
        novoProcedimento.setNomeResponsavelDisplay(adminLogado.getNome());

        prontuario.setDataUltimaAtualizacao(LocalDateTime.now());
        return procedimentoRepository.save(novoProcedimento);
    }

    @Transactional
    public EncaminhamentoRegistroEntity adicionarEncaminhamento(
            Long pacienteId,
            CriarEncaminhamentoRequestDTO dto,
            AdministradorEntity adminLogado,
            boolean criarProntuarioSeNaoExistir) {

        MedicoEntity medicoSolicitante = medicoRepository.findById(dto.getMedicoSolicitanteId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico solicitante do encaminhamento não encontrado com ID: " + dto.getMedicoSolicitanteId()));
        if (medicoSolicitante.getStatus() != StatusMedico.ATIVO) {
            throw new IllegalArgumentException("Médico solicitante ("+ medicoSolicitante.getNomeCompleto() +") não está ativo.");
        }

        ProntuarioEntity prontuario = findOrCreateProntuario(pacienteId, dto.getMedicoSolicitanteId(), adminLogado);

        logger.info("Adicionando encaminhamento ao prontuário ID: {} pelo admin: {}", prontuario.getId(), adminLogado.getEmail());

        EncaminhamentoRegistroEntity novoEncaminhamento = new EncaminhamentoRegistroEntity();
        novoEncaminhamento.setProntuario(prontuario);
        BeanUtils.copyProperties(dto, novoEncaminhamento, "medicoSolicitanteId");
        novoEncaminhamento.setMedicoSolicitante(medicoSolicitante);
        novoEncaminhamento.setNomeResponsavelDisplay(adminLogado.getNome());

        prontuario.setDataUltimaAtualizacao(LocalDateTime.now());
        return encaminhamentoRepository.save(novoEncaminhamento);
    }


    @Transactional
    public ProntuarioEntity atualizarDadosBasicosProntuario(Long prontuarioId, Long medicoResponsavelIdNovo) {
        ProntuarioEntity prontuario = prontuarioRepository.findById(prontuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado com ID: " + prontuarioId));

        boolean modificado = false;
        if (medicoResponsavelIdNovo != null &&
                (prontuario.getMedicoResponsavel() == null || !medicoResponsavelIdNovo.equals(prontuario.getMedicoResponsavel().getId()))) {

            MedicoEntity medicoNovo = medicoRepository.findById(medicoResponsavelIdNovo)
                    .orElseThrow(() -> new ResourceNotFoundException("Médico responsável não encontrado com ID: " + medicoResponsavelIdNovo));

            if (medicoNovo.getStatus() != StatusMedico.ATIVO) {
                throw new IllegalArgumentException("Novo médico responsável selecionado ("+ medicoNovo.getNomeCompleto() +") não está ativo.");
            }
            prontuario.setMedicoResponsavel(medicoNovo);
            modificado = true;
        }

        if (modificado) {
            return prontuarioRepository.save(prontuario);
        }
        return prontuario;
    }
}