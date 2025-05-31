package com.clientehm.service;

import com.clientehm.entity.*; // Import all entities from the package
import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.model.CriarEntradaMedicaRequestDTO; // Importar novo DTO
import com.clientehm.model.NovoProntuarioRequestDTO;
import com.clientehm.repository.*; // Import all repositories
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ProntuarioService {

    private static final Logger logger = LoggerFactory.getLogger(ProntuarioService.class);

    @Autowired private ProntuarioRepository prontuarioRepository;
    @Autowired private PacienteRepository pacienteRepository;
    @Autowired private MedicoRepository medicoRepository;
    @Autowired private AdministradorRepository administradorRepository;
    @Autowired private EntradaMedicaRegistroRepository entradaMedicaRegistroRepository; // NOVO REPOSITÓRIO

    // ... (métodos existentes buscarTodos, buscarPorId, criarProntuario) ...
    @Transactional(readOnly = true)
    public Page<ProntuarioEntity> buscarTodos(Pageable pageable) {
        return prontuarioRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public ProntuarioEntity buscarPorId(Long id) {
        // Para popular a timeline, você pode precisar carregar as coleções aqui
        // Ex: usando um fetch join na query do repositório ou Hibernate.initialize()
        ProntuarioEntity prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado com ID: " + id));
        // Exemplo para carregar explicitamente se for LAZY e necessário:
        // Hibernate.initialize(prontuario.getEntradasMedicas());
        // Hibernate.initialize(prontuario.getHistoricoMedico());
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
        try {
            prontuario.setTipoTratamento(ProntuarioEntity.TipoTratamento.valueOf(novoProntuarioDTO.getTipoTratamento().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de tratamento inválido: " + novoProntuarioDTO.getTipoTratamento());
        }
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


    // NOVO MÉTODO
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
            novaEntrada.setAlergiasDetalhe(null); // Garante que detalhes sejam nulos
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

        // Definir o responsável pelo registro
        // Aqui, assumimos que o admin logado é o responsável. Poderia ser um médico.
        novaEntrada.setResponsavelAdmin(adminLogado);
        novaEntrada.setNomeResponsavelDisplay(adminLogado.getNome());
        // Se um médico também puder registrar, você precisaria de lógica para determinar qual é o responsável
        // e possivelmente um campo no DTO para indicar quem é o profissional registrando.

        return entradaMedicaRegistroRepository.save(novaEntrada);
    }
}