package com.clientehm.mapper;

import com.clientehm.entity.MedicoEntity;
import com.clientehm.model.MedicoCreateDTO;
import com.clientehm.model.MedicoDTO;
import com.clientehm.model.MedicoUpdateDTO;
import com.clientehm.model.ProntuarioDTO; // Certifique-se de que ProntuarioDTO está acessível ou importe a classe interna estática
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
        return modelMapper.map(createDTO, MedicoEntity.class);
    }

    public MedicoEntity toEntity(MedicoUpdateDTO updateDTO) {
        return modelMapper.map(updateDTO, MedicoEntity.class);
    }

    public MedicoDTO toDTO(MedicoEntity medicoEntity) {
        if (medicoEntity == null) {
            return null;
        }
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
        if (updateDTO.getStatus() != null) {
            medicoEntity.setStatus(updateDTO.getStatus());
        }
    }

    /**
     * Converte MedicoEntity para ProntuarioDTO.MedicoBasicDTO.
     * Usado para representar informações básicas do médico dentro do ProntuarioDTO.
     */
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