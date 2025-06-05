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
        // MedicoSolicitante e Prontuario são definidos no serviço
        return modelMapper.map(dto, EncaminhamentoRegistroEntity.class);
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
        }
        // O nomeResponsavelDisplay deve ser definido no serviço
        return dto;
    }

    public void updateEntityFromDTO(AtualizarEncaminhamentoRequestDTO dto, EncaminhamentoRegistroEntity entity, MedicoEntity medicoSolicitante) {
        if (dto.getDataEncaminhamento() != null) entity.setDataEncaminhamento(dto.getDataEncaminhamento());
        if (StringUtils.hasText(dto.getEspecialidadeDestino())) entity.setEspecialidadeDestino(dto.getEspecialidadeDestino());
        if (StringUtils.hasText(dto.getMotivoEncaminhamento())) entity.setMotivoEncaminhamento(dto.getMotivoEncaminhamento());
        if (dto.getObservacoes() != null) entity.setObservacoes(StringUtils.hasText(dto.getObservacoes()) ? dto.getObservacoes().trim() : null);

        // MedicoSolicitante é obrigatório, então deve ser passado pelo serviço.
        if (medicoSolicitante != null) {
            entity.setMedicoSolicitante(medicoSolicitante);
            entity.setNomeResponsavelDisplay(medicoSolicitante.getNomeCompleto());
        }
    }
}