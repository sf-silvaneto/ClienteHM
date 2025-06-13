package com.clientehm.mapper;

import com.clientehm.entity.SinaisVitaisEntity;
import com.clientehm.model.SinaisVitaisDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SinaisVitaisMapper {

    @Autowired
    private ModelMapper modelMapper;

    public SinaisVitaisEntity toEntity(SinaisVitaisDTO dto) {
        if (dto == null) return null;
        return modelMapper.map(dto, SinaisVitaisEntity.class);
    }

    public SinaisVitaisDTO toDTO(SinaisVitaisEntity entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, SinaisVitaisDTO.class);
    }

    public void updateEntityFromDTO(SinaisVitaisDTO dto, SinaisVitaisEntity entity) {
        if (dto == null || entity == null) return;

        if (dto.getPressaoArterial() != null) {
            entity.setPressaoArterial(StringUtils.hasText(dto.getPressaoArterial()) ? dto.getPressaoArterial().trim() : null);
        }
        if (dto.getTemperatura() != null) {
            entity.setTemperatura(StringUtils.hasText(dto.getTemperatura()) ? dto.getTemperatura().trim() : null);
        }
        if (dto.getFrequenciaCardiaca() != null) {
            entity.setFrequenciaCardiaca(StringUtils.hasText(dto.getFrequenciaCardiaca()) ? dto.getFrequenciaCardiaca().trim() : null);
        }
        if (dto.getSaturacao() != null) {
            entity.setSaturacao(StringUtils.hasText(dto.getSaturacao()) ? dto.getSaturacao().trim() : null);
        }
        if (dto.getHgt() != null) {
            entity.setHgt(StringUtils.hasText(dto.getHgt()) ? dto.getHgt().trim() : null);
        }
    }
}