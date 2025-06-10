package com.clientehm.mapper;

import com.clientehm.entity.EncaminhamentoRegistroEntity;
import com.clientehm.entity.MedicoEntity;
import com.clientehm.model.CriarEncaminhamentoRequestDTO;
import com.clientehm.model.AtualizarEncaminhamentoRequestDTO;
import com.clientehm.model.EncaminhamentoRegistroDTO;
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
        entity.setDataEncaminhamento(dto.getDataEncaminhamento()); // Mapear o novo campo
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
        dto.setDataEncaminhamento(entity.getDataEncaminhamento()); // Mapear o novo campo
        return dto;
    }

    public void updateEntityFromDTO(AtualizarEncaminhamentoRequestDTO dto, EncaminhamentoRegistroEntity entity, MedicoEntity medicoSolicitante) {
        if (StringUtils.hasText(dto.getEspecialidadeDestino())) entity.setEspecialidadeDestino(dto.getEspecialidadeDestino());
        if (StringUtils.hasText(dto.getMotivoEncaminhamento())) entity.setMotivoEncaminhamento(dto.getMotivoEncaminhamento());
        if (dto.getObservacoes() != null) entity.setObservacoes(StringUtils.hasText(dto.getObservacoes()) ? dto.getObservacoes().trim() : null);
        if (dto.getDataEncaminhamento() != null) entity.setDataEncaminhamento(dto.getDataEncaminhamento()); // Atualizar o novo campo

        if (medicoSolicitante != null) {
            entity.setMedicoSolicitante(medicoSolicitante);
        }
    }
}