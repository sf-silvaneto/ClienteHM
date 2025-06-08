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
    @Autowired private SinaisVitaisRepository sinaisVitaisRepository; // Novo repositório
    @Autowired private ProntuarioMapper prontuarioMapper;
    @Autowired private ConsultaMapper consultaMapper;
    @Autowired private ExameMapper exameMapper;
    @Autowired private ProcedimentoMapper procedimentoMapper;
    @Autowired private EncaminhamentoMapper encaminhamentoMapper;

    private void inicializarConsultaCompletamente(ConsultaRegistroEntity consulta) {
        if (consulta != null) {
            if (consulta.getProntuario() != null) consulta.getProntuario().getId();
            if (consulta.getResponsavelMedico() != null) consulta.getResponsavelMedico().getId();
            if (consulta.getResponsavelAdmin() != null) consulta.getResponsavelAdmin().getId();
            if (consulta.getSinaisVitais() != null) consulta.getSinaisVitais().getId(); // Novo: inicializar sinais vitais
        }
    }
    private void inicializarExameCompletamente(ExameRegistroEntity exame) {
        if (exame != null) {
            if (exame.getProntuario() != null) exame.getProntuario().getId();
            if (exame.getMedicoResponsavelExame() != null) exame.getMedicoResponsavelExame().getId();
        }
    }
    private void inicializarProcedimentoCompletamente(ProcedimentoRegistroEntity procedimento) {
        if (procedimento != null) {
            if (procedimento.getProntuario() != null) procedimento.getProntuario().getId();
            if (procedimento.getMedicoExecutor() != null) procedimento.getMedicoExecutor().getId();
        }
    }
    private void inicializarEncaminhamentoCompletamente(EncaminhamentoRegistroEntity encaminhamento) {
        if (encaminhamento != null) {
            if (encaminhamento.getProntuario() != null) encaminhamento.getProntuario().getId();
            if (encaminhamento.getMedicoSolicitante() != null) encaminhamento.getMedicoSolicitante().getId();
        }
    }
    private void inicializarPacienteCompleto(PacienteEntity paciente) {
        if (paciente != null) {
            if (paciente.getEndereco() != null) paciente.getEndereco().getCep();
            if (paciente.getContato() != null) paciente.getContato().getEmail();
            // Adicione a inicialização de coleções do paciente se necessário
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
            pageableParaConsulta = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "dataUltimaAtualizacao"));
        }
        Page<ProntuarioEntity> resultadoEntities = prontuarioRepository.findAll(spec, pageableParaConsulta);
        return prontuarioMapper.toBasicDTOPage(resultadoEntities);
    }

    @Transactional(readOnly = true)
    public ProntuarioDTO buscarProntuarioPorIdDetalhado(Long id) {
        ProntuarioEntity prontuario = prontuarioRepository.findByIdFetchingCollections(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado com ID: " + id));

        // Força a inicialização das coleções para evitar LazyInitializationException
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
        // Alterado: Verificação de status usando excludedAt
        if (medicoRef.getExcludedAt() != null) { // Médico está inativo se excludedAt não for null
            throw new IllegalArgumentException("Médico ("+ medicoRef.getNomeCompleto() +") de referência para o prontuário não está ativo.");
        }

        Optional<ProntuarioEntity> prontuarioExistenteOpt = prontuarioRepository.findByPacienteId(pacienteId);

        if (prontuarioExistenteOpt.isPresent()) {
            ProntuarioEntity prontuarioExistente = prontuarioExistenteOpt.get();
            boolean modificado = false;
            // Se o médico responsável estiver nulo no prontuário existente ou for diferente
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
        if (prontuario != null) {
            prontuario.setDataUltimaAtualizacao(LocalDateTime.now());
            prontuarioRepository.saveAndFlush(prontuario);
        }
    }

    @Transactional
    public ConsultaDTO adicionarConsultaERetornarDTO(Long pacienteId, CriarConsultaRequestDTO dto, AdministradorEntity adminLogado, Long medicoExecutorId) {
        MedicoEntity medicoExecutor = medicoRepository.findById(medicoExecutorId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico executor da consulta (ID: " + medicoExecutorId + ") não encontrado."));
        // Alterado: Verificação de status usando excludedAt
        if (medicoExecutor.getExcludedAt() != null) { // Médico está inativo se excludedAt não for null
            throw new IllegalArgumentException("Médico ("+ medicoExecutor.getNomeCompleto() +") não está ativo.");
        }
        ProntuarioEntity prontuario = findOrCreateProntuario(pacienteId, medicoExecutorId, adminLogado);

        ConsultaRegistroEntity novaConsulta = consultaMapper.toEntity(dto);
        novaConsulta.setProntuario(prontuario);
        novaConsulta.setResponsavelMedico(medicoExecutor);
        novaConsulta.setNomeResponsavelDisplay(medicoExecutor.getNomeCompleto());

        // Se houver dados de sinais vitais no DTO, criar a entidade SinaisVitais
        if (dto.getSinaisVitais() != null) {
            SinaisVitaisEntity sinaisVitais = new SinaisVitaisEntity();
            // O ID será automaticamente mapeado por @MapsId quando a consulta for salva
            // sinaisVitais.setId(novaConsulta.getId()); // Não precisa setar o ID aqui, @MapsId faz isso
            sinaisVitais.setPressaoArterial(dto.getSinaisVitais().getPressaoArterial());
            sinaisVitais.setTemperatura(dto.getSinaisVitais().getTemperatura());
            sinaisVitais.setFrequenciaCardiaca(dto.getSinaisVitais().getFrequenciaCardiaca());
            sinaisVitais.setSaturacao(dto.getSinaisVitais().getSaturacao());
            sinaisVitais.setHgt(dto.getSinaisVitais().getHgt()); // Adicionado HGT
            novaConsulta.setSinaisVitais(sinaisVitais);
        }

        ConsultaRegistroEntity consultaSalva = consultaRepository.save(novaConsulta);

        // Se SinaisVitais foi criado e associado, ele será salvo em cascata.
        // Precisamos garantir que a associação bidirecional está correta para que @MapsId funcione.
        if (consultaSalva.getSinaisVitais() != null) {
            consultaSalva.getSinaisVitais().setConsulta(consultaSalva); // Garante a referência de volta
        }


        atualizarDataProntuario(prontuario);
        return consultaMapper.toDTO(consultaSalva);
    }

    @Transactional
    public ExameRegistroDTO adicionarExameERetornarDTO(Long pacienteId, CriarExameRequestDTO dto, AdministradorEntity adminLogado, Long medicoResponsavelExameId) {
        MedicoEntity medicoExame = medicoRepository.findById(medicoResponsavelExameId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico responsável pelo exame (ID: " + medicoResponsavelExameId + ") não encontrado."));
        // Alterado: Verificação de status usando excludedAt
        if (medicoExame.getExcludedAt() != null) { // Médico está inativo se excludedAt não for null
            throw new IllegalArgumentException("Médico responsável pelo exame ("+ medicoExame.getNomeCompleto() +") não está ativo.");
        }
        ProntuarioEntity prontuario = findOrCreateProntuario(pacienteId, medicoResponsavelExameId, adminLogado);

        ExameRegistroEntity novoExame = exameMapper.toEntity(dto);
        novoExame.setProntuario(prontuario);
        novoExame.setMedicoResponsavelExame(medicoExame);
        novoExame.setNomeResponsavelDisplay(medicoExame.getNomeCompleto());

        ExameRegistroEntity exameSalvo = exameRepository.save(novoExame);
        atualizarDataProntuario(prontuario);
        return exameMapper.toDTO(exameSalvo);
    }

    @Transactional
    public ProcedimentoRegistroDTO adicionarProcedimentoERetornarDTO(Long pacienteId, CriarProcedimentoRequestDTO dto, AdministradorEntity adminLogado) {
        MedicoEntity medicoExecutor = medicoRepository.findById(dto.getMedicoExecutorId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico executor do procedimento (ID: " + dto.getMedicoExecutorId() + ") não encontrado."));
        // Alterado: Verificação de status usando excludedAt
        if (medicoExecutor.getExcludedAt() != null) { // Médico está inativo se excludedAt não for null
            throw new IllegalArgumentException("Médico executor ("+ medicoExecutor.getNomeCompleto() +") não está ativo.");
        }
        ProntuarioEntity prontuario = findOrCreateProntuario(pacienteId, medicoExecutor.getId(), adminLogado);

        ProcedimentoRegistroEntity novoProcedimento = procedimentoMapper.toEntity(dto);
        novoProcedimento.setProntuario(prontuario);
        novoProcedimento.setMedicoExecutor(medicoExecutor);
        novoProcedimento.setNomeResponsavelDisplay(medicoExecutor.getNomeCompleto());

        ProcedimentoRegistroEntity procedimentoSalvo = procedimentoRepository.save(novoProcedimento);
        atualizarDataProntuario(prontuario);
        return procedimentoMapper.toDTO(procedimentoSalvo);
    }

    @Transactional
    public EncaminhamentoRegistroDTO adicionarEncaminhamentoERetornarDTO(Long pacienteId, CriarEncaminhamentoRequestDTO dto, AdministradorEntity adminLogado) {
        MedicoEntity medicoSolicitante = medicoRepository.findById(dto.getMedicoSolicitanteId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico solicitante do encaminhamento (ID: " + dto.getMedicoSolicitanteId() + ") não encontrado."));
        // Alterado: Verificação de status usando excludedAt
        if (medicoSolicitante.getExcludedAt() != null) { // Médico está inativo se excludedAt não for null
            throw new IllegalArgumentException("Médico solicitante ("+ medicoSolicitante.getNomeCompleto() +") não está ativo.");
        }
        ProntuarioEntity prontuario = findOrCreateProntuario(pacienteId, medicoSolicitante.getId(), adminLogado);

        EncaminhamentoRegistroEntity novoEncaminhamento = encaminhamentoMapper.toEntity(dto);
        novoEncaminhamento.setProntuario(prontuario);
        novoEncaminhamento.setMedicoSolicitante(medicoSolicitante);
        novoEncaminhamento.setNomeResponsavelDisplay(medicoSolicitante.getNomeCompleto());

        EncaminhamentoRegistroEntity encaminhamentoSalvo = encaminhamentoRepository.save(novoEncaminhamento);
        atualizarDataProntuario(prontuario);
        return encaminhamentoMapper.toDTO(encaminhamentoSalvo);
    }

    @Transactional
    public ConsultaDTO atualizarConsultaERetornarDTO(Long consultaId, AtualizarConsultaRequestDTO dto, AdministradorEntity adminLogado) {
        ConsultaRegistroEntity consultaExistente = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com ID: " + consultaId));

        MedicoEntity medicoExecutor = null;
        if (dto.getMedicoExecutorId() != null) {
            medicoExecutor = medicoRepository.findById(dto.getMedicoExecutorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Médico executor não encontrado: ID " + dto.getMedicoExecutorId()));
            // Alterado: Verificação de status usando excludedAt
            if (medicoExecutor.getExcludedAt() != null) { // Médico está inativo se excludedAt não for null
                throw new IllegalArgumentException("Médico executor (" + medicoExecutor.getNomeCompleto() + ") não está ativo.");
            }
        }
        consultaMapper.updateEntityFromDTO(dto, consultaExistente, medicoExecutor, adminLogado);

        // Se sinais vitais foram enviados no DTO, salve-os.
        if (dto.getSinaisVitais() != null) {
            SinaisVitaisEntity sinaisVitais = consultaExistente.getSinaisVitais();
            if (sinaisVitais == null) {
                sinaisVitais = new SinaisVitaisEntity();
                sinaisVitais.setId(consultaExistente.getId()); // Define o ID para corresponder ao da consulta
                sinaisVitais.setConsulta(consultaExistente); // Define a referência bidirecional
            }
            sinaisVitais.setPressaoArterial(dto.getSinaisVitais().getPressaoArterial());
            sinaisVitais.setTemperatura(dto.getSinaisVitais().getTemperatura());
            sinaisVitais.setFrequenciaCardiaca(dto.getSinaisVitais().getFrequenciaCardiaca());
            sinaisVitais.setSaturacao(dto.getSinaisVitais().getSaturacao());
            sinaisVitais.setHgt(dto.getSinaisVitais().getHgt()); // Adicionado HGT
            sinaisVitaisRepository.save(sinaisVitais);
            consultaExistente.setSinaisVitais(sinaisVitais); // Garante que a entidade de consulta tenha a referência atualizada
        } else if (consultaExistente.getSinaisVitais() != null) {
            // Se o DTO não forneceu sinais vitais, mas a entidade possui, e o usuário quer remover,
            // poderíamos remover a entidade de sinais vitais. No entanto, se o DTO não vem com null
            // e apenas não tem o objeto, a estratégia é manter o existente, se não quiser, apague.
            // Para remover sinais vitais se o DTO enviar explicitamente null para o objeto 'sinaisVitais':
            // if (dto.getSinaisVitais() == null && consultaExistente.getSinaisVitais() != null) {
            //     sinaisVitaisRepository.delete(consultaExistente.getSinaisVitais());
            //     consultaExistente.setSinaisVitais(null);
            // }
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
            // Alterado: Verificação de status usando excludedAt
            if (medicoResponsavelExame.getExcludedAt() != null) { // Médico está inativo se excludedAt não for null
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
            // Alterado: Verificação de status usando excludedAt
            if (medicoExecutor.getExcludedAt() != null) { // Médico está inativo se excludedAt não for null
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
            // Alterado: Verificação de status usando excludedAt
            if (medicoSolicitante.getExcludedAt() != null) { // Médico está inativo se excludedAt não for null
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
        // Alterado: Verificação de status usando excludedAt
        if (medicoNovo.getExcludedAt() != null) { // Médico está inativo se excludedAt não for null
            throw new IllegalArgumentException("Novo médico responsável selecionado ("+ medicoNovo.getNomeCompleto() +") não está ativo.");
        }

        boolean modificado = false;
        if (prontuario.getMedicoResponsavel() == null || !medicoResponsavelIdNovo.equals(prontuario.getMedicoResponsavel().getId())) {
            prontuario.setMedicoResponsavel(medicoNovo);
            modificado = true;
        }

        if (modificado) {
            prontuario.setDataUltimaAtualizacao(LocalDateTime.now());
            prontuarioRepository.save(prontuario);
        }
        return buscarProntuarioPorIdDetalhado(prontuario.getId());
    }
}