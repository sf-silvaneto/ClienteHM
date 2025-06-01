package com.clientehm.service;

import com.clientehm.entity.*;
import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.model.CriarEntradaMedicaRequestDTO;
import com.clientehm.model.NovoProntuarioRequestDTO;
import com.clientehm.repository.*;
import jakarta.persistence.criteria.Join; // Adicionado
import jakarta.persistence.criteria.JoinType; // Adicionado
import jakarta.persistence.criteria.Predicate; // Adicionado
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest; // Adicionado
import org.springframework.data.domain.Sort; // Adicionado
import org.springframework.data.jpa.domain.Specification; // Adicionado
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // Adicionado

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList; // Adicionado
import java.util.List; // Adicionado
import java.util.UUID;

@Service
public class ProntuarioService {

    private static final Logger logger = LoggerFactory.getLogger(ProntuarioService.class);

    @Autowired private ProntuarioRepository prontuarioRepository;
    @Autowired private PacienteRepository pacienteRepository;
    @Autowired private MedicoRepository medicoRepository;
    @Autowired private AdministradorRepository administradorRepository;
    @Autowired private EntradaMedicaRegistroRepository entradaMedicaRegistroRepository;

    @Transactional(readOnly = true)
    public Page<ProntuarioEntity> buscarTodos(Pageable pageable, String termo, String numeroProntuarioFilter, ProntuarioEntity.StatusProntuario statusFilter) {
        logger.info("SERVICE: Iniciando busca de prontuários. Termo: '{}', NumProntuario: '{}', Status: '{}'", termo, numeroProntuarioFilter, statusFilter);

        Specification<ProntuarioEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Garante que o JOIN com paciente só é adicionado uma vez e é acessível
            Join<ProntuarioEntity, PacienteEntity> pacienteJoin = root.join("paciente", JoinType.LEFT);

            if (StringUtils.hasText(termo)) {
                String termoLike = "%" + termo.toLowerCase() + "%";
                Predicate porNumeroProntuario = cb.like(cb.lower(root.get("numeroProntuario")), termoLike);
                Predicate porNomePaciente = cb.like(cb.lower(pacienteJoin.get("nome")), termoLike);
                Predicate porCpfPaciente = cb.like(pacienteJoin.get("cpf"), termoLike); // CPF geralmente não é case-sensitive e pode ser busca exata

                predicates.add(cb.or(porNumeroProntuario, porNomePaciente, porCpfPaciente));
            }

            if (StringUtils.hasText(numeroProntuarioFilter)) {
                String numeroProntuarioLike = "%" + numeroProntuarioFilter.toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("numeroProntuario")), numeroProntuarioLike));
            }

            if (statusFilter != null) {
                predicates.add(cb.equal(root.get("status"), statusFilter));
            }

            // Evita N+1 para o paciente, mas pode precisar de outros para medicoResponsavel, administradorCriador se forem frequentemente acessados na listagem
            // query.distinct(true) pode ser necessário se os joins duplicarem resultados
            // root.fetch("paciente", JoinType.LEFT); // Exemplo de fetch join

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // Adicionar ordenação padrão se não vier do pageable ou se necessário
        Pageable pageableComSort = pageable;
        if (pageable.getSort().isUnsorted()) {
            pageableComSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("dataUltimaAtualizacao").descending());
        }

        logger.info("SERVICE: Executando busca no repositório com Pageable: {}", pageableComSort);
        Page<ProntuarioEntity> resultado = prontuarioRepository.findAll(spec, pageableComSort);
        logger.info("SERVICE: Busca concluída. Encontrados {} prontuários na página {} de {}.", resultado.getNumberOfElements(), resultado.getNumber(), resultado.getTotalPages());
        return resultado;
    }

    @Transactional(readOnly = true)
    public ProntuarioEntity buscarPorId(Long id) {
        ProntuarioEntity prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado com ID: " + id));
        // Forçar a inicialização das coleções LAZY se necessário antes de retornar (se convertToDTO precisar delas e a sessão fechar)
        // Exemplo: prontuario.getHistoricoMedico().size();
        // prontuario.getEntradasMedicas().size();
        return prontuario;
    }

    @Transactional
    public ProntuarioEntity criarProntuario(NovoProntuarioRequestDTO novoProntuarioDTO, String emailAdminLogado) {
        logger.info("Criando novo prontuário para o paciente ID: {} pelo admin: {}", novoProntuarioDTO.getPacienteId(), emailAdminLogado);

        PacienteEntity paciente = pacienteRepository.findById(novoProntuarioDTO.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com ID: " + novoProntuarioDTO.getPacienteId()));

        MedicoEntity medicoResponsavel = medicoRepository.findById(novoProntuarioDTO.getMedicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado com ID: " + novoProntuarioDTO.getMedicoId()));

        if (medicoResponsavel.getStatus() != StatusMedico.ATIVO) {
            throw new IllegalArgumentException("Médico selecionado ("+ medicoResponsavel.getNomeCompleto() +") não está ativo.");
        }

        AdministradorEntity adminCriador = administradorRepository.findByEmail(emailAdminLogado)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador não encontrado com email: " + emailAdminLogado));

        ProntuarioEntity prontuario = new ProntuarioEntity();
        prontuario.setPaciente(paciente);
        prontuario.setMedicoResponsavel(medicoResponsavel);
        prontuario.setAdministradorCriador(adminCriador);
        prontuario.setNumeroProntuario(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // Linha de atribuição do tipoTratamento REMOVIDA
        // try {
        //     prontuario.setTipoTratamento(ProntuarioEntity.TipoTratamento.valueOf(novoProntuarioDTO.getTipoTratamento().toUpperCase()));
        // } catch (IllegalArgumentException e) {
        //     throw new IllegalArgumentException("Tipo de tratamento inválido: " + novoProntuarioDTO.getTipoTratamento());
        // }

        prontuario.setDataInicio(LocalDate.now());
        prontuario.setStatus(ProntuarioEntity.StatusProntuario.ATIVO);

        HistoricoMedicoEntity historicoInicial = new HistoricoMedicoEntity();
        historicoInicial.setDescricao(novoProntuarioDTO.getHistoricoMedico().getDescricao());
        historicoInicial.setResponsavel(adminCriador.getNome());
        historicoInicial.setData(LocalDateTime.now());
        historicoInicial.setProntuario(prontuario);
        prontuario.getHistoricoMedico().add(historicoInicial);

        ProntuarioEntity prontuarioSalvo = prontuarioRepository.save(prontuario);
        logger.info("Prontuário {} criado com sucesso para o paciente {} com médico responsável {}",
                prontuarioSalvo.getNumeroProntuario(), paciente.getNome(), medicoResponsavel.getNomeCompleto());
        return prontuarioSalvo;
    }

    @Transactional
    public EntradaMedicaRegistroEntity adicionarEntradaMedica(Long prontuarioId, CriarEntradaMedicaRequestDTO dto, AdministradorEntity adminLogado) {
        logger.info("Adicionando entrada médica ao prontuário ID: {} pelo admin: {}", prontuarioId, adminLogado.getEmail());
        ProntuarioEntity prontuario = prontuarioRepository.findById(prontuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado com ID: " + prontuarioId));

        EntradaMedicaRegistroEntity novaEntrada = new EntradaMedicaRegistroEntity();
        novaEntrada.setProntuario(prontuario);
        novaEntrada.setDataHoraEntrada(dto.getDataHoraEntrada());
        novaEntrada.setMotivoEntrada(dto.getMotivoEntrada());
        novaEntrada.setQueixasPrincipais(dto.getQueixasPrincipais());
        novaEntrada.setPressaoArterial(dto.getPressaoArterial());
        novaEntrada.setTemperatura(dto.getTemperatura());
        novaEntrada.setFrequenciaCardiaca(dto.getFrequenciaCardiaca());
        novaEntrada.setSaturacao(dto.getSaturacao());

        if (dto.getSemAlergiasConhecidas() != null && dto.getSemAlergiasConhecidas()) {
            novaEntrada.setSemAlergiasConhecidas(true);
            novaEntrada.setAlergiasDetalhe(null);
        } else {
            novaEntrada.setSemAlergiasConhecidas(false);
            novaEntrada.setAlergiasDetalhe(dto.getAlergiasDetalhe());
        }

        novaEntrada.setTemComorbidades("sim".equalsIgnoreCase(dto.getTemComorbidades()));
        if (novaEntrada.getTemComorbidades()) {
            novaEntrada.setComorbidadesDetalhes(dto.getComorbidadesDetalhes());
        } else {
            novaEntrada.setComorbidadesDetalhes(null);
        }

        novaEntrada.setUsaMedicamentosContinuos("sim".equalsIgnoreCase(dto.getUsaMedicamentosContinuos()));
        if (novaEntrada.getUsaMedicamentosContinuos()) {
            novaEntrada.setMedicamentosContinuosDetalhes(dto.getMedicamentosContinuosDetalhes());
        } else {
            novaEntrada.setMedicamentosContinuosDetalhes(null);
        }

        novaEntrada.setHistoricoFamiliarRelevante(dto.getHistoricoFamiliarRelevante());

        novaEntrada.setResponsavelAdmin(adminLogado);
        novaEntrada.setNomeResponsavelDisplay(adminLogado.getNome());

        // Atualiza a data de última atualização do prontuário pai
        prontuario.setDataUltimaAtualizacao(LocalDateTime.now());
        prontuarioRepository.save(prontuario); // Salva a alteração no prontuário

        return entradaMedicaRegistroRepository.save(novaEntrada);
    }
}