package com.main.domain.service;

import com.main.domain.entity.PacienteEntity;
import com.main.domain.entity.ContatoEntity;
import com.main.api.exception.ResourceNotFoundException;
import com.main.api.exception.CpfAlreadyExistsException;
import com.main.api.exception.EmailAlreadyExistsException;
import com.main.mapper.PacienteMapper;
import com.main.api.model.PacienteCreateDTO;
import com.main.api.model.PacienteDTO;
import com.main.api.model.PacienteUpdateDTO;
import com.main.domain.repository.PacienteRepository;
import com.main.domain.repository.ContatoRepository;
import com.main.domain.repository.AlergiaRepository;
import com.main.domain.repository.ComorbidadeRepository;
import com.main.domain.repository.MedicamentoContinuoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.Optional;

@Service
public class PacienteService {

    private static final Logger logger = LoggerFactory.getLogger(PacienteService.class);

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ContatoRepository contatoRepository;

    @Autowired
    private AlergiaRepository alergiaRepository;
    @Autowired
    private ComorbidadeRepository comorbidadeRepository;
    @Autowired
    private MedicamentoContinuoRepository medicamentoContinuoRepository;

    @Autowired
    private PacienteMapper pacienteMapper;

    @Transactional
    public PacienteDTO criarPaciente(PacienteCreateDTO pacienteCreateDTO) {
        logger.info("SERVICE: Tentando criar paciente com CPF: {}", pacienteCreateDTO.getCpf());
        pacienteRepository.findByCpf(pacienteCreateDTO.getCpf()).ifPresent(p -> {
            throw new CpfAlreadyExistsException("CPF " + pacienteCreateDTO.getCpf() + " já cadastrado.");
        });

        if (StringUtils.hasText(pacienteCreateDTO.getEmail())) {
            contatoRepository.findByEmail(pacienteCreateDTO.getEmail().trim().toLowerCase()).ifPresent(c -> {
                throw new EmailAlreadyExistsException("Email " + pacienteCreateDTO.getEmail() + " já cadastrado.");
            });
        }

        PacienteEntity pacienteEntity = pacienteMapper.toEntity(pacienteCreateDTO);

        if (pacienteEntity.getAlergias() != null) {
            pacienteEntity.getAlergias().forEach(alergia -> alergia.setPaciente(pacienteEntity));
        }
        if (pacienteEntity.getComorbidades() != null) {
            pacienteEntity.getComorbidades().forEach(comorbidade -> comorbidade.setPaciente(pacienteEntity));
        }
        if (pacienteEntity.getMedicamentosContinuos() != null) {
            pacienteEntity.getMedicamentosContinuos().forEach(medicamento -> medicamento.setPaciente(pacienteEntity));
        }

        PacienteEntity pacienteSalvo = pacienteRepository.save(pacienteEntity);
        logger.info("SERVICE: Paciente criado com ID: {}", pacienteSalvo.getId());
        return pacienteMapper.toDTO(pacienteSalvo);
    }

    @Transactional(readOnly = true)
    public Page<PacienteDTO> buscarTodosPacientes(Pageable pageable, String nome, String cpf) {
        logger.info("SERVICE: Buscando pacientes. Filtros: nome='{}', cpf='{}'", nome, cpf);
        Page<PacienteEntity> pacientesPage;
        if (StringUtils.hasText(cpf)) {
            pacientesPage = pacienteRepository.findByCpfStartingWith(cpf, pageable);
        } else if (StringUtils.hasText(nome)) {
            pacientesPage = pacienteRepository.findByNomeContainingIgnoreCase(nome, pageable);
        } else {
            pacientesPage = pacienteRepository.findAll(pageable);
        }
        return pacienteMapper.toDTOPage(pacientesPage);
    }

    @Transactional(readOnly = true)
    public PacienteDTO buscarPacientePorId(Long id) {
        logger.info("SERVICE: Buscando paciente com ID: {}", id);
        PacienteEntity pacienteEntity = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com ID: " + id));
        return pacienteMapper.toDTO(pacienteEntity);
    }

    @Transactional
    public PacienteDTO atualizarPaciente(Long id, PacienteUpdateDTO pacienteUpdateDTO) {
        logger.info("SERVICE: Atualizando paciente com ID: {}", id);
        PacienteEntity pacienteEntity = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com ID: " + id));

        if (pacienteUpdateDTO.getEmail() != null &&
                StringUtils.hasText(pacienteUpdateDTO.getEmail()) &&
                (pacienteEntity.getContato() == null || !pacienteUpdateDTO.getEmail().trim().equalsIgnoreCase(pacienteEntity.getContato().getEmail()))) {

            String novoEmail = pacienteUpdateDTO.getEmail().trim().toLowerCase();
            Optional<ContatoEntity> contatoComEmail = contatoRepository.findByEmail(novoEmail);

            if (contatoComEmail.isPresent() && (pacienteEntity.getContato() == null || !contatoComEmail.get().getId().equals(pacienteEntity.getContato().getId()))) {
                throw new EmailAlreadyExistsException("Email " + novoEmail + " já cadastrado para outro contato.");
            }
        }

        pacienteMapper.updateEntityFromDTO(pacienteUpdateDTO, pacienteEntity);

        PacienteEntity pacienteAtualizado = pacienteRepository.save(pacienteEntity);
        logger.info("SERVICE: Paciente atualizado com ID: {}", pacienteAtualizado.getId());
        return pacienteMapper.toDTO(pacienteAtualizado);
    }

    @Transactional
    public void deletarPaciente(Long id) {
        logger.info("SERVICE: Deletando paciente com ID: {}", id);
        PacienteEntity paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com ID: " + id));
        pacienteRepository.delete(paciente);
        logger.info("SERVICE: Paciente deletado com ID: {}", id);
    }
}