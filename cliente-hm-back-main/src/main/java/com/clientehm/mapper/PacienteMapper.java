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

        // Configuração para mapear de PacienteEntity -> PacienteDTO (campos de ContatoEntity)
        this.modelMapper.addMappings(new PropertyMap<PacienteEntity, PacienteDTO>() {
            @Override
            protected void configure() {
                map().setTelefone(source.getContato().getTelefone());
                map().setEmail(source.getContato().getEmail());
            }
        });
    }

    /**
     * Converte PacienteEntity para PacienteDTO.
     */
    public PacienteDTO toDTO(PacienteEntity pacienteEntity) {
        if (pacienteEntity == null) {
            return null;
        }
        PacienteDTO pacienteDTO = modelMapper.map(pacienteEntity, PacienteDTO.class);

        // Mapeamento manual de Enums para String, se necessário e não coberto pelo ModelMapper
        if (pacienteEntity.getGenero() != null) {
            pacienteDTO.setGenero(pacienteEntity.getGenero().name());
        }
        if (pacienteEntity.getRacaCor() != null) {
            pacienteDTO.setRacaCor(pacienteEntity.getRacaCor().name());
        } else {
            pacienteDTO.setRacaCor(null); // Garante que seja nulo se a entidade for nula
        }
        if (pacienteEntity.getTipoSanguineo() != null) {
            pacienteDTO.setTipoSanguineo(pacienteEntity.getTipoSanguineo().name());
        } else {
            pacienteDTO.setTipoSanguineo(null); // Garante que seja nulo se a entidade for nula
        }

        // O ModelMapper deve lidar com EnderecoEntity -> EnderecoDTO se os nomes dos campos forem compatíveis.
        // Se ContatoEntity for null, telefone e email serão null no DTO devido ao PropertyMap e getters.
        if (pacienteEntity.getContato() == null) {
            pacienteDTO.setTelefone(null);
            pacienteDTO.setEmail(null);
        }

        return pacienteDTO;
    }

    /**
     * Converte PacienteCreateDTO para PacienteEntity.
     */
    public PacienteEntity toEntity(PacienteCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }
        // ModelMapper não lida bem com conversão de String para Enum diretamente na configuração global para todos os casos.
        // Fazemos o mapeamento básico e depois ajustamos Enums e objetos aninhados.
        PacienteEntity pacienteEntity = modelMapper.map(createDTO, PacienteEntity.class);

        // Mapear Enums
        setEnumValuesFromDTO(createDTO, pacienteEntity);

        // Mapear Endereco
        if (createDTO.getEndereco() != null) {
            EnderecoEntity enderecoEntity = modelMapper.map(createDTO.getEndereco(), EnderecoEntity.class);
            pacienteEntity.setEndereco(enderecoEntity);
        }

        // Mapear Contato
        if (StringUtils.hasText(createDTO.getTelefone()) || StringUtils.hasText(createDTO.getEmail())) {
            ContatoEntity contatoEntity = new ContatoEntity();
            contatoEntity.setTelefone(StringUtils.hasText(createDTO.getTelefone()) ? createDTO.getTelefone() : null);
            contatoEntity.setEmail(StringUtils.hasText(createDTO.getEmail()) ? createDTO.getEmail().trim().toLowerCase() : null);
            pacienteEntity.setContato(contatoEntity);
        }

        return pacienteEntity;
    }

    /**
     * Atualiza uma PacienteEntity com dados de um PacienteUpdateDTO.
     */
    public void updateEntityFromDTO(PacienteUpdateDTO updateDTO, PacienteEntity pacienteEntity) {
        if (updateDTO == null || pacienteEntity == null) {
            return;
        }

        // Nomes dos campos que o ModelMapper pode mapear diretamente (exceto enums, endereco, contato)
        modelMapper.map(updateDTO, pacienteEntity);

        // Mapeamento manual para campos que precisam de lógica especial (Enums, aninhados)
        if (StringUtils.hasText(updateDTO.getGenero())) {
            try {
                pacienteEntity.setGenero(PacienteEntity.Genero.valueOf(updateDTO.getGenero().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Gênero inválido fornecido para atualização: " + updateDTO.getGenero());
            }
        }
        if (updateDTO.getRacaCor() != null) { // Permite limpar o campo
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
        if (updateDTO.getTipoSanguineo() != null) { // Permite limpar o campo
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


        // Atualizar Endereco (se fornecido no DTO)
        if (updateDTO.getEndereco() != null) {
            EnderecoUpdateDTO enderecoUpdateDTO = updateDTO.getEndereco();
            EnderecoEntity enderecoEntity = pacienteEntity.getEndereco();
            if (enderecoEntity == null) { // Caso o paciente não tenha endereço e está sendo adicionado
                enderecoEntity = new EnderecoEntity();
                pacienteEntity.setEndereco(enderecoEntity);
            }
            // ModelMapper pode ser usado aqui também se os campos forem compatíveis
            // ou mapeamento manual campo a campo:
            if(enderecoUpdateDTO.getLogradouro() != null) enderecoEntity.setLogradouro(enderecoUpdateDTO.getLogradouro());
            if(enderecoUpdateDTO.getNumero() != null) enderecoEntity.setNumero(enderecoUpdateDTO.getNumero());
            if(enderecoUpdateDTO.getComplemento() != null) enderecoEntity.setComplemento(enderecoUpdateDTO.getComplemento());
            if(enderecoUpdateDTO.getBairro() != null) enderecoEntity.setBairro(enderecoUpdateDTO.getBairro());
            if(enderecoUpdateDTO.getCidade() != null) enderecoEntity.setCidade(enderecoUpdateDTO.getCidade());
            if(enderecoUpdateDTO.getEstado() != null) enderecoEntity.setEstado(enderecoUpdateDTO.getEstado());
            if(enderecoUpdateDTO.getCep() != null) enderecoEntity.setCep(enderecoUpdateDTO.getCep());
        }

        // Atualizar Contato (se fornecido no DTO)
        boolean contatoInfoPresentInDto = StringUtils.hasText(updateDTO.getTelefone()) || StringUtils.hasText(updateDTO.getEmail());
        ContatoEntity contatoEntity = pacienteEntity.getContato();

        if (contatoEntity == null && contatoInfoPresentInDto) {
            contatoEntity = new ContatoEntity();
            pacienteEntity.setContato(contatoEntity);
        }

        if (contatoEntity != null) {
            // Telefone: atualiza se explicitamente fornecido no DTO (mesmo que seja string vazia para limpar)
            if (updateDTO.getTelefone() != null) {
                contatoEntity.setTelefone(StringUtils.hasText(updateDTO.getTelefone()) ? updateDTO.getTelefone() : null);
            }
            // Email: atualiza se explicitamente fornecido no DTO (mesmo que seja string vazia para limpar)
            if (updateDTO.getEmail() != null) {
                // A verificação de unicidade do email deve ocorrer no serviço
                contatoEntity.setEmail(StringUtils.hasText(updateDTO.getEmail()) ? updateDTO.getEmail().trim().toLowerCase() : null);
            }
        }
        // Campos de histórico de saúde
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
            // Logar o erro ou lançar uma exceção mais específica de negócio se necessário
            throw new IllegalArgumentException("Valor inválido para Gênero, Raça/Cor ou Tipo Sanguíneo: " + e.getMessage(), e);
        }
    }

    /**
     * Converte uma Page de PacienteEntity para uma Page de PacienteDTO.
     */
    public Page<PacienteDTO> toDTOPage(Page<PacienteEntity> pacientesPage) {
        return pacientesPage.map(this::toDTO);
    }
}