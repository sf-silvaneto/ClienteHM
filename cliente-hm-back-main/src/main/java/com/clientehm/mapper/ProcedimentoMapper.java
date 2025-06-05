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

    /**
     * Converte CriarProcedimentoRequestDTO para ProcedimentoRegistroEntity.
     * A associação com ProntuarioEntity e MedicoExecutor será feita no serviço.
     */
    public ProcedimentoRegistroEntity toEntity(CriarProcedimentoRequestDTO createDTO) {
        if (createDTO == null) {
            return null;
        }
        // ModelMapper mapeará os campos com nomes correspondentes (dataProcedimento, descricaoProcedimento, relatorioProcedimento).
        // medicoExecutorId não será mapeado diretamente para a entidade MedicoEntity aqui.
        ProcedimentoRegistroEntity entity = new ProcedimentoRegistroEntity();
        entity.setDataProcedimento(createDTO.getDataProcedimento());
        entity.setDescricaoProcedimento(createDTO.getDescricaoProcedimento());
        entity.setRelatorioProcedimento(createDTO.getRelatorioProcedimento());
        // O medicoExecutor será buscado e associado no serviço usando createDTO.getMedicoExecutorId()
        return entity;
    }

    /**
     * Converte ProcedimentoRegistroEntity para ProcedimentoRegistroDTO.
     */
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
        }
        // O campo nomeResponsavelDisplay já deve estar preenchido na entidade pelo serviço.
        // dto.setNomeResponsavelDisplay(entity.getNomeResponsavelDisplay());
        // createdAt e updatedAt devem ser mapeados automaticamente se os nomes forem iguais.
        return dto;
    }

    /**
     * Atualiza uma ProcedimentoRegistroEntity com dados de um AtualizarProcedimentoRequestDTO.
     * @param updateDTO DTO com os dados para atualizar.
     * @param entity Entidade a ser atualizada.
     * @param medicoExecutor O MedicoEntity que executará o procedimento (buscado no serviço).
     */
    public void updateEntityFromDTO(AtualizarProcedimentoRequestDTO updateDTO, ProcedimentoRegistroEntity entity, MedicoEntity medicoExecutor) {
        if (updateDTO == null || entity == null) {
            return;
        }

        if (updateDTO.getDataProcedimento() != null) {
            entity.setDataProcedimento(updateDTO.getDataProcedimento());
        }
        if (StringUtils.hasText(updateDTO.getDescricaoProcedimento())) {
            entity.setDescricaoProcedimento(updateDTO.getDescricaoProcedimento());
        }
        // Permite limpar o relatório se uma string vazia for passada, ou atualizar se houver texto.
        if (updateDTO.getRelatorioProcedimento() != null) {
            entity.setRelatorioProcedimento(StringUtils.hasText(updateDTO.getRelatorioProcedimento()) ? updateDTO.getRelatorioProcedimento().trim() : null);
        }

        // O MedicoExecutor deve ser sempre fornecido para um procedimento, mesmo na atualização.
        // A lógica de buscar o MedicoEntity baseado no medicoExecutorId do DTO
        // deve ocorrer no serviço antes de chamar este método de update.
        if (medicoExecutor != null) {
            entity.setMedicoExecutor(medicoExecutor);
            // O nomeResponsavelDisplay é atualizado com base no médico executor.
            entity.setNomeResponsavelDisplay(medicoExecutor.getNomeCompleto());
        } else {
            // Considerar lançar uma exceção se medicoExecutor for nulo,
            // pois procedimentos geralmente exigem um executor.
            // Isso dependerá das regras de negócio para atualização.
            // Por ora, se medicoExecutor não for fornecido (ou o ID no DTO for inválido),
            // o médico executor existente na entidade não será alterado aqui,
            // mas o nomeResponsavelDisplay pode ficar inconsistente se não for atualizado.
            // É melhor que o serviço garanta que medicoExecutor seja válido.
            // Se a intenção é DESASSOCIAR um médico (o que não é comum para executor),
            // a lógica seria diferente (ex: entity.setMedicoExecutor(null); entity.setNomeResponsavelDisplay(adminLogado.getNome()); )
        }
    }
}