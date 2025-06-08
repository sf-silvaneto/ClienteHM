package com.clientehm.mapper;

import com.clientehm.entity.MedicoEntity;
// import com.clientehm.entity.StatusMedico; // Remova esta importação
import com.clientehm.model.MedicoCreateDTO;
import com.clientehm.model.MedicoDTO;
import com.clientehm.model.MedicoUpdateDTO;
import com.clientehm.model.ProntuarioDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MedicoMapper {

    @Autowired
    private ModelMapper modelMapper;

    public MedicoEntity toEntity(MedicoCreateDTO createDTO) {
        // ModelMapper cuida da maior parte, mas podemos definir o excludedAt inicial
        MedicoEntity entity = modelMapper.map(createDTO, MedicoEntity.class);
        entity.setExcludedAt(null); // Médicos recém-criados são ativos
        return entity;
    }

    // Este método toEntity(MedicoUpdateDTO) não é usado na camada de serviço para atualização.
    // A atualização é feita no updateEntityFromDTO.
    public MedicoEntity toEntity(MedicoUpdateDTO updateDTO) {
        return modelMapper.map(updateDTO, MedicoEntity.class);
    }

    public MedicoDTO toDTO(MedicoEntity medicoEntity) {
        if (medicoEntity == null) {
            return null;
        }
        // ModelMapper mapeia automaticamente 'excludedAt' agora
        return modelMapper.map(medicoEntity, MedicoDTO.class);
    }

    public List<MedicoDTO> toDTOList(List<MedicoEntity> medicos) {
        return medicos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Page<MedicoDTO> toDTOPage(Page<MedicoEntity> medicosPage) {
        return medicosPage.map(this::toDTO);
    }

    public void updateEntityFromDTO(MedicoUpdateDTO updateDTO, MedicoEntity medicoEntity) {
        if (updateDTO.getNomeCompleto() != null) {
            medicoEntity.setNomeCompleto(updateDTO.getNomeCompleto());
        }
        if (updateDTO.getCrm() != null) {
            medicoEntity.setCrm(updateDTO.getCrm());
        }
        if (updateDTO.getEspecialidade() != null) {
            medicoEntity.setEspecialidade(updateDTO.getEspecialidade());
        }
        if (updateDTO.getResumoEspecialidade() != null) {
            medicoEntity.setResumoEspecialidade(updateDTO.getResumoEspecialidade());
        }
        if (updateDTO.getRqe() != null) {
            medicoEntity.setRqe(updateDTO.getRqe());
        }
        // Antigo: if (updateDTO.getStatus() != null) { medicoEntity.setStatus(updateDTO.getStatus()); }
        // NOVO: Atualizar 'excludedAt'
        if (updateDTO.getExcludedAt() != null) {
            medicoEntity.setExcludedAt(updateDTO.getExcludedAt());
        } else if (updateDTO.getExcludedAt() == null && medicoEntity.getExcludedAt() != null && updateDTO.getCrm() != null) {
            // Se o DTO envia explicitamente null para excludedAt E a entidade jÁ estava inativa,
            // significa que o médico está sendo reativado. A verificação do CRM != null é só para garantir que é uma chamada de atualização válida.
            medicoEntity.setExcludedAt(null);
        }
        // Nota: a lógica de setar excludedAt para null se o médico for reativado pode ser mais robusta
        // na camada de serviço, para evitar dependências de outros campos do DTO.
    }

    public ProntuarioDTO.MedicoBasicDTO toMedicoBasicDTO(MedicoEntity medicoEntity) {
        if (medicoEntity == null) {
            return null;
        }
        ProntuarioDTO.MedicoBasicDTO dto = new ProntuarioDTO.MedicoBasicDTO();
        dto.setId(medicoEntity.getId());
        dto.setNomeCompleto(medicoEntity.getNomeCompleto());
        dto.setCrm(medicoEntity.getCrm());
        dto.setEspecialidade(medicoEntity.getEspecialidade());
        return dto;
    }
}