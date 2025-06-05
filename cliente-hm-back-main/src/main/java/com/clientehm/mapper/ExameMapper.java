package com.clientehm.mapper;

import com.clientehm.entity.ExameRegistroEntity;
import com.clientehm.entity.MedicoEntity;
import com.clientehm.entity.AdministradorEntity; // <<<--- ADICIONE ESTE IMPORT
import com.clientehm.model.CriarExameRequestDTO;
import com.clientehm.model.AtualizarExameRequestDTO;
import com.clientehm.model.ExameRegistroDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ExameMapper {

    @Autowired
    private ModelMapper modelMapper;

    public ExameRegistroEntity toEntity(CriarExameRequestDTO dto) {
        ExameRegistroEntity entity = new ExameRegistroEntity();
        entity.setNome(dto.getNome());
        entity.setDataExame(dto.getData()); // DTO de request usa 'data'
        entity.setResultado(dto.getResultado());
        entity.setObservacoes(dto.getObservacoes());
        // MedicoResponsavelExame e Prontuario são definidos no serviço
        return entity;
    }

    public ExameRegistroDTO toDTO(ExameRegistroEntity entity) {
        if (entity == null) return null;
        ExameRegistroDTO dto = modelMapper.map(entity, ExameRegistroDTO.class);
        if (entity.getProntuario() != null) {
            dto.setProntuarioId(entity.getProntuario().getId());
        }
        if (entity.getMedicoResponsavelExame() != null) {
            dto.setMedicoResponsavelExameId(entity.getMedicoResponsavelExame().getId());
            dto.setMedicoResponsavelExameNome(entity.getMedicoResponsavelExame().getNomeCompleto());
        }
        // O nomeResponsavelDisplay é preenchido pela entidade, que foi definida no serviço
        dto.setNomeResponsavelDisplay(entity.getNomeResponsavelDisplay());
        return dto;
    }

    /**
     * Atualiza uma ExameRegistroEntity com dados de um AtualizarExameRequestDTO.
     * @param dto DTO com os dados para atualizar.
     * @param entity Entidade a ser atualizada.
     * @param medicoResponsavel O MedicoEntity responsável pelo exame (pode ser null).
     * @param adminLogado O AdministradorEntity logado que está realizando a alteração (usado se medicoResponsavel for null).
     */
    public void updateEntityFromDTO(AtualizarExameRequestDTO dto, ExameRegistroEntity entity,
                                    MedicoEntity medicoResponsavel, AdministradorEntity adminLogado) { // <<<--- TIPO DO PARÂMETRO CORRIGIDO
        if (StringUtils.hasText(dto.getNome())) entity.setNome(dto.getNome());
        if (dto.getData() != null) entity.setDataExame(dto.getData()); // DTO de request usa 'data'
        if (StringUtils.hasText(dto.getResultado())) entity.setResultado(dto.getResultado());
        // Permite limpar as observações se uma string vazia for passada
        if (dto.getObservacoes() != null) {
            entity.setObservacoes(StringUtils.hasText(dto.getObservacoes()) ? dto.getObservacoes().trim() : null);
        }

        // Define quem é o responsável pelo registro e o nome para exibição
        if (medicoResponsavel != null) {
            entity.setMedicoResponsavelExame(medicoResponsavel);
            entity.setNomeResponsavelDisplay(medicoResponsavel.getNomeCompleto());
        } else if (adminLogado != null) { // Se não há médico específico, o admin que editou assume
            entity.setMedicoResponsavelExame(null);
            entity.setNomeResponsavelDisplay(adminLogado.getNome()); // <<<--- CORRETO: AdministradorEntity tem getNome()
        } else {
            // Se ambos forem nulos (cenário improvável para uma atualização por um usuário logado),
            // pode-se manter o nomeResponsavelDisplay existente ou definir como null/padrão.
            // No contexto atual, espera-se que adminLogado esteja sempre presente se medicoResponsavel não estiver.
            // Se medicoResponsavelExameId não for fornecido no DTO e o exame já tinha um médico,
            // e um admin está editando, o médico anterior será removido e o admin se torna o display.
            entity.setMedicoResponsavelExame(null); // Garante que não haja médico associado se não for fornecido
            entity.setNomeResponsavelDisplay(adminLogado != null ? adminLogado.getNome() : "Sistema"); // Fallback, mas adminLogado deve existir
        }
    }
}