package com.clientehm.config;

import com.clientehm.entity.ConsultaRegistroEntity;
import com.clientehm.entity.PacienteEntity; // Importar PacienteEntity
import com.clientehm.model.ConsultaDTO;
import com.clientehm.model.PacienteUpdateDTO; // Importar PacienteUpdateDTO
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap; // Importar TypeMap
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

        // NOVO: Adicionar mapeamento para PacienteUpdateDTO para PacienteEntity para pular coleções
        TypeMap<PacienteUpdateDTO, PacienteEntity> pacienteUpdateTypeMap =
                modelMapper.createTypeMap(PacienteUpdateDTO.class, PacienteEntity.class);

        pacienteUpdateTypeMap.addMappings(mapper -> {
            mapper.skip(PacienteEntity::setAlergias);
            mapper.skip(PacienteEntity::setComorbidades);
            mapper.skip(PacienteEntity::setMedicamentosContinuos);
        });

        return modelMapper;
    }
}