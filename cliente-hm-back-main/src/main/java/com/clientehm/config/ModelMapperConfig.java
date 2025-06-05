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

        // Configurações globais do ModelMapper
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD) // STANDARD é menos propenso a erros de ambiguidade que STRICT inicialmente
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true) // Pula campos nulos na origem (bom para atualizações parciais)
                .setAmbiguityIgnored(true); // <<<--- NOVA CONFIGURAÇÃO IMPORTANTE

        // Configuração específica para o mapeamento de EntradaMedicaRegistroEntity para ConsultaDTO
        // Mesmo com setAmbiguityIgnored(true), é uma boa prática definir explicitamente o skip.
        TypeMap<ConsultaRegistroEntity, ConsultaDTO> consultaTypeMap =
                modelMapper.createTypeMap(ConsultaRegistroEntity.class, ConsultaDTO.class);

        consultaTypeMap.addMappings(mapper -> {
            mapper.skip(ConsultaDTO::setResponsavelId); // Pula o mapeamento automático de responsavelId
            mapper.skip(ConsultaDTO::setTipoResponsavel); // Pula o mapeamento automático de tipoResponsavel
            mapper.skip(ConsultaDTO::setResponsavelNomeCompleto); // Pula o mapeamento automático de responsavelNomeCompleto
            mapper.skip(ConsultaDTO::setResponsavelEspecialidade); // Pula o mapeamento automático de responsavelEspecialidade
            mapper.skip(ConsultaDTO::setResponsavelCRM); // Pula o mapeamento automático de responsavelCRM

            // Mapeia explicitamente createdAt e updatedAt
            mapper.map(ConsultaRegistroEntity::getCreatedAt, ConsultaDTO::setCreatedAt);
            mapper.map(ConsultaRegistroEntity::getUpdatedAt, ConsultaDTO::setUpdatedAt);
        });

        // Adicione outras configurações de TypeMap para outros pares problemáticos aqui se necessário

        return modelMapper;
    }
}