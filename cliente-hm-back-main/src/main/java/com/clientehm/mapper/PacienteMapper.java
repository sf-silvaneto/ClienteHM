package com.clientehm.mapper;

import com.clientehm.entity.AlergiaEntity;
import com.clientehm.entity.ComorbidadeEntity;
import com.clientehm.entity.ContatoEntity;
import com.clientehm.entity.EnderecoEntity;
import com.clientehm.entity.MedicamentoContinuoEntity;
import com.clientehm.entity.PacienteEntity;
import com.clientehm.model.AlergiaDTO;
import com.clientehm.model.ComorbidadeDTO;
import com.clientehm.model.EnderecoUpdateDTO;
import com.clientehm.model.MedicamentoContinuoDTO;
import com.clientehm.model.PacienteCreateDTO;
import com.clientehm.model.PacienteDTO;
import com.clientehm.model.PacienteUpdateDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

        this.modelMapper.createTypeMap(AlergiaEntity.class, AlergiaDTO.class);
        this.modelMapper.createTypeMap(ComorbidadeEntity.class, ComorbidadeDTO.class);
        this.modelMapper.createTypeMap(MedicamentoContinuoEntity.class, MedicamentoContinuoDTO.class);
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

        if (pacienteEntity.getAlergias() != null) {
            pacienteDTO.setAlergias(pacienteEntity.getAlergias().stream()
                    .map(alergiaEntity -> modelMapper.map(alergiaEntity, AlergiaDTO.class))
                    .collect(Collectors.toList()));
        } else {
            pacienteDTO.setAlergias(Collections.emptyList());
        }

        if (pacienteEntity.getComorbidades() != null) {
            pacienteDTO.setComorbidades(pacienteEntity.getComorbidades().stream()
                    .map(comorbidadeEntity -> modelMapper.map(comorbidadeEntity, ComorbidadeDTO.class))
                    .collect(Collectors.toList()));
        } else {
            pacienteDTO.setComorbidades(Collections.emptyList());
        }

        if (pacienteEntity.getMedicamentosContinuos() != null) {
            pacienteDTO.setMedicamentosContinuos(pacienteEntity.getMedicamentosContinuos().stream()
                    .map(medicamentoEntity -> modelMapper.map(medicamentoEntity, MedicamentoContinuoDTO.class))
                    .collect(Collectors.toList()));
        } else {
            pacienteDTO.setMedicamentosContinuos(Collections.emptyList());
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

        if (createDTO.getAlergias() != null) {
            pacienteEntity.setAlergias(createDTO.getAlergias().stream()
                    .map(alergiaDTO -> {
                        AlergiaEntity alergiaEntity = modelMapper.map(alergiaDTO, AlergiaEntity.class);
                        alergiaEntity.setPaciente(pacienteEntity);
                        return alergiaEntity;
                    }).collect(Collectors.toList()));
        }

        if (createDTO.getComorbidades() != null) {
            pacienteEntity.setComorbidades(createDTO.getComorbidades().stream()
                    .map(comorbidadeDTO -> {
                        ComorbidadeEntity comorbidadeEntity = modelMapper.map(comorbidadeDTO, ComorbidadeEntity.class);
                        comorbidadeEntity.setPaciente(pacienteEntity);
                        return comorbidadeEntity;
                    }).collect(Collectors.toList()));
        }

        if (createDTO.getMedicamentosContinuos() != null) {
            pacienteEntity.setMedicamentosContinuos(createDTO.getMedicamentosContinuos().stream()
                    .map(medicamentoDTO -> {
                        MedicamentoContinuoEntity medicamentoEntity = modelMapper.map(medicamentoDTO, MedicamentoContinuoEntity.class);
                        medicamentoEntity.setPaciente(pacienteEntity);
                        return medicamentoEntity;
                    }).collect(Collectors.toList()));
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

        updateCollection(updateDTO.getAlergias(), pacienteEntity.getAlergias(), pacienteEntity, AlergiaEntity.class);
        updateCollection(updateDTO.getComorbidades(), pacienteEntity.getComorbidades(), pacienteEntity, ComorbidadeEntity.class);
        updateCollection(updateDTO.getMedicamentosContinuos(), pacienteEntity.getMedicamentosContinuos(), pacienteEntity, MedicamentoContinuoEntity.class);
    }

    private <D, E> void updateCollection(List<D> dtoList, List<E> entityList, PacienteEntity pacienteEntity, Class<E> entityClass) {
        if (dtoList == null) {
            entityList.clear();
            return;
        }

        entityList.removeIf(entity -> {
            Long entityId = (Long) getEntityId(entity);
            if (entityId == null) return false;
            return dtoList.stream().noneMatch(dto -> {
                Long dtoId = (Long) getDtoId(dto);
                return dtoId != null && dtoId.equals(entityId);
            });
        });

        for (D dto : dtoList) {
            Long dtoId = (Long) getDtoId(dto);
            E existingEntity = null;
            if (dtoId != null) {
                existingEntity = entityList.stream()
                        .filter(entity -> dtoId.equals(getEntityId(entity)))
                        .findFirst()
                        .orElse(null);
            }

            if (existingEntity != null) {
                modelMapper.map(dto, existingEntity);
            } else {
                E newEntity = modelMapper.map(dto, entityClass);
                setPatientToEntity(newEntity, pacienteEntity);
                entityList.add(newEntity);
            }
        }
    }

    private Object getEntityId(Object entity) {
        if (entity instanceof AlergiaEntity) return ((AlergiaEntity) entity).getId();
        if (entity instanceof ComorbidadeEntity) return ((ComorbidadeEntity) entity).getId();
        if (entity instanceof MedicamentoContinuoEntity) return ((MedicamentoContinuoEntity) entity).getId();
        return null;
    }

    private Object getDtoId(Object dto) {
        if (dto instanceof AlergiaDTO) return ((AlergiaDTO) dto).getId();
        if (dto instanceof ComorbidadeDTO) return ((ComorbidadeDTO) dto).getId();
        if (dto instanceof MedicamentoContinuoDTO) return ((MedicamentoContinuoDTO) dto).getId();
        return null;
    }

    private void setPatientToEntity(Object entity, PacienteEntity paciente) {
        if (entity instanceof AlergiaEntity) ((AlergiaEntity) entity).setPaciente(paciente);
        if (entity instanceof ComorbidadeEntity) ((ComorbidadeEntity) entity).setPaciente(paciente);
        if (entity instanceof MedicamentoContinuoEntity) ((MedicamentoContinuoEntity) entity).setPaciente(paciente);
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