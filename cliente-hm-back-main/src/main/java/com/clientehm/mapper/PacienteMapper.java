package com.clientehm.mapper;

import com.clientehm.entity.ContatoEntity;
import com.clientehm.entity.EnderecoEntity;
import com.clientehm.entity.PacienteEntity;
import com.clientehm.model.EnderecoCreateDTO;
import com.clientehm.model.EnderecoDTO;
import com.clientehm.model.EnderecoUpdateDTO;
import com.clientehm.model.PacienteCreateDTO;
import com.clientehm.model.PacienteDTO;
import com.clientehm.model.PacienteUpdateDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PacienteMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public PacienteMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        this.modelMapper.addMappings(new PropertyMap<PacienteEntity, PacienteDTO>() {
            @Override
            protected void configure() {
                map().setTelefone(source.getContato().getTelefone());
                map().setEmail(source.getContato().getEmail());
            }
        });
    }

    public PacienteDTO toDTO(PacienteEntity pacienteEntity) {
        if (pacienteEntity == null) {
            return null;
        }
        PacienteDTO pacienteDTO = modelMapper.map(pacienteEntity, PacienteDTO.class);

        if (pacienteEntity.getGenero() != null) {
            pacienteDTO.setGenero(pacienteEntity.getGenero().name());
        }
        if (pacienteEntity.getRacaCor() != null) {
            pacienteDTO.setRacaCor(pacienteEntity.getRacaCor().name());
        } else {
            pacienteDTO.setRacaCor(null);
        }
        if (pacienteEntity.getTipoSanguineo() != null) {
            pacienteDTO.setTipoSanguineo(pacienteEntity.getTipoSanguineo().name());
        } else {
            pacienteDTO.setTipoSanguineo(null);
        }

        if (pacienteEntity.getContato() == null) {
            pacienteDTO.setTelefone(null);
            pacienteDTO.setEmail(null);
        }

        return pacienteDTO;
    }

    public PacienteEntity toEntity(PacienteCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }
        PacienteEntity pacienteEntity = modelMapper.map(createDTO, PacienteEntity.class);

        setEnumValuesFromDTO(createDTO, pacienteEntity);

        if (createDTO.getEndereco() != null) {
            EnderecoEntity enderecoEntity = modelMapper.map(createDTO.getEndereco(), EnderecoEntity.class);
            pacienteEntity.setEndereco(enderecoEntity);
        }

        if (StringUtils.hasText(createDTO.getTelefone()) || StringUtils.hasText(createDTO.getEmail())) {
            ContatoEntity contatoEntity = new ContatoEntity();
            contatoEntity.setTelefone(StringUtils.hasText(createDTO.getTelefone()) ? createDTO.getTelefone() : null);
            contatoEntity.setEmail(StringUtils.hasText(createDTO.getEmail()) ? createDTO.getEmail().trim().toLowerCase() : null);
            pacienteEntity.setContato(contatoEntity);
        }

        return pacienteEntity;
    }

    public void updateEntityFromDTO(PacienteUpdateDTO updateDTO, PacienteEntity pacienteEntity) {
        if (updateDTO == null || pacienteEntity == null) {
            return;
        }

        modelMapper.map(updateDTO, pacienteEntity);

        if (StringUtils.hasText(updateDTO.getGenero())) {
            try {
                pacienteEntity.setGenero(PacienteEntity.Genero.valueOf(updateDTO.getGenero().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Gênero inválido fornecido para atualização: " + updateDTO.getGenero());
            }
        }
        if (updateDTO.getRacaCor() != null) {
            if (StringUtils.hasText(updateDTO.getRacaCor())) {
                try {
                    pacienteEntity.setRacaCor(PacienteEntity.RacaCor.valueOf(updateDTO.getRacaCor().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Raça/Cor inválida fornecida para atualização: " + updateDTO.getRacaCor());
                }
            } else {
                pacienteEntity.setRacaCor(null);
            }
        }
        if (updateDTO.getTipoSanguineo() != null) {
            if (StringUtils.hasText(updateDTO.getTipoSanguineo())) {
                try {
                    pacienteEntity.setTipoSanguineo(PacienteEntity.TipoSanguineo.valueOf(updateDTO.getTipoSanguineo().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Tipo Sanguíneo inválido fornecido para atualização: " + updateDTO.getTipoSanguineo());
                }
            } else {
                pacienteEntity.setTipoSanguineo(null);
            }
        }

        if (updateDTO.getEndereco() != null) {
            EnderecoUpdateDTO enderecoUpdateDTO = updateDTO.getEndereco();
            EnderecoEntity enderecoEntity = pacienteEntity.getEndereco();
            if (enderecoEntity == null) {
                enderecoEntity = new EnderecoEntity();
                pacienteEntity.setEndereco(enderecoEntity);
            }
            if(enderecoUpdateDTO.getLogradouro() != null) enderecoEntity.setLogradouro(enderecoUpdateDTO.getLogradouro());
            if(enderecoUpdateDTO.getNumero() != null) enderecoEntity.setNumero(enderecoUpdateDTO.getNumero());
            if(enderecoUpdateDTO.getComplemento() != null) enderecoEntity.setComplemento(enderecoUpdateDTO.getComplemento());
            if(enderecoUpdateDTO.getBairro() != null) enderecoEntity.setBairro(enderecoUpdateDTO.getBairro());
            if(enderecoUpdateDTO.getCidade() != null) enderecoEntity.setCidade(enderecoUpdateDTO.getCidade());
            if(enderecoUpdateDTO.getEstado() != null) enderecoEntity.setEstado(enderecoUpdateDTO.getEstado());
            if(enderecoUpdateDTO.getCep() != null) enderecoEntity.setCep(enderecoUpdateDTO.getCep());
        }

        boolean contatoInfoPresentInDto = StringUtils.hasText(updateDTO.getTelefone()) || StringUtils.hasText(updateDTO.getEmail());
        ContatoEntity contatoEntity = pacienteEntity.getContato();

        if (contatoEntity == null && contatoInfoPresentInDto) {
            contatoEntity = new ContatoEntity();
            pacienteEntity.setContato(contatoEntity);
        }

        if (contatoEntity != null) {
            if (updateDTO.getTelefone() != null) {
                contatoEntity.setTelefone(StringUtils.hasText(updateDTO.getTelefone()) ? updateDTO.getTelefone() : null);
            }
            if (updateDTO.getEmail() != null) {
                contatoEntity.setEmail(StringUtils.hasText(updateDTO.getEmail()) ? updateDTO.getEmail().trim().toLowerCase() : null);
            }
        }
        if (updateDTO.getAlergiasDeclaradas() != null) pacienteEntity.setAlergiasDeclaradas(updateDTO.getAlergiasDeclaradas());
        if (updateDTO.getComorbidadesDeclaradas() != null) pacienteEntity.setComorbidadesDeclaradas(updateDTO.getComorbidadesDeclaradas());
        if (updateDTO.getMedicamentosContinuos() != null) pacienteEntity.setMedicamentosContinuos(updateDTO.getMedicamentosContinuos());
    }

    private void setEnumValuesFromDTO(PacienteCreateDTO dto, PacienteEntity entity) {
        try {
            if (StringUtils.hasText(dto.getGenero())) {
                entity.setGenero(PacienteEntity.Genero.valueOf(dto.getGenero().toUpperCase()));
            }
            if (StringUtils.hasText(dto.getRacaCor())) {
                entity.setRacaCor(PacienteEntity.RacaCor.valueOf(dto.getRacaCor().toUpperCase()));
            } else {
                entity.setRacaCor(null);
            }
            if (StringUtils.hasText(dto.getTipoSanguineo())) {
                entity.setTipoSanguineo(PacienteEntity.TipoSanguineo.valueOf(dto.getTipoSanguineo().toUpperCase()));
            } else {
                entity.setTipoSanguineo(null);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Valor inválido para Gênero, Raça/Cor ou Tipo Sanguíneo: " + e.getMessage(), e);
        }
    }
    public Page<PacienteDTO> toDTOPage(Page<PacienteEntity> pacientesPage) {
        return pacientesPage.map(this::toDTO);
    }
}