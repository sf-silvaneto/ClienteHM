package com.clientehm.config;

import com.clientehm.entity.ConsultaRegistroEntity;
import com.clientehm.entity.PacienteEntity;
import com.clientehm.entity.ExameRegistroEntity;
import com.clientehm.entity.ProcedimentoRegistroEntity;
import com.clientehm.entity.EncaminhamentoRegistroEntity;
import com.clientehm.model.ConsultaDTO;
import com.clientehm.model.PacienteUpdateDTO;
import com.clientehm.model.CriarConsultaRequestDTO;
import com.clientehm.model.AtualizarConsultaRequestDTO;
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

        TypeMap<ConsultaRegistroEntity, ConsultaDTO> consultaEntityTypeMap =
                modelMapper.createTypeMap(ConsultaRegistroEntity.class, ConsultaDTO.class);
        consultaEntityTypeMap.addMappings(mapper -> {
            // mapper.skip(ConsultaDTO::setResponsavelId); // Removido
            // mapper.skip(ConsultaDTO::setTipoResponsavel); // Removido
            // mapper.skip(ConsultaDTO::setResponsavelNomeCompleto); // Removido
            // mapper.skip(ConsultaDTO::setResponsavelEspecialidade); // Removido
            // mapper.skip(ConsultaDTO::setResponsavelCRM); // Removido
            mapper.map(ConsultaRegistroEntity::getUpdatedAt, ConsultaDTO::setUpdatedAt);
            mapper.map(ConsultaRegistroEntity::getCreatedAt, ConsultaDTO::setCreatedAt);
        });

        TypeMap<PacienteUpdateDTO, PacienteEntity> pacienteUpdateTypeMap =
                modelMapper.createTypeMap(PacienteUpdateDTO.class, PacienteEntity.class);
        pacienteUpdateTypeMap.addMappings(mapper -> {
            mapper.skip(PacienteEntity::setAlergias);
            mapper.skip(PacienteEntity::setComorbidades);
            mapper.skip(PacienteEntity::setMedicamentosContinuos);
        });

        modelMapper.createTypeMap(CriarConsultaRequestDTO.class, ConsultaRegistroEntity.class)
                .addMappings(mapper -> mapper.skip(ConsultaRegistroEntity::setCreatedAt));
        modelMapper.createTypeMap(AtualizarConsultaRequestDTO.class, ConsultaRegistroEntity.class)
                .addMappings(mapper -> mapper.skip(ConsultaRegistroEntity::setUpdatedAt));
        TypeMap<ExameRegistroEntity, com.clientehm.model.ExameRegistroDTO> exameEntityTypeMap =
                modelMapper.createTypeMap(ExameRegistroEntity.class, com.clientehm.model.ExameRegistroDTO.class);
        exameEntityTypeMap.addMappings(mapper -> {
        });

        TypeMap<ProcedimentoRegistroEntity, com.clientehm.model.ProcedimentoRegistroDTO> procedimentoEntityTypeMap =
                modelMapper.createTypeMap(ProcedimentoRegistroEntity.class, com.clientehm.model.ProcedimentoRegistroDTO.class);
        procedimentoEntityTypeMap.addMappings(mapper -> {
        });

        TypeMap<EncaminhamentoRegistroEntity, com.clientehm.model.EncaminhamentoRegistroDTO> encaminhamentoEntityTypeMap =
                modelMapper.createTypeMap(EncaminhamentoRegistroEntity.class, com.clientehm.model.EncaminhamentoRegistroDTO.class);
        encaminhamentoEntityTypeMap.addMappings(mapper -> {
        });

        return modelMapper;
    }
}