package com.clientehm.mapper;

import com.clientehm.entity.AdministradorEntity;
import com.clientehm.entity.ConsultaRegistroEntity;
import com.clientehm.entity.MedicoEntity;
import com.clientehm.model.ConsultaDTO;
import com.clientehm.model.CriarConsultaRequestDTO;
import com.clientehm.model.AtualizarConsultaRequestDTO;
import org.modelmapper.ModelMapper;
// TypeMap e @PostConstruct não são mais necessários aqui se a configuração for central
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

// import jakarta.annotation.PostConstruct; // REMOVER ESTE IMPORT SE AINDA EXISTIR

@Component
public class ConsultaMapper {

    private final ModelMapper modelMapper; // Injetar o ModelMapper já configurado

    @Autowired
    public ConsultaMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    // NENHUM MÉTODO @PostConstruct AQUI

    public ConsultaRegistroEntity toEntity(CriarConsultaRequestDTO dto) {
        if (dto == null) return null;
        return modelMapper.map(dto, ConsultaRegistroEntity.class);
    }

    public ConsultaDTO toDTO(ConsultaRegistroEntity entity) {
        if (entity == null) return null;

        // ModelMapper fará o mapeamento básico.
        // A configuração de skip para os campos do responsável e o mapeamento de
        // createdAt/updatedAt já foi feita no ModelMapperConfig.
        ConsultaDTO dto = modelMapper.map(entity, ConsultaDTO.class);

        // Preenchimento manual/lógico dos campos do responsável permanece aqui.
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

        // Os campos createdAt e updatedAt devem ter sido mapeados pela configuração no ModelMapperConfig.
        // Se ainda não estiverem, a configuração no ModelMapperConfig precisa ser verificada.
        // dto.setCreatedAt(entity.getCreatedAt()); // Pode não ser mais necessário aqui
        // dto.setUpdatedAt(entity.getUpdatedAt()); // Pode não ser mais necessário aqui

        return dto;
    }

    public void updateEntityFromDTO(AtualizarConsultaRequestDTO dto, ConsultaRegistroEntity entity, MedicoEntity medicoExecutor, AdministradorEntity adminLogado) {
        // ... (corpo do método permanece o mesmo de antes) ...
        if (dto == null || entity == null) return;

        if (dto.getDataHoraConsulta() != null) entity.setDataHoraConsulta(dto.getDataHoraConsulta());
        if (StringUtils.hasText(dto.getMotivoConsulta())) entity.setMotivoConsulta(dto.getMotivoConsulta());
        if (StringUtils.hasText(dto.getQueixasPrincipais())) entity.setQueixasPrincipais(dto.getQueixasPrincipais());
        if (dto.getPressaoArterial() != null) entity.setPressaoArterial(StringUtils.hasText(dto.getPressaoArterial()) ? dto.getPressaoArterial().trim() : null);
        if (dto.getTemperatura() != null) entity.setTemperatura(StringUtils.hasText(dto.getTemperatura()) ? dto.getTemperatura().trim() : null);
        if (dto.getFrequenciaCardiaca() != null) entity.setFrequenciaCardiaca(StringUtils.hasText(dto.getFrequenciaCardiaca()) ? dto.getFrequenciaCardiaca().trim() : null);
        if (dto.getSaturacao() != null) entity.setSaturacao(StringUtils.hasText(dto.getSaturacao()) ? dto.getSaturacao().trim() : null);
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