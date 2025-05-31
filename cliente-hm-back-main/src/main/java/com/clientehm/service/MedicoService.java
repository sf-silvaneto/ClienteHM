package com.clientehm.service;

import com.clientehm.entity.MedicoEntity;
import com.clientehm.entity.StatusMedico;
import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.exception.CrmAlreadyExistsException;
import com.clientehm.model.MedicoCreateDTO;
import com.clientehm.model.MedicoDTO;
import com.clientehm.model.MedicoUpdateDTO;
import com.clientehm.repository.MedicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MedicoService {

    private static final Logger logger = LoggerFactory.getLogger(MedicoService.class);

    @Autowired
    private MedicoRepository medicoRepository;

    private MedicoDTO convertToDTO(MedicoEntity entity) {
        if (entity == null) return null;
        MedicoDTO dto = new MedicoDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Transactional
    public MedicoDTO criarMedico(MedicoCreateDTO medicoCreateDTO) {
        logger.info("SERVICE: Tentando criar médico com CRM: {}", medicoCreateDTO.getCrm());
        // Assume que MedicoCreateDTO.getCrm() retorna o CRM completo (numero+UF) se for o caso,
        // ou apenas o número se o campo na entidade for só número.
        // A lógica de combinação de CRM+UF do MedicoForm.tsx deve ser consistente com o que é esperado aqui.
        if (medicoRepository.findByCrm(medicoCreateDTO.getCrm()).isPresent()) {
            throw new CrmAlreadyExistsException("CRM " + medicoCreateDTO.getCrm() + " já cadastrado.");
        }

        MedicoEntity medicoEntity = new MedicoEntity();
        medicoEntity.setNomeCompleto(medicoCreateDTO.getNomeCompleto());
        medicoEntity.setCrm(medicoCreateDTO.getCrm()); // Salva o CRM como recebido
        medicoEntity.setEspecialidade(medicoCreateDTO.getEspecialidade());
        medicoEntity.setResumoEspecialidade(medicoCreateDTO.getResumoEspecialidade());
        medicoEntity.setRqe(medicoCreateDTO.getRqe());
        medicoEntity.setStatus(StatusMedico.ATIVO); // Default status

        MedicoEntity medicoSalvo = medicoRepository.save(medicoEntity);
        logger.info("SERVICE: Médico criado com ID: {}", medicoSalvo.getId());
        return convertToDTO(medicoSalvo);
    }

    @Transactional(readOnly = true)
    public Page<MedicoDTO> buscarTodosMedicos(Pageable pageable) {
        logger.info("SERVICE: Buscando todos os médicos paginados.");
        return medicoRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<MedicoDTO> buscarMedicosPorNome(String nome, Pageable pageable) {
        logger.info("SERVICE: Buscando médicos por nome contendo: {}", nome);
        return medicoRepository.findByNomeCompletoContainingIgnoreCase(nome, pageable).map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<MedicoDTO> buscarMedicosPorStatus(StatusMedico status, Pageable pageable) {
        logger.info("SERVICE: Buscando médicos por status: {}", status);
        return medicoRepository.findByStatus(status, pageable).map(this::convertToDTO);
    }

    // Método para buscar por especialidade
    @Transactional(readOnly = true)
    public Page<MedicoDTO> buscarMedicosPorEspecialidade(String especialidade, Pageable pageable) {
        logger.info("SERVICE: Buscando médicos pela especialidade: {}", especialidade); // Esta seria aproximadamente a linha 59
        return medicoRepository.findByEspecialidadeIgnoreCase(especialidade, pageable).map(this::convertToDTO);
    }

    // Método para buscar por CRM
    @Transactional(readOnly = true)
    public Page<MedicoDTO> buscarMedicosPorCrm(String crm, Pageable pageable) {
        logger.info("SERVICE: Buscando médicos pelo CRM: {}", crm);
        return medicoRepository.findByCrmIgnoreCase(crm, pageable).map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public MedicoDTO buscarMedicoPorId(Long id) {
        logger.info("SERVICE: Buscando médico com ID: {}", id);
        MedicoEntity medicoEntity = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado com ID: " + id));
        return convertToDTO(medicoEntity);
    }

    @Transactional
    public MedicoDTO atualizarMedico(Long id, MedicoUpdateDTO medicoUpdateDTO) {
        logger.info("SERVICE: Atualizando médico com ID: {}", id);
        MedicoEntity medicoEntity = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado com ID: " + id));

        // Lógica para atualizar CRM (considerando que MedicoUpdateDTO.getCrm() pode ser o CRM completo com UF)
        if (medicoUpdateDTO.getCrm() != null && !medicoUpdateDTO.getCrm().trim().isEmpty() && !medicoUpdateDTO.getCrm().equals(medicoEntity.getCrm())) {
            Optional<MedicoEntity> medicoExistenteComCrm = medicoRepository.findByCrm(medicoUpdateDTO.getCrm());
            if (medicoExistenteComCrm.isPresent() && !medicoExistenteComCrm.get().getId().equals(id)) {
                throw new CrmAlreadyExistsException("CRM " + medicoUpdateDTO.getCrm() + " já cadastrado para outro médico.");
            }
            medicoEntity.setCrm(medicoUpdateDTO.getCrm());
        }

        if (medicoUpdateDTO.getNomeCompleto() != null && !medicoUpdateDTO.getNomeCompleto().trim().isEmpty()) {
            medicoEntity.setNomeCompleto(medicoUpdateDTO.getNomeCompleto());
        }
        if (medicoUpdateDTO.getEspecialidade() != null && !medicoUpdateDTO.getEspecialidade().trim().isEmpty()) {
            medicoEntity.setEspecialidade(medicoUpdateDTO.getEspecialidade());
        }
        if (medicoUpdateDTO.getResumoEspecialidade() != null) {
            medicoEntity.setResumoEspecialidade(medicoUpdateDTO.getResumoEspecialidade().trim().isEmpty() ? null : medicoUpdateDTO.getResumoEspecialidade());
        }
        if (medicoUpdateDTO.getRqe() != null) {
            medicoEntity.setRqe(medicoUpdateDTO.getRqe().trim().isEmpty() ? null : medicoUpdateDTO.getRqe());
        }
        if (medicoUpdateDTO.getStatus() != null) {
            medicoEntity.setStatus(medicoUpdateDTO.getStatus());
        }

        MedicoEntity medicoAtualizado = medicoRepository.save(medicoEntity);
        logger.info("SERVICE: Médico atualizado com ID: {}", medicoAtualizado.getId());
        return convertToDTO(medicoAtualizado);
    }

    @Transactional
    public MedicoDTO atualizarStatusMedico(Long id, StatusMedico status) {
        logger.info("SERVICE: Atualizando status do médico com ID: {} para {}", id, status);
        MedicoEntity medicoEntity = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado com ID: " + id));

        medicoEntity.setStatus(status);
        MedicoEntity medicoAtualizado = medicoRepository.save(medicoEntity);
        logger.info("SERVICE: Status do médico atualizado com ID: {}", medicoAtualizado.getId());
        return convertToDTO(medicoAtualizado);
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