package com.clientehm.mapper;

import com.clientehm.entity.AdministradorEntity;
import com.clientehm.entity.ConsultaRegistroEntity;
import com.clientehm.entity.MedicoEntity;
import com.clientehm.entity.SinaisVitaisEntity; // Importar SinaisVitaisEntity
import com.clientehm.model.ConsultaDTO;
import com.clientehm.model.CriarConsultaRequestDTO;
import com.clientehm.model.AtualizarConsultaRequestDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ConsultaMapper {

    private final ModelMapper modelMapper; // Injetar o ModelMapper já configurado
    private final SinaisVitaisMapper sinaisVitaisMapper; // Injetar o novo mapper

    @Autowired
    public ConsultaMapper(ModelMapper modelMapper, SinaisVitaisMapper sinaisVitaisMapper) {
        this.modelMapper = modelMapper;
        this.sinaisVitaisMapper = sinaisVitaisMapper; // Injeção do novo mapper
    }

    public ConsultaRegistroEntity toEntity(CriarConsultaRequestDTO dto) {
        if (dto == null) return null;
        ConsultaRegistroEntity entity = modelMapper.map(dto, ConsultaRegistroEntity.class);

        // Mapear e associar SinaisVitaisEntity se houver dados no DTO
        if (dto.getSinaisVitais() != null) {
            SinaisVitaisEntity sinaisVitaisEntity = sinaisVitaisMapper.toEntity(dto.getSinaisVitais());
            entity.setSinaisVitais(sinaisVitaisEntity);
        }
        return entity;
    }

    public ConsultaDTO toDTO(ConsultaRegistroEntity entity) {
        if (entity == null) return null;

        ConsultaDTO dto = modelMapper.map(entity, ConsultaDTO.class);

        // Mapear SinaisVitaisEntity para SinaisVitaisDTO
        if (entity.getSinaisVitais() != null) {
            dto.setSinaisVitais(sinaisVitaisMapper.toDTO(entity.getSinaisVitais()));
        }

        if (entity.getResponsavelMedico() != null) {
            dto.setTipoResponsavel("MEDICO");
            dto.setResponsavelId(entity.getResponsavelMedico().getId());
            dto.setResponsavelNomeCompleto(entity.getResponsavelMedico().getNomeCompleto());
            dto.setResponsavelEspecialidade(entity.getResponsavelMedico().getEspecialidade());
            dto.setResponsavelCRM(entity.getResponsavelMedico().getCrm());
        } else if (entity.getResponsavelAdmin() != null) {
            dto.setTipoResponsavel("ADMINISTRADOR");
            dto.setResponsavelId(entity.getResponsavelAdmin().getId());
            dto.setResponsavelNomeCompleto(entity.getResponsavelAdmin().getNome());
        } else {
            dto.setResponsavelNomeCompleto(entity.getNomeResponsavelDisplay());
        }

        return dto;
    }

    public void updateEntityFromDTO(AtualizarConsultaRequestDTO dto, ConsultaRegistroEntity entity, MedicoEntity medicoExecutor, AdministradorEntity adminLogado) {
        if (dto == null || entity == null) return;

        if (dto.getDataHoraConsulta() != null) entity.setDataHoraConsulta(dto.getDataHoraConsulta());
        if (StringUtils.hasText(dto.getMotivoConsulta())) entity.setMotivoConsulta(dto.getMotivoConsulta());
        if (StringUtils.hasText(dto.getQueixasPrincipais())) entity.setQueixasPrincipais(dto.getQueixasPrincipais());

        // Lidar com a atualização de SinaisVitais
        if (dto.getSinaisVitais() != null) {
            SinaisVitaisEntity sinaisVitaisEntity = entity.getSinaisVitais();
            if (sinaisVitaisEntity == null) {
                sinaisVitaisEntity = new SinaisVitaisEntity();
                entity.setSinaisVitais(sinaisVitaisEntity);
            }
            sinaisVitaisMapper.updateEntityFromDTO(dto.getSinaisVitais(), sinaisVitaisEntity);
        } else if (entity.getSinaisVitais() != null) {
            // Se o DTO não fornecer sinais vitais, mas a entidade tiver, podemos optar por limpá-los ou mantê-los
            // Neste caso, vamos manter os dados existentes se o DTO não fornecer, mas se precisar limpar, descomente a linha abaixo.
            // entity.setSinaisVitais(null);
        }


        if (dto.getExameFisico() != null) entity.setExameFisico(StringUtils.hasText(dto.getExameFisico()) ? dto.getExameFisico().trim() : null);
        if (dto.getHipoteseDiagnostica() != null) entity.setHipoteseDiagnostica(StringUtils.hasText(dto.getHipoteseDiagnostica()) ? dto.getHipoteseDiagnostica().trim() : null);
        if (dto.getCondutaPlanoTerapeutico() != null) entity.setCondutaPlanoTerapeutico(StringUtils.hasText(dto.getCondutaPlanoTerapeutico()) ? dto.getCondutaPlanoTerapeutico().trim() : null);
        if (dto.getDetalhesConsulta() != null) entity.setDetalhesConsulta(StringUtils.hasText(dto.getDetalhesConsulta()) ? dto.getDetalhesConsulta().trim() : null);
        if (dto.getObservacoesConsulta() != null) entity.setObservacoesConsulta(StringUtils.hasText(dto.getObservacoesConsulta()) ? dto.getObservacoesConsulta().trim() : null);

        if (medicoExecutor != null) {
            entity.setResponsavelMedico(medicoExecutor);
            entity.setResponsavelAdmin(null);
            entity.setNomeResponsavelDisplay(medicoExecutor.getNomeCompleto());
        } else if (adminLogado != null) {
            entity.setResponsavelMedico(null);
            entity.setResponsavelAdmin(adminLogado);
            entity.setNomeResponsavelDisplay(adminLogado.getNome());
        }
    }
}