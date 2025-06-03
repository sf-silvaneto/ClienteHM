package com.clientehm.service;

import com.clientehm.entity.*;
import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.model.*; // Certifique-se que todos os DTOs de atualização estão aqui
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

    // --- MÉTODOS DE CONVERSÃO PARA DTO ---
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
        BeanUtils.copyProperties(paciente, dto, "endereco", "contato", "prontuarios");
        if (paciente.getGenero() != null) dto.setGenero(paciente.getGenero().name());
        if (paciente.getRacaCor() != null) dto.setRacaCor(paciente.getRacaCor().name()); else dto.setRacaCor(null);
        if (paciente.getTipoSanguineo() != null) dto.setTipoSanguineo(paciente.getTipoSanguineo().name()); else dto.setTipoSanguineo(null);

        dto.setAlergiasDeclaradas(paciente.getAlergiasDeclaradas());
        dto.setComorbidadesDeclaradas(paciente.getComorbidadesDeclaradas());
        dto.setMedicamentosContinuos(paciente.getMedicamentosContinuos());

        if (paciente.getEndereco() != null) {
            EnderecoDTO enderecoDTO = new EnderecoDTO();
            BeanUtils.copyProperties(paciente.getEndereco(), enderecoDTO);
            dto.setEndereco(enderecoDTO);
        }
        if (paciente.getContato() != null) {
            dto.setTelefone(paciente.getContato().getTelefone());
            dto.setEmail(paciente.getContato().getEmail());
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
        dto.setUpdatedAt(entity.getDataUltimaAtualizacao());

        if (entity.getPaciente() != null) {
            dto.setPaciente(convertPacienteToDTO(entity.getPaciente()));
        }
        if (entity.getMedicoResponsavel() != null) {
            dto.setMedicoResponsavel(convertMedicoToBasicDTO(entity.getMedicoResponsavel()));
        }
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
        if (entity.getProntuario() != null) dto.setProntuarioId(entity.getProntuario().getId());
        if (entity.getMedicoResponsavelExame() != null) {
            dto.setMedicoResponsavelExameId(entity.getMedicoResponsavelExame().getId());
            dto.setMedicoResponsavelExameNome(entity.getMedicoResponsavelExame().getNomeCompleto());
        }
        dto.setNomeResponsavelDisplay(entity.getNomeResponsavelDisplay());
        return dto;
    }

    private ProcedimentoRegistroDTO convertProcedimentoRegistroEntityToDTO(ProcedimentoRegistroEntity entity) {
        if (entity == null) return null;
        ProcedimentoRegistroDTO dto = new ProcedimentoRegistroDTO();
        BeanUtils.copyProperties(entity, dto, "prontuario", "medicoExecutor");
        dto.setId(entity.getId());
        if (entity.getProntuario() != null) dto.setProntuarioId(entity.getProntuario().getId());
        if (entity.getMedicoExecutor() != null) {
            dto.setMedicoExecutorId(entity.getMedicoExecutor().getId());
            dto.setMedicoExecutorNome(entity.getMedicoExecutor().getNomeCompleto());
        }
        dto.setNomeResponsavelDisplay(entity.getNomeResponsavelDisplay());
        return dto;
    }

    private EncaminhamentoRegistroDTO convertEncaminhamentoRegistroEntityToDTO(EncaminhamentoRegistroEntity entity) {
        if (entity == null) return null;
        EncaminhamentoRegistroDTO dto = new EncaminhamentoRegistroDTO();
        BeanUtils.copyProperties(entity, dto, "prontuario", "medicoSolicitante");
        dto.setId(entity.getId());
        if (entity.getProntuario() != null) dto.setProntuarioId(entity.getProntuario().getId());
        if (entity.getMedicoSolicitante() != null) {
            dto.setMedicoSolicitanteId(entity.getMedicoSolicitante().getId());
            dto.setMedicoSolicitanteNome(entity.getMedicoSolicitante().getNomeCompleto());
            dto.setMedicoSolicitanteCRM(entity.getMedicoSolicitante().getCrm());
            // Adicionar especialidade se fizer parte do DTO e da entidade Medico
            // dto.setMedicoSolicitanteEspecialidade(entity.getMedicoSolicitante().getEspecialidade());
        }
        dto.setNomeResponsavelDisplay(entity.getNomeResponsavelDisplay());
        return dto;
    }

    private ProntuarioDTO convertProntuarioEntityToDetailedDTO(ProntuarioEntity entity) {
        if (entity == null) return null;
        ProntuarioDTO dto = convertProntuarioEntityToBasicListDTO(entity);
        if (entity.getAdministradorCriador() != null) {
            dto.setAdministradorCriador(convertAdminToBasicDTO(entity.getAdministradorCriador()));
        }
        dto.setConsultas(entity.getConsultas().stream().map(this::convertConsultaEntityToDTO).collect(Collectors.toList()));
        dto.setExamesRegistrados(entity.getExamesRegistrados().stream().map(this::convertExameRegistroEntityToDTO).collect(Collectors.toList()));
        dto.setProcedimentosRegistrados(entity.getProcedimentosRegistrados().stream().map(this::convertProcedimentoRegistroEntityToDTO).collect(Collectors.toList()));
        dto.setEncaminhamentosRegistrados(entity.getEncaminhamentosRegistrados().stream().map(this::convertEncaminhamentoRegistroEntityToDTO).collect(Collectors.toList()));
        dto.setUpdatedAt(entity.getDataUltimaAtualizacao());
        return dto;
    }

    // --- MÉTODOS DE BUSCA ---
    @Transactional(readOnly = true)
    public Page<ProntuarioDTO> buscarTodosProntuarios(Pageable pageable, String termo, String numeroProntuarioFilter) {
        logger.info("SERVICE: Iniciando busca de prontuários. Termo: '{}', NumProntuario: '{}'", termo, numeroProntuarioFilter);
        Specification<ProntuarioEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            root.fetch("paciente", JoinType.LEFT);
            root.fetch("medicoResponsavel", JoinType.LEFT);
            if (StringUtils.hasText(termo)) {
                String termoLike = "%" + termo.toLowerCase() + "%";
                Join<ProntuarioEntity, PacienteEntity> pacienteJoin = root.join("paciente", JoinType.LEFT);
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("numeroProntuario")), termoLike),
                        cb.like(cb.lower(pacienteJoin.get("nome")), termoLike),
                        cb.like(pacienteJoin.get("cpf"), termoLike)
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
        return resultadoEntities.map(this::convertProntuarioEntityToBasicListDTO);
    }

    @Transactional(readOnly = true)
    public ProntuarioDTO buscarProntuarioPorIdDetalhado(Long id) {
        ProntuarioEntity prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado com ID: " + id));
        // Forçar o carregamento de coleções LAZY
        prontuario.getConsultas().size();
        prontuario.getExamesRegistrados().size();
        prontuario.getProcedimentosRegistrados().size();
        prontuario.getEncaminhamentosRegistrados().size();
        if(prontuario.getAdministradorCriador() != null) prontuario.getAdministradorCriador().getNome();
        if(prontuario.getPaciente() != null) prontuario.getPaciente().getNome();
        if(prontuario.getMedicoResponsavel() != null) prontuario.getMedicoResponsavel().getNomeCompleto();
        return convertProntuarioEntityToDetailedDTO(prontuario);
    }

    // --- HELPER PARA CRIAR PRONTUÁRIO OU BUSCAR EXISTENTE ---
    private ProntuarioEntity findOrCreateProntuario(Long pacienteId, Long medicoResponsavelProntuarioId, AdministradorEntity adminLogado) {
        PacienteEntity paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com ID: " + pacienteId));
        Optional<ProntuarioEntity> prontuarioExistenteOpt = prontuarioRepository.findByPacienteId(pacienteId);
        if (prontuarioExistenteOpt.isPresent()) {
            return prontuarioExistenteOpt.get();
        } else {
            MedicoEntity medicoRespProntuario = medicoRepository.findById(medicoResponsavelProntuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Médico responsável (ID: " + medicoResponsavelProntuarioId + ") pelo novo prontuário não encontrado."));
            if (medicoRespProntuario.getStatus() != StatusMedico.ATIVO) {
                throw new IllegalArgumentException("Médico ("+ medicoRespProntuario.getNomeCompleto() +") para ser responsável pelo prontuário não está ativo.");
            }
            ProntuarioEntity novoProntuario = new ProntuarioEntity();
            novoProntuario.setPaciente(paciente);
            novoProntuario.setMedicoResponsavel(medicoRespProntuario);
            novoProntuario.setAdministradorCriador(adminLogado);
            return prontuarioRepository.save(novoProntuario);
        }
    }

    // --- HELPER PARA ATUALIZAR DATA DO PRONTUÁRIO ---
    private void atualizarDataProntuario(ProntuarioEntity prontuario) {
        if (prontuario != null) {
            prontuario.setDataUltimaAtualizacao(LocalDateTime.now());
            prontuarioRepository.save(prontuario);
            logger.debug("Data de última atualização do prontuário ID {} atualizada para {}", prontuario.getId(), prontuario.getDataUltimaAtualizacao());
        }
    }

    // --- MÉTODOS DE CRIAÇÃO DE REGISTROS ---
    @Transactional
    public EntradaMedicaRegistroEntity adicionarConsulta(Long pacienteId, CriarConsultaRequestDTO dto, AdministradorEntity adminLogado, Long medicoExecutorId, boolean criarProntuarioSeNaoExistir) {
        MedicoEntity medicoExecutor = medicoRepository.findById(medicoExecutorId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico executor da consulta (ID: " + medicoExecutorId + ") não encontrado."));
        if (medicoExecutor.getStatus() != StatusMedico.ATIVO) {
            throw new IllegalArgumentException("Médico executor ("+ medicoExecutor.getNomeCompleto() +") não está ativo.");
        }
        ProntuarioEntity prontuario = findOrCreateProntuario(pacienteId, medicoExecutorId, adminLogado);
        EntradaMedicaRegistroEntity novaConsulta = new EntradaMedicaRegistroEntity();
        novaConsulta.setProntuario(prontuario);
        BeanUtils.copyProperties(dto, novaConsulta);
        novaConsulta.setResponsavelMedico(medicoExecutor);
        novaConsulta.setNomeResponsavelDisplay(medicoExecutor.getNomeCompleto()); // ou adminLogado.getNome() se o admin que registra
        EntradaMedicaRegistroEntity consultaSalva = consultaRepository.save(novaConsulta);
        atualizarDataProntuario(prontuario);
        return consultaSalva;
    }

    @Transactional
    public ExameRegistroEntity adicionarExame(Long pacienteId, CriarExameRequestDTO dto, AdministradorEntity adminLogado, Long medicoResponsavelExameId, boolean criarProntuarioSeNaoExistir) {
        MedicoEntity medicoExame = medicoRepository.findById(medicoResponsavelExameId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico responsável pelo exame (ID: " + medicoResponsavelExameId + ") não encontrado."));
        if (medicoExame.getStatus() != StatusMedico.ATIVO) {
            throw new IllegalArgumentException("Médico responsável pelo exame ("+ medicoExame.getNomeCompleto() +") não está ativo.");
        }
        ProntuarioEntity prontuario = findOrCreateProntuario(pacienteId, medicoResponsavelExameId, adminLogado);
        ExameRegistroEntity novoExame = new ExameRegistroEntity();
        novoExame.setProntuario(prontuario);
        novoExame.setNome(dto.getNome());
        novoExame.setDataExame(dto.getData());
        novoExame.setResultado(dto.getResultado());
        novoExame.setObservacoes(dto.getObservacoes());
        novoExame.setMedicoResponsavelExame(medicoExame);
        novoExame.setNomeResponsavelDisplay(adminLogado.getNome());
        ExameRegistroEntity exameSalvo = exameRepository.save(novoExame);
        atualizarDataProntuario(prontuario);
        return exameSalvo;
    }

    @Transactional
    public ProcedimentoRegistroEntity adicionarProcedimento(Long pacienteId, CriarProcedimentoRequestDTO dto, AdministradorEntity adminLogado, boolean criarProntuarioSeNaoExistir) {
        MedicoEntity medicoExecutor = medicoRepository.findById(dto.getMedicoExecutorId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico executor (ID: " + dto.getMedicoExecutorId() + ") não encontrado."));
        if (medicoExecutor.getStatus() != StatusMedico.ATIVO) {
            throw new IllegalArgumentException("Médico executor ("+ medicoExecutor.getNomeCompleto() +") não está ativo.");
        }
        ProntuarioEntity prontuario = findOrCreateProntuario(pacienteId, dto.getMedicoExecutorId(), adminLogado);
        ProcedimentoRegistroEntity novoProcedimento = new ProcedimentoRegistroEntity();
        novoProcedimento.setProntuario(prontuario);
        BeanUtils.copyProperties(dto, novoProcedimento, "medicoExecutorId");
        novoProcedimento.setMedicoExecutor(medicoExecutor);
        novoProcedimento.setNomeResponsavelDisplay(adminLogado.getNome());
        ProcedimentoRegistroEntity procedimentoSalvo = procedimentoRepository.save(novoProcedimento);
        atualizarDataProntuario(prontuario);
        return procedimentoSalvo;
    }

    @Transactional
    public EncaminhamentoRegistroEntity adicionarEncaminhamento(Long pacienteId, CriarEncaminhamentoRequestDTO dto, AdministradorEntity adminLogado, boolean criarProntuarioSeNaoExistir) {
        MedicoEntity medicoSolicitante = medicoRepository.findById(dto.getMedicoSolicitanteId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico solicitante (ID: " + dto.getMedicoSolicitanteId() + ") não encontrado."));
        if (medicoSolicitante.getStatus() != StatusMedico.ATIVO) {
            throw new IllegalArgumentException("Médico solicitante ("+ medicoSolicitante.getNomeCompleto() +") não está ativo.");
        }
        ProntuarioEntity prontuario = findOrCreateProntuario(pacienteId, dto.getMedicoSolicitanteId(), adminLogado);
        EncaminhamentoRegistroEntity novoEncaminhamento = new EncaminhamentoRegistroEntity();
        novoEncaminhamento.setProntuario(prontuario);
        BeanUtils.copyProperties(dto, novoEncaminhamento, "medicoSolicitanteId");
        novoEncaminhamento.setMedicoSolicitante(medicoSolicitante);
        novoEncaminhamento.setNomeResponsavelDisplay(adminLogado.getNome());
        EncaminhamentoRegistroEntity encaminhamentoSalvo = encaminhamentoRepository.save(novoEncaminhamento);
        atualizarDataProntuario(prontuario);
        return encaminhamentoSalvo;
    }

    // --- MÉTODOS DE ATUALIZAÇÃO DE REGISTROS ---
    @Transactional
    public EntradaMedicaRegistroEntity atualizarConsulta(Long consultaId, AtualizarConsultaRequestDTO dto, AdministradorEntity adminLogado) {
        logger.debug("SERVICE: Tentando atualizar consulta ID: {}", consultaId);
        EntradaMedicaRegistroEntity c = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com ID: " + consultaId));
        boolean modificado = false;

        if (dto.getDataHoraConsulta() != null && !dto.getDataHoraConsulta().equals(c.getDataHoraConsulta())) { c.setDataHoraConsulta(dto.getDataHoraConsulta()); modificado = true; }
        if (StringUtils.hasText(dto.getMotivoConsulta()) && !dto.getMotivoConsulta().equals(c.getMotivoConsulta())) { c.setMotivoConsulta(dto.getMotivoConsulta()); modificado = true; }
        if (StringUtils.hasText(dto.getQueixasPrincipais()) && !dto.getQueixasPrincipais().equals(c.getQueixasPrincipais())) { c.setQueixasPrincipais(dto.getQueixasPrincipais()); modificado = true; }

        // Campos opcionais - só atualiza se DTO tiver valor, ou permite limpar se DTO explicitamente manda vazio/null
        if (dto.getPressaoArterial() != null) { String val = StringUtils.hasText(dto.getPressaoArterial()) ? dto.getPressaoArterial().trim() : null; if (!java.util.Objects.equals(c.getPressaoArterial(), val)) { c.setPressaoArterial(val); modificado = true; }}
        if (dto.getTemperatura() != null) { String val = StringUtils.hasText(dto.getTemperatura()) ? dto.getTemperatura().trim() : null; if (!java.util.Objects.equals(c.getTemperatura(), val)) { c.setTemperatura(val); modificado = true; }}
        if (dto.getFrequenciaCardiaca() != null) { String val = StringUtils.hasText(dto.getFrequenciaCardiaca()) ? dto.getFrequenciaCardiaca().trim() : null; if (!java.util.Objects.equals(c.getFrequenciaCardiaca(), val)) { c.setFrequenciaCardiaca(val); modificado = true; }}
        if (dto.getSaturacao() != null) { String val = StringUtils.hasText(dto.getSaturacao()) ? dto.getSaturacao().trim() : null; if (!java.util.Objects.equals(c.getSaturacao(), val)) { c.setSaturacao(val); modificado = true; }}
        if (dto.getExameFisico() != null) { String val = StringUtils.hasText(dto.getExameFisico()) ? dto.getExameFisico().trim() : null; if (!java.util.Objects.equals(c.getExameFisico(), val)) { c.setExameFisico(val); modificado = true; }}
        if (dto.getHipoteseDiagnostica() != null) { String val = StringUtils.hasText(dto.getHipoteseDiagnostica()) ? dto.getHipoteseDiagnostica().trim() : null; if (!java.util.Objects.equals(c.getHipoteseDiagnostica(), val)) { c.setHipoteseDiagnostica(val); modificado = true; }}
        if (dto.getCondutaPlanoTerapeutico() != null) { String val = StringUtils.hasText(dto.getCondutaPlanoTerapeutico()) ? dto.getCondutaPlanoTerapeutico().trim() : null; if (!java.util.Objects.equals(c.getCondutaPlanoTerapeutico(), val)) { c.setCondutaPlanoTerapeutico(val); modificado = true; }}
        if (dto.getDetalhesConsulta() != null) { String val = StringUtils.hasText(dto.getDetalhesConsulta()) ? dto.getDetalhesConsulta().trim() : null; if (!java.util.Objects.equals(c.getDetalhesConsulta(), val)) { c.setDetalhesConsulta(val); modificado = true; }}
        if (dto.getObservacoesConsulta() != null) { String val = StringUtils.hasText(dto.getObservacoesConsulta()) ? dto.getObservacoesConsulta().trim() : null; if (!java.util.Objects.equals(c.getObservacoesConsulta(), val)) { c.setObservacoesConsulta(val); modificado = true; }}

        if (dto.getMedicoExecutorId() != null) { // Se um novo ID de médico é fornecido
            if (c.getResponsavelMedico() == null || !dto.getMedicoExecutorId().equals(c.getResponsavelMedico().getId())) {
                MedicoEntity novoMedico = medicoRepository.findById(dto.getMedicoExecutorId())
                        .orElseThrow(() -> new ResourceNotFoundException("Médico executor não encontrado: ID " + dto.getMedicoExecutorId()));
                if (novoMedico.getStatus() != StatusMedico.ATIVO) throw new IllegalArgumentException("Médico executor (" + novoMedico.getNomeCompleto() + ") não está ativo.");
                c.setResponsavelMedico(novoMedico);
                c.setResponsavelAdmin(null); // Limpa o admin se um médico for definido
                c.setNomeResponsavelDisplay(novoMedico.getNomeCompleto());
                modificado = true;
            }
        } else if (dto.getMedicoExecutorId() == null && c.getResponsavelMedico() != null) { // Se ID do médico é explicitamente null (para remover)
            c.setResponsavelMedico(null);
            // Decide quem é o responsável agora, ex: o admin que editou
            // c.setResponsavelAdmin(adminLogado);
            // c.setNomeResponsavelDisplay(adminLogado.getNome());
            // Se não há médico e não há admin, o display pode ser o admin que fez a alteração
            if(c.getResponsavelAdmin() == null) {
                c.setNomeResponsavelDisplay(adminLogado.getNome());
            } // Se já existia um admin, mantém
            modificado = true;
        }

        if(modificado) {
            c.setNomeResponsavelDisplay(adminLogado.getNome()); // Registra quem fez a última alteração
            EntradaMedicaRegistroEntity atualizada = consultaRepository.save(c);
            atualizarDataProntuario(atualizada.getProntuario());
            return atualizada;
        }
        return c;
    }

    @Transactional
    public ExameRegistroEntity atualizarExame(Long exameId, AtualizarExameRequestDTO dto, AdministradorEntity adminLogado) {
        logger.debug("SERVICE: Tentando atualizar exame ID: {}", exameId);
        ExameRegistroEntity e = exameRepository.findById(exameId)
                .orElseThrow(() -> new ResourceNotFoundException("Exame não encontrado com ID: " + exameId));
        boolean modificado = false;

        if (StringUtils.hasText(dto.getNome()) && !dto.getNome().equals(e.getNome())) { e.setNome(dto.getNome()); modificado = true; }
        if (dto.getData() != null && !dto.getData().equals(e.getDataExame())) { e.setDataExame(dto.getData()); modificado = true; }
        if (StringUtils.hasText(dto.getResultado()) && !dto.getResultado().equals(e.getResultado())) { e.setResultado(dto.getResultado()); modificado = true; }
        if (dto.getObservacoes() != null) { String val = StringUtils.hasText(dto.getObservacoes()) ? dto.getObservacoes().trim() : null; if (!java.util.Objects.equals(e.getObservacoes(), val)) { e.setObservacoes(val); modificado = true; }}

        if (dto.getMedicoResponsavelExameId() != null) {
            if (e.getMedicoResponsavelExame() == null || !dto.getMedicoResponsavelExameId().equals(e.getMedicoResponsavelExame().getId())) {
                MedicoEntity novoMedico = medicoRepository.findById(dto.getMedicoResponsavelExameId())
                        .orElseThrow(() -> new ResourceNotFoundException("Médico responsável pelo exame não encontrado: ID " + dto.getMedicoResponsavelExameId()));
                if (novoMedico.getStatus() != StatusMedico.ATIVO) throw new IllegalArgumentException("Médico responsável (" + novoMedico.getNomeCompleto() + ") não está ativo.");
                e.setMedicoResponsavelExame(novoMedico);
                modificado = true;
            }
        } else if (dto.getMedicoResponsavelExameId() == null && e.getMedicoResponsavelExame() != null) {
            e.setMedicoResponsavelExame(null);
            modificado = true;
        }

        if(modificado) {
            e.setNomeResponsavelDisplay(adminLogado.getNome()); // Admin que fez a alteração
            ExameRegistroEntity atualizado = exameRepository.save(e);
            atualizarDataProntuario(atualizado.getProntuario());
            return atualizado;
        }
        return e;
    }

    @Transactional
    public ProcedimentoRegistroEntity atualizarProcedimento(Long procedimentoId, AtualizarProcedimentoRequestDTO dto, AdministradorEntity adminLogado) {
        logger.debug("SERVICE: Tentando atualizar procedimento ID: {}", procedimentoId);
        ProcedimentoRegistroEntity p = procedimentoRepository.findById(procedimentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Procedimento não encontrado com ID: " + procedimentoId));
        boolean modificado = false;

        if (dto.getDataProcedimento() != null && !dto.getDataProcedimento().equals(p.getDataProcedimento())) { p.setDataProcedimento(dto.getDataProcedimento()); modificado = true; }
        if (StringUtils.hasText(dto.getDescricaoProcedimento()) && !dto.getDescricaoProcedimento().equals(p.getDescricaoProcedimento())) { p.setDescricaoProcedimento(dto.getDescricaoProcedimento()); modificado = true; }
        if (dto.getRelatorioProcedimento() != null) { String val = StringUtils.hasText(dto.getRelatorioProcedimento()) ? dto.getRelatorioProcedimento().trim() : null; if (!java.util.Objects.equals(p.getRelatorioProcedimento(), val)) { p.setRelatorioProcedimento(val); modificado = true; }}

        if (dto.getMedicoExecutorId() != null) {
            if (p.getMedicoExecutor() == null || !dto.getMedicoExecutorId().equals(p.getMedicoExecutor().getId())) {
                MedicoEntity novoMedico = medicoRepository.findById(dto.getMedicoExecutorId())
                        .orElseThrow(() -> new ResourceNotFoundException("Médico executor não encontrado: ID " + dto.getMedicoExecutorId()));
                if (novoMedico.getStatus() != StatusMedico.ATIVO) throw new IllegalArgumentException("Médico executor (" + novoMedico.getNomeCompleto() + ") não está ativo.");
                p.setMedicoExecutor(novoMedico);
                modificado = true;
            }
        } else if (dto.getMedicoExecutorId() == null && p.getMedicoExecutor() != null) {
            throw new IllegalArgumentException("ID do médico executor é obrigatório para procedimentos e não pode ser removido se já existente.");
        }

        if(modificado) {
            p.setNomeResponsavelDisplay(adminLogado.getNome()); // Admin que fez a alteração
            ProcedimentoRegistroEntity atualizado = procedimentoRepository.save(p);
            atualizarDataProntuario(atualizado.getProntuario());
            return atualizado;
        }
        return p;
    }

    @Transactional
    public EncaminhamentoRegistroEntity atualizarEncaminhamento(Long encaminhamentoId, AtualizarEncaminhamentoRequestDTO dto, AdministradorEntity adminLogado) {
        logger.debug("SERVICE: Tentando atualizar encaminhamento ID: {}", encaminhamentoId);
        EncaminhamentoRegistroEntity enc = encaminhamentoRepository.findById(encaminhamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Encaminhamento não encontrado com ID: " + encaminhamentoId));
        boolean modificado = false;

        if (dto.getDataEncaminhamento() != null && !dto.getDataEncaminhamento().equals(enc.getDataEncaminhamento())) { enc.setDataEncaminhamento(dto.getDataEncaminhamento()); modificado = true; }
        if (StringUtils.hasText(dto.getEspecialidadeDestino()) && !dto.getEspecialidadeDestino().equals(enc.getEspecialidadeDestino())) { enc.setEspecialidadeDestino(dto.getEspecialidadeDestino()); modificado = true; }
        if (StringUtils.hasText(dto.getMotivoEncaminhamento()) && !dto.getMotivoEncaminhamento().equals(enc.getMotivoEncaminhamento())) { enc.setMotivoEncaminhamento(dto.getMotivoEncaminhamento()); modificado = true; }
        if (dto.getObservacoes() != null) { String val = StringUtils.hasText(dto.getObservacoes()) ? dto.getObservacoes().trim() : null; if (!java.util.Objects.equals(enc.getObservacoes(), val)) { enc.setObservacoes(val); modificado = true; }}

        if (dto.getMedicoSolicitanteId() != null) {
            if (enc.getMedicoSolicitante() == null || !dto.getMedicoSolicitanteId().equals(enc.getMedicoSolicitante().getId())) {
                MedicoEntity novoMedico = medicoRepository.findById(dto.getMedicoSolicitanteId())
                        .orElseThrow(() -> new ResourceNotFoundException("Médico solicitante não encontrado: ID " + dto.getMedicoSolicitanteId()));
                if (novoMedico.getStatus() != StatusMedico.ATIVO) throw new IllegalArgumentException("Médico solicitante (" + novoMedico.getNomeCompleto() + ") não está ativo.");
                enc.setMedicoSolicitante(novoMedico);
                modificado = true;
            }
        } else if (dto.getMedicoSolicitanteId() == null && enc.getMedicoSolicitante() != null) {
            throw new IllegalArgumentException("ID do médico solicitante é obrigatório para encaminhamentos e não pode ser removido se já existente.");
        }

        if(modificado) {
            enc.setNomeResponsavelDisplay(adminLogado.getNome()); // Admin que fez a alteração
            EncaminhamentoRegistroEntity atualizado = encaminhamentoRepository.save(enc);
            atualizarDataProntuario(atualizado.getProntuario());
            return atualizado;
        }
        return enc;
    }

    // --- ATUALIZAÇÃO DE DADOS BÁSICOS DO PRONTUÁRIO ---
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
            // A data de última atualização é gerenciada pela anotação @PreUpdate na ProntuarioEntity
            return prontuarioRepository.save(prontuario);
        }
        return prontuario;
    }
}