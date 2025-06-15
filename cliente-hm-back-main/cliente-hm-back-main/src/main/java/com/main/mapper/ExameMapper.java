package com.main.mapper;

import com.main.domain.entity.ExameRegistroEntity;
import com.main.domain.entity.MedicoEntity;
import com.main.domain.entity.AdministradorEntity;
import com.main.api.model.CriarExameRequestDTO;
import com.main.api.model.AtualizarExameRequestDTO;
import com.main.api.model.ExameRegistroDTO;
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
        entity.setDataExame(dto.getDataExame());
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
            dto.setMedicoResponsavelExameEspecialidade(entity.getMedicoResponsavelExame().getEspecialidade());
            dto.setMedicoResponsavelExameCRM(entity.getMedicoResponsavelExame().getCrm());
        }
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setDataExame(entity.getDataExame());
        return dto;
    }

    public void updateEntityFromDTO(AtualizarExameRequestDTO dto, ExameRegistroEntity entity,
                                    MedicoEntity medicoResponsavel, AdministradorEntity adminLogado) {
        if (StringUtils.hasText(dto.getNome())) entity.setNome(dto.getNome());
        if (StringUtils.hasText(dto.getResultado())) entity.setResultado(dto.getResultado());
        if (dto.getObservacoes() != null) {
            entity.setObservacoes(StringUtils.hasText(dto.getObservacoes()) ? dto.getObservacoes().trim() : null);
        }
        if (dto.getDataExame() != null) entity.setDataExame(dto.getDataExame());

        if (medicoResponsavel != null) {
            entity.setMedicoResponsavelExame(medicoResponsavel);
        } else if (adminLogado != null) {
            entity.setMedicoResponsavelExame(null);
        } else {
            entity.setMedicoResponsavelExame(null);
        }
    }
}