package com.clientehm.service;

// Imports explícitos para as entidades usadas
import com.clientehm.entity.ProntuarioEntity;
import com.clientehm.entity.PacienteEntity;
import com.clientehm.entity.MedicoEntity; // Import explícito
import com.clientehm.entity.StatusMedico; // Import explícito
import com.clientehm.entity.AdministradorEntity;
import com.clientehm.entity.EntradaMedicaRegistroEntity;
import com.clientehm.entity.InternacaoEntity;

import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.model.CriarConsultaRequestDTO;
import com.clientehm.model.InternacaoRequestDTO;
import com.clientehm.model.RegistrarAltaInternacaoDTO;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProntuarioService {

    private static final Logger logger = LoggerFactory.getLogger(ProntuarioService.class);

    @Autowired private ProntuarioRepository prontuarioRepository;
    @Autowired private PacienteRepository pacienteRepository;
    @Autowired private MedicoRepository medicoRepository;
    @Autowired private AdministradorRepository administradorRepository;
    @Autowired private EntradaMedicaRegistroRepository consultaRepository;
    @Autowired private InternacaoRepository internacaoRepository;

    @Transactional(readOnly = true)
    public Page<ProntuarioEntity> buscarTodosProntuarios(Pageable pageable, String termo, String numeroProntuarioFilter, String statusFilterString) {
        logger.info("SERVICE: Iniciando busca de prontuários. Termo: '{}', NumProntuario: '{}', Status: '{}'", termo, numeroProntuarioFilter, statusFilterString);

        ProntuarioEntity.StatusProntuario statusEnum = null;
        if (StringUtils.hasText(statusFilterString)) {
            try {
                statusEnum = ProntuarioEntity.StatusProntuario.valueOf(statusFilterString.toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.warn("Status de prontuário inválido fornecido para busca: {}", statusFilterString);
            }
        }
        final ProntuarioEntity.StatusProntuario finalStatusEnum = statusEnum;

        Specification<ProntuarioEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<ProntuarioEntity, PacienteEntity> pacienteJoin = root.join("paciente", JoinType.LEFT);

            if (StringUtils.hasText(termo)) {
                String termoLike = "%" + termo.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("numeroProntuario")), termoLike),
                        cb.like(cb.lower(pacienteJoin.get("nome")), termoLike),
                        cb.like(pacienteJoin.get("cpf"), termoLike)
                ));
            }
            if (StringUtils.hasText(numeroProntuarioFilter)) {
                predicates.add(cb.like(cb.lower(root.get("numeroProntuario")), "%" + numeroProntuarioFilter.toLowerCase() + "%"));
            }
            if (finalStatusEnum != null) {
                predicates.add(cb.equal(root.get("status"), finalStatusEnum));
            }
            query.distinct(true); // Para evitar duplicatas se houver múltiplos joins que podem causar isso
            root.fetch("paciente", JoinType.LEFT); // Eager fetch para evitar N+1
            root.fetch("medicoResponsavel", JoinType.LEFT); // Eager fetch

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageableComSort = pageable;
        if (pageable.getSort().isUnsorted()) {
            pageableComSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("dataUltimaAtualizacao").descending());
        }

        Page<ProntuarioEntity> resultado = prontuarioRepository.findAll(spec, pageableComSort);
        logger.info("SERVICE: Busca concluída. Encontrados {} prontuários na página {} de {}.", resultado.getNumberOfElements(), resultado.getNumber() + 1, resultado.getTotalPages());
        return resultado;
    }

    @Transactional(readOnly = true)
    public ProntuarioEntity buscarProntuarioPorId(Long id) {
        ProntuarioEntity prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado com ID: " + id));
        // Forçar inicialização de coleções LAZY
        prontuario.getConsultas().size();
        prontuario.getInternacoes().size();
        prontuario.getHistoricoGeral().size();
        return prontuario;
    }

    private ProntuarioEntity findOrCreateProntuario(Long pacienteId, Long medicoResponsavelId, AdministradorEntity adminLogado) {
        logger.debug("Procurando ou criando prontuário para paciente ID: {} com médico responsável ID: {}", pacienteId, medicoResponsavelId);

        PacienteEntity paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com ID: " + pacienteId));

        Optional<ProntuarioEntity> prontuarioExistenteOpt = prontuarioRepository.findByPacienteId(pacienteId);

        if (prontuarioExistenteOpt.isPresent()) {
            ProntuarioEntity prontuarioExistente = prontuarioExistenteOpt.get();
            logger.info("Prontuário existente ID: {} encontrado para o paciente ID: {}", prontuarioExistente.getId(), pacienteId);
            return prontuarioExistente;
        } else {
            logger.info("Nenhum prontuário existente para o paciente ID: {}. Criando um novo.", pacienteId);
            MedicoEntity medicoResponsavel = medicoRepository.findById(medicoResponsavelId) // Usa MedicoEntity
                    .orElseThrow(() -> new ResourceNotFoundException("Médico responsável não encontrado com ID: " + medicoResponsavelId));
            if (medicoResponsavel.getStatus() != StatusMedico.ATIVO) { // Usa StatusMedico
                throw new IllegalArgumentException("Médico selecionado ("+ medicoResponsavel.getNomeCompleto() +") para ser responsável pelo prontuário não está ativo.");
            }

            ProntuarioEntity novoProntuario = new ProntuarioEntity();
            novoProntuario.setPaciente(paciente);
            novoProntuario.setMedicoResponsavel(medicoResponsavel);
            novoProntuario.setAdministradorCriador(adminLogado);
            novoProntuario.setNumeroProntuario(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            novoProntuario.setDataInicio(LocalDate.now());
            novoProntuario.setStatus(ProntuarioEntity.StatusProntuario.EM_ELABORACAO);

            return prontuarioRepository.save(novoProntuario);
        }
    }

    @Transactional
    public EntradaMedicaRegistroEntity adicionarConsulta(
            Long pacienteIdParaProntuario,
            CriarConsultaRequestDTO dto,
            AdministradorEntity adminLogado,
            Long medicoExecutorId,
            boolean criarProntuarioSeNaoExistir) {

        MedicoEntity medicoExecutor = medicoRepository.findById(medicoExecutorId) // Usa MedicoEntity
                .orElseThrow(() -> new ResourceNotFoundException("Médico executor da consulta não encontrado com ID: " + medicoExecutorId));
        if (medicoExecutor.getStatus() != StatusMedico.ATIVO) { // Usa StatusMedico
            throw new IllegalArgumentException("Médico executor da consulta ("+ medicoExecutor.getNomeCompleto() +") não está ativo.");
        }

        ProntuarioEntity prontuario;
        if (criarProntuarioSeNaoExistir) {
            prontuario = findOrCreateProntuario(pacienteIdParaProntuario, medicoExecutorId, adminLogado);
        } else {
            prontuario = prontuarioRepository.findById(pacienteIdParaProntuario)
                    .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado com ID: " + pacienteIdParaProntuario));
        }

        logger.info("Adicionando consulta ao prontuário ID: {} pelo admin: {}", prontuario.getId(), adminLogado.getEmail());

        EntradaMedicaRegistroEntity novaConsulta = new EntradaMedicaRegistroEntity();
        novaConsulta.setProntuario(prontuario);
        novaConsulta.setDataHoraConsulta(dto.getDataHoraConsulta());
        novaConsulta.setMotivoConsulta(dto.getMotivoConsulta());
        novaConsulta.setQueixasPrincipais(dto.getQueixasPrincipais());
        novaConsulta.setPressaoArterial(dto.getPressaoArterial());
        novaConsulta.setTemperatura(dto.getTemperatura());
        novaConsulta.setFrequenciaCardiaca(dto.getFrequenciaCardiaca());
        novaConsulta.setSaturacao(dto.getSaturacao());

        novaConsulta.setExameFisico(dto.getExameFisico());
        novaConsulta.setHipoteseDiagnostica(dto.getHipoteseDiagnostica());
        novaConsulta.setCondutaPlanoTerapeutico(dto.getCondutaPlanoTerapeutico());
        novaConsulta.setDetalhesConsulta(dto.getDetalhesConsulta());
        novaConsulta.setObservacoesConsulta(dto.getObservacoesConsulta());

        novaConsulta.setResponsavelMedico(medicoExecutor);
        novaConsulta.setNomeResponsavelDisplay(medicoExecutor.getNomeCompleto());

        if (prontuario.getStatus() != ProntuarioEntity.StatusProntuario.INTERNADO) {
            prontuario.setStatus(ProntuarioEntity.StatusProntuario.ARQUIVADO);
        }
        prontuario.setDataUltimaAtualizacao(LocalDateTime.now());
        prontuarioRepository.save(prontuario);

        return consultaRepository.save(novaConsulta);
    }

    @Transactional
    public InternacaoEntity adicionarInternacao(Long pacienteIdParaProntuario, InternacaoRequestDTO dto, AdministradorEntity adminLogado, boolean criarProntuarioSeNaoExistir) {
        ProntuarioEntity prontuario;
        MedicoEntity medicoAdmissao = medicoRepository.findById(dto.getMedicoResponsavelAdmissaoId()) // Usa MedicoEntity
                .orElseThrow(() -> new ResourceNotFoundException("Médico de admissão não encontrado com ID: " + dto.getMedicoResponsavelAdmissaoId()));
        if (medicoAdmissao.getStatus() != StatusMedico.ATIVO) { // Usa StatusMedico
            throw new IllegalArgumentException("Médico de admissão ("+ medicoAdmissao.getNomeCompleto() +") não está ativo.");
        }

        if (criarProntuarioSeNaoExistir) {
            prontuario = findOrCreateProntuario(pacienteIdParaProntuario, dto.getMedicoResponsavelAdmissaoId(), adminLogado);
        } else {
            prontuario = prontuarioRepository.findById(pacienteIdParaProntuario)
                    .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado com ID: " + pacienteIdParaProntuario));
        }

        logger.info("Adicionando internação ao prontuário ID: {} pelo admin: {}", prontuario.getId(), adminLogado.getEmail());

        InternacaoEntity novaInternacao = new InternacaoEntity();
        novaInternacao.setProntuario(prontuario);
        novaInternacao.setDataAdmissao(dto.getDataAdmissao());
        novaInternacao.setMotivoInternacao(dto.getMotivoInternacao());
        novaInternacao.setHistoriaDoencaAtual(dto.getHistoriaDoencaAtual());
        novaInternacao.setDataAltaPrevista(dto.getDataAltaPrevista());

        novaInternacao.setResponsavelAdmissaoMedico(medicoAdmissao); // Usa MedicoEntity
        novaInternacao.setNomeResponsavelAdmissaoDisplay(medicoAdmissao.getNomeCompleto());

        prontuario.setStatus(ProntuarioEntity.StatusProntuario.INTERNADO);
        prontuario.setDataUltimaAtualizacao(LocalDateTime.now());
        prontuarioRepository.save(prontuario);

        return internacaoRepository.save(novaInternacao);
    }

    @Transactional
    public InternacaoEntity registrarAltaInternacao(Long internacaoId, RegistrarAltaInternacaoDTO dto, AdministradorEntity adminLogado) {
        logger.info("Registrando alta para internação ID: {} pelo admin: {}", internacaoId, adminLogado != null ? adminLogado.getEmail() : "SISTEMA");
        InternacaoEntity internacao = internacaoRepository.findById(internacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Internação não encontrada com ID: " + internacaoId));

        MedicoEntity medicoAlta = medicoRepository.findById(dto.getMedicoResponsavelAltaId()) // Usa MedicoEntity
                .orElseThrow(() -> new ResourceNotFoundException("Médico responsável pela alta não encontrado com ID: " + dto.getMedicoResponsavelAltaId()));
        if (medicoAlta.getStatus() != StatusMedico.ATIVO) { // Usa StatusMedico
            throw new IllegalArgumentException("Médico da alta ("+ medicoAlta.getNomeCompleto() +") não está ativo.");
        }

        internacao.setDataAltaEfetiva(dto.getDataAltaEfetiva());
        internacao.setResumoAlta(dto.getResumoAlta());
        internacao.setMedicoResponsavelAlta(medicoAlta); // Usa MedicoEntity

        ProntuarioEntity prontuario = internacao.getProntuario();
        prontuario.setStatus(ProntuarioEntity.StatusProntuario.ARQUIVADO);
        prontuario.setDataAltaAdministrativa(dto.getDataAltaEfetiva().toLocalDate());
        prontuario.setDataUltimaAtualizacao(LocalDateTime.now());
        prontuarioRepository.save(prontuario);

        return internacaoRepository.save(internacao);
    }

    // Este é o método provavelmente na linha 255 ou próxima a ela
    @Transactional
    public ProntuarioEntity atualizarDadosBasicosProntuario(Long prontuarioId, Long medicoResponsavelIdNovo, ProntuarioEntity.StatusProntuario novoStatus, LocalDate novaDataAltaAdministrativa) {
        ProntuarioEntity prontuario = prontuarioRepository.findById(prontuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado com ID: " + prontuarioId));

        boolean modificado = false;
        if (medicoResponsavelIdNovo != null &&
                (prontuario.getMedicoResponsavel() == null || !medicoResponsavelIdNovo.equals(prontuario.getMedicoResponsavel().getId()))) {

            // A linha abaixo usa MedicoEntity. O import explícito ou o wildcard deve resolver.
            MedicoEntity medicoNovo = medicoRepository.findById(medicoResponsavelIdNovo)
                    .orElseThrow(() -> new ResourceNotFoundException("Médico responsável não encontrado com ID: " + medicoResponsavelIdNovo));

            // A linha abaixo usa StatusMedico. O import explícito ou o wildcard deve resolver.
            if (medicoNovo.getStatus() != StatusMedico.ATIVO) {
                throw new IllegalArgumentException("Novo médico responsável selecionado ("+ medicoNovo.getNomeCompleto() +") não está ativo.");
            }
            prontuario.setMedicoResponsavel(medicoNovo);
            modificado = true;
        }

        if (novoStatus != null && novoStatus != prontuario.getStatus()) {
            if (novoStatus == ProntuarioEntity.StatusProntuario.INTERNADO &&
                    !prontuario.getInternacoes().stream().anyMatch(i -> i.getDataAltaEfetiva() == null)) {
                throw new IllegalArgumentException("Não é possível definir o status para INTERNADO manualmente sem uma internação ativa.");
            }
            prontuario.setStatus(novoStatus);
            modificado = true;
        }

        if (novaDataAltaAdministrativa != null &&
                (prontuario.getDataAltaAdministrativa() == null || !novaDataAltaAdministrativa.isEqual(prontuario.getDataAltaAdministrativa()))) {
            prontuario.setDataAltaAdministrativa(novaDataAltaAdministrativa);
            if(prontuario.getStatus() != ProntuarioEntity.StatusProntuario.INTERNADO) {
                prontuario.setStatus(ProntuarioEntity.StatusProntuario.ARQUIVADO);
            }
            modificado = true;
        } else if (novaDataAltaAdministrativa == null && prontuario.getDataAltaAdministrativa() != null) {
            prontuario.setDataAltaAdministrativa(null);
            modificado = true;
        }

        if (modificado) {
            prontuario.setDataUltimaAtualizacao(LocalDateTime.now());
            return prontuarioRepository.save(prontuario);
        }
        return prontuario;
    }
}