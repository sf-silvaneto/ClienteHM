package com.clientehm.service;

import com.clientehm.entity.*;
import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.mapper.*;
import com.clientehm.model.*;
import com.clientehm.repository.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class ProntuarioService {

    private static final Logger logger = LoggerFactory.getLogger(ProntuarioService.class);

    @Autowired private ProntuarioRepository prontuarioRepository;
    @Autowired private PacienteRepository pacienteRepository;
    @Autowired private MedicoRepository medicoRepository;
    @Autowired private EntradaMedicaRegistroRepository consultaRepository;
    @Autowired private ExameRegistroRepository exameRepository;
    @Autowired private ProcedimentoRegistroRepository procedimentoRepository;
    @Autowired private EncaminhamentoRegistroRepository encaminhamentoRepository;
    @Autowired private SinaisVitaisRepository sinaisVitaisRepository;
    @Autowired private ProntuarioMapper prontuarioMapper;
    @Autowired private ConsultaMapper consultaMapper;
    @Autowired private ExameMapper exameMapper;
    @Autowired private ProcedimentoMapper procedimentoMapper;
    @Autowired private EncaminhamentoMapper encaminhamentoMapper;

    private void inicializarConsultaCompletamente(ConsultaRegistroEntity consulta) {
        if (consulta != null) {
            if (consulta.getProntuario() != null) consulta.getProntuario().getId();
            if (consulta.getResponsavelMedico() != null) consulta.getResponsavelMedico().getId();
            if (consulta.getSinaisVitais() != null) consulta.getSinaisVitais().getId();
            if (consulta.getDataConsulta() != null) consulta.getDataConsulta();
        }
    }
    private void inicializarExameCompletamente(ExameRegistroEntity exame) {
        if (exame != null) {
            if (exame.getProntuario() != null) exame.getProntuario().getId();
            if (exame.getMedicoResponsavelExame() != null) exame.getMedicoResponsavelExame().getId();
            if (exame.getDataExame() != null) exame.getDataExame();
        }
    }
    private void inicializarProcedimentoCompletamente(ProcedimentoRegistroEntity procedimento) {
        if (procedimento != null) {
            if (procedimento.getProntuario() != null) procedimento.getProntuario().getId();
            if (procedimento.getMedicoExecutor() != null) procedimento.getMedicoExecutor().getId();
            if (procedimento.getDataProcedimento() != null) procedimento.getDataProcedimento();
        }
    }
    private void inicializarEncaminhamentoCompletamente(EncaminhamentoRegistroEntity encaminhamento) {
        if (encaminhamento != null) {
            if (encaminhamento.getProntuario() != null) encaminhamento.getProntuario().getId();
            if (encaminhamento.getMedicoSolicitante() != null) encaminhamento.getMedicoSolicitante().getId();
            if (encaminhamento.getDataEncaminhamento() != null) encaminhamento.getDataEncaminhamento();
        }
    }
    private void inicializarPacienteCompleto(PacienteEntity paciente) {
        if (paciente != null) {
            if (paciente.getEndereco() != null) paciente.getEndereco().getCep();
            if (paciente.getContato() != null) paciente.getContato().getEmail();
            if (paciente.getAlergias() != null) paciente.getAlergias().size();
            if (paciente.getComorbidades() != null) paciente.getComorbidades().size();
            if (paciente.getMedicamentosContinuos() != null) paciente.getMedicamentosContinuos().size();
        }
    }

    @Transactional(readOnly = true)
    public Page<ProntuarioDTO> buscarTodosProntuarios(Pageable pageable, String termo, String numeroProntuarioFilter) {
        logger.info("SERVICE: Buscando prontuários. Termo: '{}', NumProntuario: '{}'", termo, numeroProntuarioFilter);
        Specification<ProntuarioEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            root.fetch("paciente", JoinType.LEFT).fetch("contato", JoinType.LEFT);
            root.fetch("paciente").fetch("endereco", JoinType.LEFT);
            root.fetch("medicoResponsavel", JoinType.LEFT);
            root.fetch("administradorCriador", JoinType.LEFT);

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
        Pageable pageableParaConsulta = pageable;
        if (pageable.getSort().isUnsorted()) {
            pageableParaConsulta = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "updatedAt"));
        }
        Page<ProntuarioEntity> resultadoEntities = prontuarioRepository.findAll(spec, pageableParaConsulta);
        return prontuarioMapper.toBasicDTOPage(resultadoEntities);
    }

    @Transactional(readOnly = true)
    public ProntuarioDTO buscarProntuarioPorIdDetalhado(Long id) {
        ProntuarioEntity prontuario = prontuarioRepository.findByIdFetchingCollections(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado com ID: " + id));

        if (prontuario.getConsultas() != null) prontuario.getConsultas().forEach(this::inicializarConsultaCompletamente);
        if (prontuario.getExamesRegistrados() != null) prontuario.getExamesRegistrados().forEach(this::inicializarExameCompletamente);
        if (prontuario.getProcedimentosRegistrados() != null) prontuario.getProcedimentosRegistrados().forEach(this::inicializarProcedimentoCompletamente);
        if (prontuario.getEncaminhamentosRegistrados() != null) prontuario.getEncaminhamentosRegistrados().forEach(this::inicializarEncaminhamentoCompletamente);

        if(prontuario.getPaciente() != null) inicializarPacienteCompleto(prontuario.getPaciente());
        if(prontuario.getMedicoResponsavel() != null) prontuario.getMedicoResponsavel().getNomeCompleto();
        if(prontuario.getAdministradorCriador() != null) prontuario.getAdministradorCriador().getNome();

        return prontuarioMapper.toDetailedDTO(prontuario);
    }

    private ProntuarioEntity findOrCreateProntuario(Long pacienteId, Long medicoIdReferencia, AdministradorEntity adminLogado) {
        PacienteEntity paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com ID: " + pacienteId));

        MedicoEntity medicoRef = medicoRepository.findById(medicoIdReferencia)
                .orElseThrow(() -> new ResourceNotFoundException("Médico de referência (ID: " + medicoIdReferencia + ") não encontrado."));
        if (medicoRef.getDeletedAt() != null) {
            throw new IllegalArgumentException("Médico ("+ medicoRef.getNomeCompleto() +") de referência para o prontuário não está ativo.");
        }

        Optional<ProntuarioEntity> prontuarioExistenteOpt = prontuarioRepository.findByPacienteId(pacienteId);

        if (prontuarioExistenteOpt.isPresent()) {
            ProntuarioEntity prontuarioExistente = prontuarioExistenteOpt.get();
            boolean modificado = false;
            if (prontuarioExistente.getMedicoResponsavel() == null || !prontuarioExistente.getMedicoResponsavel().getId().equals(medicoRef.getId())) {
                prontuarioExistente.setMedicoResponsavel(medicoRef);
                modificado = true;
            }
            if (prontuarioExistente.getAdministradorCriador() == null && adminLogado != null) {
                prontuarioExistente.setAdministradorCriador(adminLogado);
                modificado = true;
            }
            if (modificado) {
                return prontuarioRepository.save(prontuarioExistente);
            }
            return prontuarioExistente;
        } else {
            ProntuarioEntity novoProntuario = new ProntuarioEntity();
            novoProntuario.setPaciente(paciente);
            novoProntuario.setMedicoResponsavel(medicoRef);
            novoProntuario.setAdministradorCriador(adminLogado);
            return prontuarioRepository.save(novoProntuario);
        }
    }

    private void atualizarDataProntuario(ProntuarioEntity prontuario) {
        prontuarioRepository.saveAndFlush(prontuario);
    }

    @Transactional
    public ConsultaDTO adicionarConsultaERetornarDTO(Long pacienteId, CriarConsultaRequestDTO dto, AdministradorEntity adminLogado, Long medicoExecutorId) {
        MedicoEntity medicoExecutor = medicoRepository.findById(medicoExecutorId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico executor da consulta (ID: " + medicoExecutorId + ") não encontrado."));
        if (medicoExecutor.getDeletedAt() != null) {
            throw new IllegalArgumentException("Médico ("+ medicoExecutor.getNomeCompleto() +") não está ativo.");
        }
        ProntuarioEntity prontuario = findOrCreateProntuario(pacienteId, medicoExecutorId, adminLogado);

        ConsultaRegistroEntity novaConsulta = consultaMapper.toEntity(dto);
        novaConsulta.setProntuario(prontuario);
        novaConsulta.setResponsavelMedico(medicoExecutor);

        if (novaConsulta.getDataConsulta() == null) {
            novaConsulta.setDataConsulta(LocalDateTime.now());
        }

        if (dto.getSinaisVitais() != null) {
            SinaisVitaisEntity sinaisVitais = new SinaisVitaisEntity();
            sinaisVitais.setPressaoArterial(dto.getSinaisVitais().getPressaoArterial());
            sinaisVitais.setTemperatura(dto.getSinaisVitais().getTemperatura());
            sinaisVitais.setFrequenciaCardiaca(dto.getSinaisVitais().getFrequenciaCardiaca());
            sinaisVitais.setSaturacao(dto.getSinaisVitais().getSaturacao());
            sinaisVitais.setHgt(dto.getSinaisVitais().getHgt());
            novaConsulta.setSinaisVitais(sinaisVitais);
        }

        ConsultaRegistroEntity consultaSalva = consultaRepository.save(novaConsulta);

        if (consultaSalva.getSinaisVitais() != null) {
            consultaSalva.getSinaisVitais().setConsulta(consultaSalva);
        }

        atualizarDataProntuario(prontuario);
        return consultaMapper.toDTO(consultaSalva);
    }

    @Transactional
    public ExameRegistroDTO adicionarExameERetornarDTO(Long pacienteId, CriarExameRequestDTO dto, AdministradorEntity adminLogado, Long medicoResponsavelExameId) {
        MedicoEntity medicoExame = medicoRepository.findById(medicoResponsavelExameId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico responsável pelo exame (ID: " + medicoResponsavelExameId + ") não encontrado."));
        if (medicoExame.getDeletedAt() != null) {
            throw new IllegalArgumentException("Médico responsável pelo exame ("+ medicoExame.getNomeCompleto() +") não está ativo.");
        }
        ProntuarioEntity prontuario = findOrCreateProntuario(pacienteId, medicoResponsavelExameId, adminLogado);

        ExameRegistroEntity novoExame = exameMapper.toEntity(dto);
        novoExame.setProntuario(prontuario);
        novoExame.setMedicoResponsavelExame(medicoExame);
        if (novoExame.getDataExame() == null) {
            novoExame.setDataExame(LocalDateTime.now());
        }

        ExameRegistroEntity exameSalvo = exameRepository.save(novoExame);
        atualizarDataProntuario(prontuario);
        return exameMapper.toDTO(exameSalvo);
    }

    @Transactional
    public ProcedimentoRegistroDTO adicionarProcedimentoERetornarDTO(Long pacienteId, CriarProcedimentoRequestDTO dto, AdministradorEntity adminLogado) {
        MedicoEntity medicoExecutor = medicoRepository.findById(dto.getMedicoExecutorId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico executor do procedimento (ID: " + dto.getMedicoExecutorId() + ") não encontrado."));
        if (medicoExecutor.getDeletedAt() != null) {
            throw new IllegalArgumentException("Médico executor ("+ medicoExecutor.getNomeCompleto() +") não está ativo.");
        }
        ProntuarioEntity prontuario = findOrCreateProntuario(pacienteId, medicoExecutor.getId(), adminLogado);

        ProcedimentoRegistroEntity novoProcedimento = procedimentoMapper.toEntity(dto);
        novoProcedimento.setProntuario(prontuario);
        novoProcedimento.setMedicoExecutor(medicoExecutor);
        if (novoProcedimento.getDataProcedimento() == null) {
            novoProcedimento.setDataProcedimento(LocalDateTime.now());
        }

        ProcedimentoRegistroEntity procedimentoSalvo = procedimentoRepository.save(novoProcedimento);
        atualizarDataProntuario(prontuario);
        return procedimentoMapper.toDTO(procedimentoSalvo);
    }

    @Transactional
    public EncaminhamentoRegistroDTO adicionarEncaminhamentoERetornarDTO(Long pacienteId, CriarEncaminhamentoRequestDTO dto, AdministradorEntity adminLogado) {
        MedicoEntity medicoSolicitante = medicoRepository.findById(dto.getMedicoSolicitanteId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico solicitante do encaminhamento (ID: " + dto.getMedicoSolicitanteId() + ") não encontrado."));
        if (medicoSolicitante.getDeletedAt() != null) {
            throw new IllegalArgumentException("Médico solicitante ("+ medicoSolicitante.getNomeCompleto() +") não está ativo.");
        }
        ProntuarioEntity prontuario = findOrCreateProntuario(pacienteId, medicoSolicitante.getId(), adminLogado);

        EncaminhamentoRegistroEntity novoEncaminhamento = encaminhamentoMapper.toEntity(dto);
        novoEncaminhamento.setProntuario(prontuario);
        novoEncaminhamento.setMedicoSolicitante(medicoSolicitante);
        if (novoEncaminhamento.getDataEncaminhamento() == null) {
            novoEncaminhamento.setDataEncaminhamento(LocalDateTime.now());
        }

        EncaminhamentoRegistroEntity encaminhamentoSalvo = encaminhamentoRepository.save(novoEncaminhamento);
        atualizarDataProntuario(prontuario);
        return encaminhamentoMapper.toDTO(encaminhamentoSalvo);
    }

    @Transactional
    public ConsultaDTO atualizarConsultaERetornarDTO(Long consultaId, AtualizarConsultaRequestDTO dto, AdministradorEntity adminLogado) {
        ConsultaRegistroEntity consultaExistente = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com ID: " + consultaId));

        MedicoEntity medicoExecutor = medicoRepository.findById(dto.getMedicoExecutorId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico executor não encontrado: ID " + dto.getMedicoExecutorId()));
        if (medicoExecutor.getDeletedAt() != null) {
            throw new IllegalArgumentException("Médico (" + medicoExecutor.getNomeCompleto() + ") não está ativo.");
        }

        consultaMapper.updateEntityFromDTO(dto, consultaExistente, medicoExecutor, adminLogado);

        if (dto.getSinaisVitais() != null) {
            SinaisVitaisEntity sinaisVitais = consultaExistente.getSinaisVitais();
            if (sinaisVitais == null) {
                sinaisVitais = new SinaisVitaisEntity();
                sinaisVitais.setId(consultaExistente.getId());
                sinaisVitais.setConsulta(consultaExistente);
            }
            sinaisVitais.setPressaoArterial(dto.getSinaisVitais().getPressaoArterial());
            sinaisVitais.setTemperatura(dto.getSinaisVitais().getTemperatura());
            sinaisVitais.setFrequenciaCardiaca(dto.getSinaisVitais().getFrequenciaCardiaca());
            sinaisVitais.setSaturacao(dto.getSinaisVitais().getSaturacao());
            sinaisVitais.setHgt(dto.getSinaisVitais().getHgt());
            sinaisVitaisRepository.save(sinaisVitais);
            consultaExistente.setSinaisVitais(sinaisVitais);
        } else if (consultaExistente.getSinaisVitais() != null) {
        }

        ConsultaRegistroEntity consultaAtualizada = consultaRepository.save(consultaExistente);
        atualizarDataProntuario(consultaAtualizada.getProntuario());
        return consultaMapper.toDTO(consultaAtualizada);
    }

    @Transactional
    public ExameRegistroDTO atualizarExameERetornarDTO(Long exameId, AtualizarExameRequestDTO dto, AdministradorEntity adminLogado) {
        ExameRegistroEntity exameExistente = exameRepository.findById(exameId)
                .orElseThrow(() -> new ResourceNotFoundException("Exame não encontrado com ID: " + exameId));

        MedicoEntity medicoResponsavelExame = null;
        if (dto.getMedicoResponsavelExameId() != null) {
            medicoResponsavelExame = medicoRepository.findById(dto.getMedicoResponsavelExameId())
                    .orElseThrow(() -> new ResourceNotFoundException("Médico responsável pelo exame não encontrado: ID " + dto.getMedicoResponsavelExameId()));
            if (medicoResponsavelExame.getDeletedAt() != null) {
                throw new IllegalArgumentException("Médico responsável (" + medicoResponsavelExame.getNomeCompleto() + ") não está ativo.");
            }
        }
        exameMapper.updateEntityFromDTO(dto, exameExistente, medicoResponsavelExame, adminLogado);

        ExameRegistroEntity exameAtualizado = exameRepository.save(exameExistente);
        atualizarDataProntuario(exameAtualizado.getProntuario());
        return exameMapper.toDTO(exameAtualizado);
    }

    @Transactional
    public ProcedimentoRegistroDTO atualizarProcedimentoERetornarDTO(Long procedimentoId, AtualizarProcedimentoRequestDTO dto, AdministradorEntity adminLogado) {
        ProcedimentoRegistroEntity procedimentoExistente = procedimentoRepository.findById(procedimentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Procedimento não encontrado com ID: " + procedimentoId));

        MedicoEntity medicoExecutor;
        if (dto.getMedicoExecutorId() != null) {
            medicoExecutor = medicoRepository.findById(dto.getMedicoExecutorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Médico executor não encontrado: ID " + dto.getMedicoExecutorId()));
            if (medicoExecutor.getDeletedAt() != null) {
                throw new IllegalArgumentException("Médico executor (" + medicoExecutor.getNomeCompleto() + ") não está ativo.");
            }
        } else {
            throw new IllegalArgumentException("ID do médico executor é obrigatório para atualizar procedimentos.");
        }

        procedimentoMapper.updateEntityFromDTO(dto, procedimentoExistente, medicoExecutor);

        ProcedimentoRegistroEntity procedimentoAtualizado = procedimentoRepository.save(procedimentoExistente);
        atualizarDataProntuario(procedimentoAtualizado.getProntuario());
        return procedimentoMapper.toDTO(procedimentoAtualizado);
    }

    @Transactional
    public EncaminhamentoRegistroDTO atualizarEncaminhamentoERetornarDTO(Long encaminhamentoId, AtualizarEncaminhamentoRequestDTO dto, AdministradorEntity adminLogado) {
        EncaminhamentoRegistroEntity encaminhamentoExistente = encaminhamentoRepository.findById(encaminhamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Encaminhamento não encontrado com ID: " + encaminhamentoId));

        MedicoEntity medicoSolicitante;
        if (dto.getMedicoSolicitanteId() != null) {
            medicoSolicitante = medicoRepository.findById(dto.getMedicoSolicitanteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Médico solicitante não encontrado: ID " + dto.getMedicoSolicitanteId()));
            if (medicoSolicitante.getDeletedAt() != null) {
                throw new IllegalArgumentException("Médico solicitante (" + medicoSolicitante.getNomeCompleto() + ") não está ativo.");
            }
        } else {
            throw new IllegalArgumentException("ID do médico solicitante é obrigatório para atualizar encaminhamentos.");
        }

        encaminhamentoMapper.updateEntityFromDTO(dto, encaminhamentoExistente, medicoSolicitante);

        EncaminhamentoRegistroEntity encaminhamentoAtualizado = encaminhamentoRepository.save(encaminhamentoExistente);
        atualizarDataProntuario(encaminhamentoAtualizado.getProntuario());
        return encaminhamentoMapper.toDTO(encaminhamentoAtualizado);
    }

    @Transactional
    public ProntuarioDTO atualizarDadosBasicosProntuarioERetornarDTO(Long prontuarioId, Long medicoResponsavelIdNovo) {
        ProntuarioEntity prontuario = prontuarioRepository.findById(prontuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado com ID: " + prontuarioId));

        if (medicoResponsavelIdNovo == null) {
            throw new IllegalArgumentException("Médico responsável principal do prontuário não pode ser nulo.");
        }

        MedicoEntity medicoNovo = medicoRepository.findById(medicoResponsavelIdNovo)
                .orElseThrow(() -> new ResourceNotFoundException("Novo médico responsável não encontrado com ID: " + medicoResponsavelIdNovo));
        if (medicoNovo.getDeletedAt() != null) {
            throw new IllegalArgumentException("Novo médico responsável selecionado ("+ medicoNovo.getNomeCompleto() +") não está ativo.");
        }

        boolean modificado = false;
        if (prontuario.getMedicoResponsavel() == null || !medicoResponsavelIdNovo.equals(prontuario.getMedicoResponsavel().getId())) {
            prontuario.setMedicoResponsavel(medicoNovo);
            modificado = true;
        }

        if (modificado) {
            prontuarioRepository.save(prontuario);
        }
        return buscarProntuarioPorIdDetalhado(prontuario.getId());
    }
}