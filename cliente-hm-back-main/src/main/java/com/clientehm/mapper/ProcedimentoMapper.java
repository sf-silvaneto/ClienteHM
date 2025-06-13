package com.clientehm.mapper;

import com.clientehm.entity.MedicoEntity;
import com.clientehm.entity.ProcedimentoRegistroEntity;
import com.clientehm.model.CriarProcedimentoRequestDTO;
import com.clientehm.model.AtualizarProcedimentoRequestDTO;
import com.clientehm.model.ProcedimentoRegistroDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ProcedimentoMapper {

    @Autowired
    private ModelMapper modelMapper;

    public ProcedimentoRegistroEntity toEntity(CriarProcedimentoRequestDTO createDTO) {
        if (createDTO == null) {
            return null;
        }
        ProcedimentoRegistroEntity entity = new ProcedimentoRegistroEntity();
        entity.setDescricaoProcedimento(createDTO.getDescricaoProcedimento());
        entity.setRelatorioProcedimento(createDTO.getRelatorioProcedimento());
        entity.setDataProcedimento(createDTO.getDataProcedimento());
        return entity;
    }

    public ProcedimentoRegistroDTO toDTO(ProcedimentoRegistroEntity entity) {
        if (entity == null) {
            return null;
        }
        ProcedimentoRegistroDTO dto = modelMapper.map(entity, ProcedimentoRegistroDTO.class);

        if (entity.getProntuario() != null) {
            dto.setProntuarioId(entity.getProntuario().getId());
        }
        if (entity.getMedicoExecutor() != null) {
            dto.setMedicoExecutorId(entity.getMedicoExecutor().getId());
            dto.setMedicoExecutorNome(entity.getMedicoExecutor().getNomeCompleto());
            dto.setMedicoExecutorEspecialidade(entity.getMedicoExecutor().getEspecialidade());
            dto.setMedicoExecutorCRM(entity.getMedicoExecutor().getCrm());
        }
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setDataProcedimento(entity.getDataProcedimento());
        return dto;
    }

    public void updateEntityFromDTO(AtualizarProcedimentoRequestDTO updateDTO, ProcedimentoRegistroEntity entity, MedicoEntity medicoExecutor) {
        if (updateDTO == null || entity == null) {
            return;
        }

        if (StringUtils.hasText(updateDTO.getDescricaoProcedimento())) {
            entity.setDescricaoProcedimento(updateDTO.getDescricaoProcedimento());
        }
        if (updateDTO.getRelatorioProcedimento() != null) {
            entity.setRelatorioProcedimento(StringUtils.hasText(updateDTO.getRelatorioProcedimento()) ? updateDTO.getRelatorioProcedimento().trim() : null);
        }
        if (updateDTO.getDataProcedimento() != null) {
            entity.setDataProcedimento(updateDTO.getDataProcedimento());
        }

        if (medicoExecutor != null) {
            entity.setMedicoExecutor(medicoExecutor);
        } else {
        }
    }
}