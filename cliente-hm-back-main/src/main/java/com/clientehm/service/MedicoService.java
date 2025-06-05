package com.clientehm.service;

import com.clientehm.entity.MedicoEntity;
import com.clientehm.entity.StatusMedico;
import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.exception.CrmAlreadyExistsException;
import com.clientehm.mapper.MedicoMapper; // Importar o Mapper
import com.clientehm.model.MedicoCreateDTO;
import com.clientehm.model.MedicoDTO;
import com.clientehm.model.MedicoUpdateDTO;
import com.clientehm.repository.MedicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// Removido BeanUtils pois ModelMapper será usado
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

    @Autowired
    private MedicoMapper medicoMapper; // Injetar o Mapper

    // O método convertToDTO foi movido para MedicoMapper

    @Transactional
    public MedicoDTO criarMedico(MedicoCreateDTO medicoCreateDTO) {
        logger.info("SERVICE: Tentando criar médico com CRM: {}", medicoCreateDTO.getCrm());
        if (medicoRepository.findByCrm(medicoCreateDTO.getCrm()).isPresent()) {
            throw new CrmAlreadyExistsException("CRM " + medicoCreateDTO.getCrm() + " já cadastrado.");
        }

        MedicoEntity medicoEntity = medicoMapper.toEntity(medicoCreateDTO);
        medicoEntity.setStatus(StatusMedico.ATIVO); // Define o status padrão

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
    public Page<MedicoDTO> buscarMedicosPorStatus(StatusMedico status, Pageable pageable) {
        logger.info("SERVICE: Buscando médicos por status: {}", status);
        Page<MedicoEntity> medicosPage = medicoRepository.findByStatus(status, pageable);
        return medicoMapper.toDTOPage(medicosPage);
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

        // Validação de unicidade do CRM se estiver sendo alterado
        if (medicoUpdateDTO.getCrm() != null &&
                !medicoUpdateDTO.getCrm().trim().isEmpty() &&
                !medicoUpdateDTO.getCrm().equals(medicoEntity.getCrm())) {
            Optional<MedicoEntity> medicoExistenteComCrm = medicoRepository.findByCrm(medicoUpdateDTO.getCrm());
            if (medicoExistenteComCrm.isPresent() && !medicoExistenteComCrm.get().getId().equals(id)) {
                throw new CrmAlreadyExistsException("CRM " + medicoUpdateDTO.getCrm() + " já cadastrado para outro médico.");
            }
        }

        // Usar o mapper para aplicar as atualizações do DTO na entidade
        medicoMapper.updateEntityFromDTO(medicoUpdateDTO, medicoEntity);

        MedicoEntity medicoAtualizado = medicoRepository.save(medicoEntity);
        logger.info("SERVICE: Médico atualizado com ID: {}", medicoAtualizado.getId());
        return medicoMapper.toDTO(medicoAtualizado);
    }

    @Transactional
    public MedicoDTO atualizarStatusMedico(Long id, StatusMedico status) {
        logger.info("SERVICE: Atualizando status do médico com ID: {} para {}", id, status);
        MedicoEntity medicoEntity = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado com ID: " + id));

        medicoEntity.setStatus(status);
        MedicoEntity medicoAtualizado = medicoRepository.save(medicoEntity);
        logger.info("SERVICE: Status do médico atualizado com ID: {}", medicoAtualizado.getId());
        return medicoMapper.toDTO(medicoAtualizado);
    }

    @Transactional
    public void deletarMedico(Long id) {
        logger.info("SERVICE: Deletando médico com ID: {}", id);
        if (!medicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Médico não encontrado com ID: " + id);
        }
        // Adicionar lógica aqui para verificar se o médico está associado a prontuários
        // Se estiver, você pode impedir a exclusão ou anonimizar o médico.
        // Por enquanto, apenas deleta.
        medicoRepository.deleteById(id);
        logger.info("SERVICE: Médico deletado com ID: {}", id);
    }
}