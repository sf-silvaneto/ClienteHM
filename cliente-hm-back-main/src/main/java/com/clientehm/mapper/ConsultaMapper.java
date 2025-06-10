package com.clientehm.mapper;

import com.clientehm.entity.AdministradorEntity; // Manter a importação se o adminLogado for usado em outros contextos do Mapper, mas não mais para a responsabilidade da consulta.
import com.clientehm.entity.ConsultaRegistroEntity;
import com.clientehm.entity.MedicoEntity;
import com.clientehm.entity.SinaisVitaisEntity;
import com.clientehm.model.ConsultaDTO;
import com.clientehm.model.CriarConsultaRequestDTO;
import com.clientehm.model.AtualizarConsultaRequestDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ConsultaMapper {

    private final ModelMapper modelMapper;
    private final SinaisVitaisMapper sinaisVitaisMapper;

    @Autowired
    public ConsultaMapper(ModelMapper modelMapper, SinaisVitaisMapper sinaisVitaisMapper) {
        this.modelMapper = modelMapper;
        this.sinaisVitaisMapper = sinaisVitaisMapper;
    }

    public ConsultaRegistroEntity toEntity(CriarConsultaRequestDTO dto) {
        if (dto == null) return null;
        ConsultaRegistroEntity entity = new ConsultaRegistroEntity();
        entity.setMotivoConsulta(dto.getMotivoConsulta());
        entity.setQueixasPrincipais(dto.getQueixasPrincipais());
        entity.setExameFisico(dto.getExameFisico());
        entity.setHipoteseDiagnostica(dto.getHipoteseDiagnostica());
        entity.setCondutaPlanoTerapeutico(dto.getCondutaPlanoTerapeutico());
        entity.setDetalhesConsulta(dto.getDetalhesConsulta());
        entity.setObservacoesConsulta(dto.getObservacoesConsulta());
        entity.setDataConsulta(dto.getDataConsulta());

        if (dto.getSinaisVitais() != null) {
            SinaisVitaisEntity sinaisVitaisEntity = sinaisVitaisMapper.toEntity(dto.getSinaisVitais());
            entity.setSinaisVitais(sinaisVitaisEntity);
        }
        return entity;
    }

    public ConsultaDTO toDTO(ConsultaRegistroEntity entity) {
        if (entity == null) return null;

        ConsultaDTO dto = modelMapper.map(entity, ConsultaDTO.class);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setDataConsulta(entity.getDataConsulta());

        if (entity.getSinaisVitais() != null) {
            dto.setSinaisVitais(sinaisVitaisMapper.toDTO(entity.getSinaisVitais()));
        }

        if (entity.getResponsavelMedico() != null) {
            // REMOVIDO: dto.setTipoResponsavel("MEDICO");
            dto.setResponsavelId(entity.getResponsavelMedico().getId());
            dto.setResponsavelNomeCompleto(entity.getResponsavelMedico().getNomeCompleto());
            dto.setResponsavelEspecialidade(entity.getResponsavelMedico().getEspecialidade());
            dto.setResponsavelCRM(entity.getResponsavelMedico().getCrm());
        }
        // REMOVIDO: else if (entity.getResponsavelAdmin() != null) {
        // REMOVIDO: dto.setTipoResponsavel("ADMINISTRADOR");
        // REMOVIDO: dto.setResponsavelId(entity.getResponsavelAdmin().getId());
        // REMOVIDO: dto.setResponsavelNomeCompleto(entity.getResponsavelAdmin().getNome());
        // REMOVIDO: }

        return dto;
    }

    // O adminLogado não será mais usado para determinar a responsabilidade da consulta, apenas o medicoExecutor.
    public void updateEntityFromDTO(AtualizarConsultaRequestDTO dto, ConsultaRegistroEntity entity, MedicoEntity medicoExecutor, AdministradorEntity adminLogado) { // Manter adminLogado para outros usos se houver, mas não para a atribuição de responsável da consulta.
        if (dto == null || entity == null) return;

        if (StringUtils.hasText(dto.getMotivoConsulta())) entity.setMotivoConsulta(dto.getMotivoConsulta());
        if (StringUtils.hasText(dto.getQueixasPrincipais())) entity.setQueixasPrincipais(dto.getQueixasPrincipais());
        if (dto.getDataConsulta() != null) entity.setDataConsulta(dto.getDataConsulta());

        if (dto.getSinaisVitais() != null) {
            SinaisVitaisEntity sinaisVitaisEntity = entity.getSinaisVitais();
            if (sinaisVitaisEntity == null) {
                sinaisVitaisEntity = new SinaisVitaisEntity();
                entity.setSinaisVitais(sinaisVitaisEntity);
            }
            sinaisVitaisMapper.updateEntityFromDTO(dto.getSinaisVitais(), sinaisVitaisEntity);
        } else if (entity.getSinaisVitais() != null) {
            // Lógica para lidar com a remoção de sinais vitais se o DTO for nulo e a entidade tiver
            // (Manter como está se o comportamento desejado é não remover sinais vitais se não forem fornecidos no DTO de update)
        }

        if (dto.getExameFisico() != null) entity.setExameFisico(StringUtils.hasText(dto.getExameFisico()) ? dto.getExameFisico().trim() : null);
        if (dto.getHipoteseDiagnostica() != null) entity.setHipoteseDiagnostica(StringUtils.hasText(dto.getHipoteseDiagnostica()) ? dto.getHipoteseDiagnostica().trim() : null);
        if (dto.getCondutaPlanoTerapeutico() != null) entity.setCondutaPlanoTerapeutico(StringUtils.hasText(dto.getCondutaPlanoTerapeutico()) ? dto.getCondutaPlanoTerapeutico().trim() : null);
        if (dto.getDetalhesConsulta() != null) entity.setDetalhesConsulta(StringUtils.hasText(dto.getDetalhesConsulta()) ? dto.getDetalhesConsulta().trim() : null);
        if (dto.getObservacoesConsulta() != null) entity.setObservacoesConsulta(StringUtils.hasText(dto.getObservacoesConsulta()) ? dto.getObservacoesConsulta().trim() : null);

        // A responsabilidade agora é sempre do médico.
        if (medicoExecutor != null) {
            entity.setResponsavelMedico(medicoExecutor);
            // REMOVIDO: entity.setResponsavelAdmin(null);
        }
        // REMOVIDO: else if (adminLogado != null) {
        // REMOVIDO: entity.setResponsavelMedico(null);
        // REMOVIDO: entity.setResponsavelAdmin(adminLogado);
        // REMOVIDO: }
    }
}