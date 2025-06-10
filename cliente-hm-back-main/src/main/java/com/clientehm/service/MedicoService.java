package com.clientehm.service;

import com.clientehm.entity.MedicoEntity;
import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.exception.CrmAlreadyExistsException;
import com.clientehm.mapper.MedicoMapper;
import com.clientehm.model.MedicoCreateDTO;
import com.clientehm.model.MedicoDTO;
import com.clientehm.model.MedicoUpdateDTO;
import com.clientehm.repository.MedicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MedicoService {

    private static final Logger logger = LoggerFactory.getLogger(MedicoService.class);

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private MedicoMapper medicoMapper;

    @Transactional
    public MedicoDTO criarMedico(MedicoCreateDTO medicoCreateDTO) {
        logger.info("SERVICE: Tentando criar médico com CRM: {}", medicoCreateDTO.getCrm());
        if (medicoRepository.findByCrm(medicoCreateDTO.getCrm()).isPresent()) {
            throw new CrmAlreadyExistsException("CRM " + medicoCreateDTO.getCrm() + " já cadastrado.");
        }

        MedicoEntity medicoEntity = medicoMapper.toEntity(medicoCreateDTO);
        medicoEntity.setExcludedAt(null);

        MedicoEntity medicoSalvo = medicoRepository.save(medicoEntity);
        logger.info("SERVICE: Médico criado com ID: {}", medicoSalvo.getId());
        return medicoMapper.toDTO(medicoSalvo);
    }

    @Transactional(readOnly = true)
    public Page<MedicoDTO> buscarTodosMedicos(Pageable pageable) {
        logger.info("SERVICE: Buscando todos os médicos paginados.");
        Page<MedicoEntity> medicosPage = medicoRepository.findAll(pageable);
        return medicoMapper.toDTOPage(medicosPage);
    }

    @Transactional(readOnly = true)
    public Page<MedicoDTO> buscarMedicosPorNome(String nome, Pageable pageable) {
        logger.info("SERVICE: Buscando médicos por nome contendo: {}", nome);
        Page<MedicoEntity> medicosPage = medicoRepository.findByNomeCompletoContainingIgnoreCase(nome, pageable);
        return medicoMapper.toDTOPage(medicosPage);
    }

    @Transactional(readOnly = true)
    public Page<MedicoDTO> buscarMedicosPorStatus(String status, Pageable pageable) {
        logger.info("SERVICE: Buscando médicos por status: {}", status);
        if ("ATIVO".equalsIgnoreCase(status)) {
            return medicoMapper.toDTOPage(medicoRepository.findByExcludedAtIsNull(pageable));
        } else if ("INATIVO".equalsIgnoreCase(status)) {
            return medicoMapper.toDTOPage(medicoRepository.findByExcludedAtIsNotNull(pageable));
        } else {
            return medicoMapper.toDTOPage(medicoRepository.findAll(pageable));
        }
    }

    @Transactional(readOnly = true)
    public Page<MedicoDTO> buscarMedicosPorEspecialidade(String especialidade, Pageable pageable) {
        logger.info("SERVICE: Buscando médicos pela especialidade: {}", especialidade);
        Page<MedicoEntity> medicosPage = medicoRepository.findByEspecialidadeIgnoreCase(especialidade, pageable);
        return medicoMapper.toDTOPage(medicosPage);
    }

    @Transactional(readOnly = true)
    public Page<MedicoDTO> buscarMedicosPorCrm(String crm, Pageable pageable) {
        logger.info("SERVICE: Buscando médicos pelo CRM: {}", crm);
        Page<MedicoEntity> medicosPage = medicoRepository.findByCrmIgnoreCase(crm, pageable);
        return medicoMapper.toDTOPage(medicosPage);
    }

    @Transactional(readOnly = true)
    public MedicoDTO buscarMedicoPorId(Long id) {
        logger.info("SERVICE: Buscando médico com ID: {}", id);
        MedicoEntity medicoEntity = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado com ID: " + id));
        return medicoMapper.toDTO(medicoEntity);
    }

    @Transactional
    public MedicoDTO atualizarMedico(Long id, MedicoUpdateDTO medicoUpdateDTO) {
        logger.info("SERVICE: Atualizando médico com ID: {}", id);
        MedicoEntity medicoEntity = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado com ID: " + id));

        if (medicoUpdateDTO.getCrm() != null &&
                !medicoUpdateDTO.getCrm().trim().isEmpty() &&
                !medicoUpdateDTO.getCrm().equals(medicoEntity.getCrm())) {
            Optional<MedicoEntity> medicoExistenteComCrm = medicoRepository.findByCrm(medicoUpdateDTO.getCrm());
            if (medicoExistenteComCrm.isPresent() && !medicoExistenteComCrm.get().getId().equals(id)) {
                throw new CrmAlreadyExistsException("CRM " + medicoUpdateDTO.getCrm() + " já cadastrado para outro médico.");
            }
        }

        medicoMapper.updateEntityFromDTO(medicoUpdateDTO, medicoEntity);

        MedicoEntity medicoAtualizado = medicoRepository.save(medicoEntity);
        logger.info("SERVICE: Médico atualizado com ID: {}", medicoAtualizado.getId());
        return medicoMapper.toDTO(medicoAtualizado);
    }

    @Transactional
    public MedicoDTO atualizarStatusMedico(Long id, boolean ativar) {
        logger.info("SERVICE: Tentando {} médico com ID: {}", ativar ? "ativar" : "inativar", id);
        MedicoEntity medicoEntity = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado com ID: " + id));

        if (ativar) {
            medicoEntity.setExcludedAt(null);
        } else {
            medicoEntity.setExcludedAt(LocalDateTime.now());
        }
        MedicoEntity medicoAtualizado = medicoRepository.save(medicoEntity);
        logger.info("SERVICE: Médico com ID: {} {} com sucesso.", medicoAtualizado.getId(), ativar ? "ativado" : "inativado");
        return medicoMapper.toDTO(medicoAtualizado);
    }


    @Transactional
    public void deletarMedico(Long id) {
        logger.info("SERVICE: Deletando médico com ID: {}", id);
        if (!medicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Médico não encontrado com ID: " + id);
        }
        medicoRepository.deleteById(id);
        logger.info("SERVICE: Médico deletado com ID: {}", id);
    }
}