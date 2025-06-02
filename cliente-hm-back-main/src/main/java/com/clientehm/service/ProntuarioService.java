// sf-silvaneto/clientehm/ClienteHM-057824fed8786ee29c7b4f9a2010aca3a83abc37/cliente-hm-back-main/src/main/java/com/clientehm/service/ProntuarioService.java
package com.clientehm.service;

import com.clientehm.entity.ProntuarioEntity;
import com.clientehm.entity.PacienteEntity;
import com.clientehm.entity.MedicoEntity;
import com.clientehm.entity.StatusMedico;
import com.clientehm.entity.AdministradorEntity;
import com.clientehm.entity.EntradaMedicaRegistroEntity;

import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.model.CriarConsultaRequestDTO;
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

    @Transactional(readOnly = true)
    public Page<ProntuarioEntity> buscarTodosProntuarios(Pageable pageable, String termo, String numeroProntuarioFilter) {
        logger.info("SERVICE: Iniciando busca de prontuários. Termo: '{}', NumProntuario: '{}'", termo, numeroProntuarioFilter);

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
            query.distinct(true);
            root.fetch("paciente", JoinType.LEFT);
            root.fetch("medicoResponsavel", JoinType.LEFT);

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
        prontuario.getConsultas().size();
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
            MedicoEntity medicoResponsavel = medicoRepository.findById(medicoResponsavelId)
                    .orElseThrow(() -> new ResourceNotFoundException("Médico responsável não encontrado com ID: " + medicoResponsavelId));
            if (medicoResponsavel.getStatus() != StatusMedico.ATIVO) {
                throw new IllegalArgumentException("Médico selecionado ("+ medicoResponsavel.getNomeCompleto() +") para ser responsável pelo prontuário não está ativo.");
            }

            ProntuarioEntity novoProntuario = new ProntuarioEntity();
            novoProntuario.setPaciente(paciente);
            novoProntuario.setMedicoResponsavel(medicoResponsavel);
            novoProntuario.setAdministradorCriador(adminLogado);
            // numeroProntuario e dataInicio serão definidos no @PrePersist via ProntuarioEntity

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

        MedicoEntity medicoExecutor = medicoRepository.findById(medicoExecutorId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico executor da consulta não encontrado com ID: " + medicoExecutorId));
        if (medicoExecutor.getStatus() != StatusMedico.ATIVO) {
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

        // A dataUltimaAtualizacao do prontuário será setada automaticamente pelo @PreUpdate
        prontuarioRepository.save(prontuario); // Garante que o @PreUpdate seja chamado se necessário

        return consultaRepository.save(novaConsulta);
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
            // dataUltimaAtualizacao será setada pelo @PreUpdate ao salvar
            return prontuarioRepository.save(prontuario);
        }
        return prontuario;
    }
} // ESTA É A CHAVE FINAL DA CLASSE