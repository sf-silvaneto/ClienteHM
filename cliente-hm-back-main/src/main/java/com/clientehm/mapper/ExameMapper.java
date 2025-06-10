package com.clientehm.mapper;

import com.clientehm.entity.ExameRegistroEntity;
import com.clientehm.entity.MedicoEntity;
import com.clientehm.entity.AdministradorEntity;
import com.clientehm.model.CriarExameRequestDTO;
import com.clientehm.model.AtualizarExameRequestDTO;
import com.clientehm.model.ExameRegistroDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ExameMapper {

    @Autowired
    private ModelMapper modelMapper;

    public ExameRegistroEntity toEntity(CriarExameRequestDTO dto) {
        ExameRegistroEntity entity = new ExameRegistroEntity();
        entity.setNome(dto.getNome());
        entity.setResultado(dto.getResultado());
        entity.setObservacoes(dto.getObservacoes());
        entity.setDataExame(dto.getDataExame()); // Mapear o novo campo
        return entity;
    }

    public ExameRegistroDTO toDTO(ExameRegistroEntity entity) {
        if (entity == null) return null;
        ExameRegistroDTO dto = modelMapper.map(entity, ExameRegistroDTO.class);

        if (entity.getProntuario() != null) {
            dto.setProntuarioId(entity.getProntuario().getId());
        }
        if (entity.getMedicoResponsavelExame() != null) {
            dto.setMedicoResponsavelExameId(entity.getMedicoResponsavelExame().getId());
            dto.setMedicoResponsavelExameNome(entity.getMedicoResponsavelExame().getNomeCompleto());
        }
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setDataExame(entity.getDataExame()); // Mapear o novo campo
        return dto;
    }

    public void updateEntityFromDTO(AtualizarExameRequestDTO dto, ExameRegistroEntity entity,
                                    MedicoEntity medicoResponsavel, AdministradorEntity adminLogado) {
        if (StringUtils.hasText(dto.getNome())) entity.setNome(dto.getNome());
        if (StringUtils.hasText(dto.getResultado())) entity.setResultado(dto.getResultado());
        if (dto.getObservacoes() != null) {
            entity.setObservacoes(StringUtils.hasText(dto.getObservacoes()) ? dto.getObservacoes().trim() : null);
        }
        if (dto.getDataExame() != null) entity.setDataExame(dto.getDataExame()); // Atualizar o novo campo

        if (medicoResponsavel != null) {
            entity.setMedicoResponsavelExame(medicoResponsavel);
        } else if (adminLogado != null) {
            entity.setMedicoResponsavelExame(null);
        } else {
            entity.setMedicoResponsavelExame(null);
        }
    }
}