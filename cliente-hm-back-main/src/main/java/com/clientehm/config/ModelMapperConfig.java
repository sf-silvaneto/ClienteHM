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
            mapper.map(src -> src.getDataConsulta(), ConsultaDTO::setDataConsulta); // Mapeia dataConsulta
            mapper.map(ConsultaRegistroEntity::getUpdatedAt, ConsultaDTO::setUpdatedAt);
            mapper.map(ConsultaRegistroEntity::getCreatedAt, ConsultaDTO::setCreatedAt);
        });

        TypeMap<CriarConsultaRequestDTO, ConsultaRegistroEntity> criarConsultaRequestTypeMap =
                modelMapper.createTypeMap(CriarConsultaRequestDTO.class, ConsultaRegistroEntity.class);
        criarConsultaRequestTypeMap.addMappings(mapper -> {
            mapper.map(src -> src.getDataConsulta(), ConsultaRegistroEntity::setDataConsulta); // Mapeia dataConsulta de CreateDTO para Entity
            mapper.skip(ConsultaRegistroEntity::setCreatedAt);
        });

        TypeMap<AtualizarConsultaRequestDTO, ConsultaRegistroEntity> atualizarConsultaRequestTypeMap =
                modelMapper.createTypeMap(AtualizarConsultaRequestDTO.class, ConsultaRegistroEntity.class);
        atualizarConsultaRequestTypeMap.addMappings(mapper -> {
            mapper.map(src -> src.getDataConsulta(), ConsultaRegistroEntity::setDataConsulta); // Mapeia dataConsulta de UpdateDTO para Entity
            mapper.skip(ConsultaRegistroEntity::setUpdatedAt);
        });

        TypeMap<ExameRegistroEntity, com.clientehm.model.ExameRegistroDTO> exameEntityTypeMap =
                modelMapper.createTypeMap(ExameRegistroEntity.class, com.clientehm.model.ExameRegistroDTO.class);
        exameEntityTypeMap.addMappings(mapper -> {
            mapper.map(src -> src.getDataExame(), com.clientehm.model.ExameRegistroDTO::setDataExame); // Mapeia dataExame
        });

        modelMapper.createTypeMap(com.clientehm.model.CriarExameRequestDTO.class, ExameRegistroEntity.class)
                .addMappings(mapper -> mapper.map(src -> src.getDataExame(), ExameRegistroEntity::setDataExame));

        modelMapper.createTypeMap(com.clientehm.model.AtualizarExameRequestDTO.class, ExameRegistroEntity.class)
                .addMappings(mapper -> mapper.map(src -> src.getDataExame(), ExameRegistroEntity::setDataExame));


        TypeMap<ProcedimentoRegistroEntity, com.clientehm.model.ProcedimentoRegistroDTO> procedimentoEntityTypeMap =
                modelMapper.createTypeMap(ProcedimentoRegistroEntity.class, com.clientehm.model.ProcedimentoRegistroDTO.class);
        procedimentoEntityTypeMap.addMappings(mapper -> {
            mapper.map(src -> src.getDataProcedimento(), com.clientehm.model.ProcedimentoRegistroDTO::setDataProcedimento); // Mapeia dataProcedimento
        });

        modelMapper.createTypeMap(com.clientehm.model.CriarProcedimentoRequestDTO.class, ProcedimentoRegistroEntity.class)
                .addMappings(mapper -> mapper.map(src -> src.getDataProcedimento(), ProcedimentoRegistroEntity::setDataProcedimento));
        modelMapper.createTypeMap(com.clientehm.model.AtualizarProcedimentoRequestDTO.class, ProcedimentoRegistroEntity.class)
                .addMappings(mapper -> mapper.map(src -> src.getDataProcedimento(), ProcedimentoRegistroEntity::setDataProcedimento));


        TypeMap<EncaminhamentoRegistroEntity, com.clientehm.model.EncaminhamentoRegistroDTO> encaminhamentoEntityTypeMap =
                modelMapper.createTypeMap(EncaminhamentoRegistroEntity.class, com.clientehm.model.EncaminhamentoRegistroDTO.class);
        encaminhamentoEntityTypeMap.addMappings(mapper -> {
            mapper.map(src -> src.getDataEncaminhamento(), com.clientehm.model.EncaminhamentoRegistroDTO::setDataEncaminhamento); // Mapeia dataEncaminhamento
        });

        modelMapper.createTypeMap(com.clientehm.model.CriarEncaminhamentoRequestDTO.class, EncaminhamentoRegistroEntity.class)
                .addMappings(mapper -> mapper.map(src -> src.getDataEncaminhamento(), EncaminhamentoRegistroEntity::setDataEncaminhamento));
        modelMapper.createTypeMap(com.clientehm.model.AtualizarEncaminhamentoRequestDTO.class, EncaminhamentoRegistroEntity.class)
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