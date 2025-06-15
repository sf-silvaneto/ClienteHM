package com.main.mapper;

import com.main.domain.entity.EncaminhamentoRegistroEntity;
import com.main.domain.entity.MedicoEntity;
import com.main.api.model.CriarEncaminhamentoRequestDTO;
import com.main.api.model.AtualizarEncaminhamentoRequestDTO;
import com.main.api.model.EncaminhamentoRegistroDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EncaminhamentoMapper {

    @Autowired
    private ModelMapper modelMapper;

    public EncaminhamentoRegistroEntity toEntity(CriarEncaminhamentoRequestDTO dto) {
        EncaminhamentoRegistroEntity entity = new EncaminhamentoRegistroEntity();
        entity.setEspecialidadeDestino(dto.getEspecialidadeDestino());
        entity.setMotivoEncaminhamento(dto.getMotivoEncaminhamento());
        entity.setObservacoes(dto.getObservacoes());
        entity.setDataEncaminhamento(dto.getDataEncaminhamento());
        return entity;
    }

    public EncaminhamentoRegistroDTO toDTO(EncaminhamentoRegistroEntity entity) {
        if (entity == null) return null;
        EncaminhamentoRegistroDTO dto = modelMapper.map(entity, EncaminhamentoRegistroDTO.class);

        if (entity.getProntuario() != null) {
            dto.setProntuarioId(entity.getProntuario().getId());
        }
        if (entity.getMedicoSolicitante() != null) {
            dto.setMedicoSolicitanteId(entity.getMedicoSolicitante().getId());
            dto.setMedicoSolicitanteNome(entity.getMedicoSolicitante().getNomeCompleto());
            dto.setMedicoSolicitanteCRM(entity.getMedicoSolicitante().getCrm());
            dto.setMedicoSolicitanteEspecialidade(entity.getMedicoSolicitante().getEspecialidade());
        }
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setDataEncaminhamento(entity.getDataEncaminhamento());
        return dto;
    }

    public void updateEntityFromDTO(AtualizarEncaminhamentoRequestDTO dto, EncaminhamentoRegistroEntity entity, MedicoEntity medicoSolicitante) {
        if (StringUtils.hasText(dto.getEspecialidadeDestino())) entity.setEspecialidadeDestino(dto.getEspecialidadeDestino());
        if (StringUtils.hasText(dto.getMotivoEncaminhamento())) entity.setMotivoEncaminhamento(dto.getMotivoEncaminhamento());
        if (dto.getObservacoes() != null) entity.setObservacoes(StringUtils.hasText(dto.getObservacoes()) ? dto.getObservacoes().trim() : null);
        if (dto.getDataEncaminhamento() != null) entity.setDataEncaminhamento(dto.getDataEncaminhamento());

        if (medicoSolicitante != null) {
            entity.setMedicoSolicitante(medicoSolicitante);
        }
    }
}