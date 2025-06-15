package com.main.config;

import com.main.api.model.*;
import com.main.domain.entity.ConsultaRegistroEntity;
import com.main.domain.entity.PacienteEntity;
import com.main.domain.entity.ExameRegistroEntity;
import com.main.domain.entity.ProcedimentoRegistroEntity;
import com.main.domain.entity.EncaminhamentoRegistroEntity;
import com.main.api.model.ConsultaDTO;
import com.main.api.model.CriarConsultaRequestDTO;
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
            mapper.map(src -> src.getDataConsulta(), ConsultaDTO::setDataConsulta);
            mapper.map(ConsultaRegistroEntity::getUpdatedAt, ConsultaDTO::setUpdatedAt);
            mapper.map(ConsultaRegistroEntity::getCreatedAt, ConsultaDTO::setCreatedAt);
        });

        TypeMap<CriarConsultaRequestDTO, ConsultaRegistroEntity> criarConsultaRequestTypeMap =
                modelMapper.createTypeMap(CriarConsultaRequestDTO.class, ConsultaRegistroEntity.class);
        criarConsultaRequestTypeMap.addMappings(mapper -> {
            mapper.map(src -> src.getDataConsulta(), ConsultaRegistroEntity::setDataConsulta);
            mapper.skip(ConsultaRegistroEntity::setCreatedAt);
        });

        TypeMap<AtualizarConsultaRequestDTO, ConsultaRegistroEntity> atualizarConsultaRequestTypeMap =
                modelMapper.createTypeMap(AtualizarConsultaRequestDTO.class, ConsultaRegistroEntity.class);
        atualizarConsultaRequestTypeMap.addMappings(mapper -> {
            mapper.map(src -> src.getDataConsulta(), ConsultaRegistroEntity::setDataConsulta);
            mapper.skip(ConsultaRegistroEntity::setUpdatedAt);
        });

        TypeMap<ExameRegistroEntity, ExameRegistroDTO> exameEntityTypeMap =
                modelMapper.createTypeMap(ExameRegistroEntity.class, ExameRegistroDTO.class);
        exameEntityTypeMap.addMappings(mapper -> {
            mapper.map(src -> src.getDataExame(), ExameRegistroDTO::setDataExame);
        });

        modelMapper.createTypeMap(CriarExameRequestDTO.class, ExameRegistroEntity.class)
                .addMappings(mapper -> mapper.map(src -> src.getDataExame(), ExameRegistroEntity::setDataExame));

        modelMapper.createTypeMap(AtualizarExameRequestDTO.class, ExameRegistroEntity.class)
                .addMappings(mapper -> mapper.map(src -> src.getDataExame(), ExameRegistroEntity::setDataExame));


        TypeMap<ProcedimentoRegistroEntity, ProcedimentoRegistroDTO> procedimentoEntityTypeMap =
                modelMapper.createTypeMap(ProcedimentoRegistroEntity.class, ProcedimentoRegistroDTO.class);
        procedimentoEntityTypeMap.addMappings(mapper -> {
            mapper.map(src -> src.getDataProcedimento(), ProcedimentoRegistroDTO::setDataProcedimento);
        });

        modelMapper.createTypeMap(CriarProcedimentoRequestDTO.class, ProcedimentoRegistroEntity.class)
                .addMappings(mapper -> mapper.map(src -> src.getDataProcedimento(), ProcedimentoRegistroEntity::setDataProcedimento));
        modelMapper.createTypeMap(AtualizarProcedimentoRequestDTO.class, ProcedimentoRegistroEntity.class)
                .addMappings(mapper -> mapper.map(src -> src.getDataProcedimento(), ProcedimentoRegistroEntity::setDataProcedimento));


        TypeMap<EncaminhamentoRegistroEntity, EncaminhamentoRegistroDTO> encaminhamentoEntityTypeMap =
                modelMapper.createTypeMap(EncaminhamentoRegistroEntity.class, EncaminhamentoRegistroDTO.class);
        encaminhamentoEntityTypeMap.addMappings(mapper -> {
            mapper.map(src -> src.getDataEncaminhamento(), EncaminhamentoRegistroDTO::setDataEncaminhamento);
        });

        modelMapper.createTypeMap(CriarEncaminhamentoRequestDTO.class, EncaminhamentoRegistroEntity.class)
                .addMappings(mapper -> mapper.map(src -> src.getDataEncaminhamento(), EncaminhamentoRegistroEntity::setDataEncaminhamento));
        modelMapper.createTypeMap(AtualizarEncaminhamentoRequestDTO.class, EncaminhamentoRegistroEntity.class)
                .addMappings(mapper -> mapper.map(src -> src.getDataEncaminhamento(), EncaminhamentoRegistroEntity::setDataEncaminhamento));

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