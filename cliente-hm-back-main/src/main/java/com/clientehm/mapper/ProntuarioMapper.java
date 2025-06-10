package com.clientehm.mapper;

import com.clientehm.entity.ProntuarioEntity;
import com.clientehm.model.ProntuarioDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class ProntuarioMapper {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PacienteMapper pacienteMapper;

    @Autowired
    private MedicoMapper medicoMapper;

    @Autowired
    private AdministradorMapper administradorMapper;

    @Autowired
    private ConsultaMapper consultaMapper;

    @Autowired
    private ExameMapper exameMapper;

    @Autowired
    private ProcedimentoMapper procedimentoMapper;

    @Autowired
    private EncaminhamentoMapper encaminhamentoMapper;

    public ProntuarioDTO toBasicDTO(ProntuarioEntity entity) {
        if (entity == null) return null;

        ProntuarioDTO dto = new ProntuarioDTO();
        dto.setId(entity.getId());
        dto.setNumeroProntuario(entity.getNumeroProntuario());
        // Removido: dto.setDataUltimaAtualizacao(entity.getDataUltimaAtualizacao());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt()); // Usar updatedAt como a data da última atualização

        if (entity.getPaciente() != null) {
            dto.setPaciente(pacienteMapper.toDTO(entity.getPaciente()));
        }
        if (entity.getMedicoResponsavel() != null) {
            dto.setMedicoResponsavel(medicoMapper.toMedicoBasicDTO(entity.getMedicoResponsavel()));
        }
        if (entity.getAdministradorCriador() != null) {
            dto.setAdministradorCriador(administradorMapper.toAdminBasicDTO(entity.getAdministradorCriador()));
        }
        return dto;
    }

    public ProntuarioDTO toDetailedDTO(ProntuarioEntity entity) {
        if (entity == null) return null;

        ProntuarioDTO dto = toBasicDTO(entity);

        if (entity.getConsultas() != null) {
            dto.setConsultas(entity.getConsultas().stream()
                    .map(consultaMapper::toDTO)
                    .collect(Collectors.toList()));
        }
        if (entity.getExamesRegistrados() != null) {
            dto.setExamesRegistrados(entity.getExamesRegistrados().stream()
                    .map(exameMapper::toDTO)
                    .collect(Collectors.toList()));
        }
        if (entity.getProcedimentosRegistrados() != null) {
            dto.setProcedimentosRegistrados(entity.getProcedimentosRegistrados().stream()
                    .map(procedimentoMapper::toDTO)
                    .collect(Collectors.toList()));
        }
        if (entity.getEncaminhamentosRegistrados() != null) {
            dto.setEncaminhamentosRegistrados(entity.getEncaminhamentosRegistrados().stream()
                    .map(encaminhamentoMapper::toDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public Page<ProntuarioDTO> toBasicDTOPage(Page<ProntuarioEntity> prontuariosPage) {
        return prontuariosPage.map(this::toBasicDTO);
    }
}