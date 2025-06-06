package com.clientehm.config;

import com.clientehm.entity.ConsultaRegistroEntity;
import com.clientehm.model.ConsultaDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setAmbiguityIgnored(true);

        TypeMap<ConsultaRegistroEntity, ConsultaDTO> consultaTypeMap =
                modelMapper.createTypeMap(ConsultaRegistroEntity.class, ConsultaDTO.class);

        consultaTypeMap.addMappings(mapper -> {
            mapper.skip(ConsultaDTO::setResponsavelId);
            mapper.skip(ConsultaDTO::setTipoResponsavel);
            mapper.skip(ConsultaDTO::setResponsavelNomeCompleto);
            mapper.skip(ConsultaDTO::setResponsavelEspecialidade);
            mapper.skip(ConsultaDTO::setResponsavelCRM);

            mapper.map(ConsultaRegistroEntity::getCreatedAt, ConsultaDTO::setCreatedAt);
            mapper.map(ConsultaRegistroEntity::getUpdatedAt, ConsultaDTO::setUpdatedAt);
        });

        return modelMapper;
    }
}