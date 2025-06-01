package com.clientehm.service;

import com.clientehm.entity.PacienteEntity;
import com.clientehm.entity.Endereco;
import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.exception.CpfAlreadyExistsException;
import com.clientehm.exception.EmailAlreadyExistsException;
import com.clientehm.model.PacienteCreateDTO;
import com.clientehm.model.PacienteDTO;
import com.clientehm.model.PacienteUpdateDTO;
import com.clientehm.repository.PacienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class PacienteService {

    private static final Logger logger = LoggerFactory.getLogger(PacienteService.class);

    @Autowired
    private PacienteRepository pacienteRepository;

    private PacienteDTO convertToDTO(PacienteEntity entity) {
        if (entity == null) return null;
        PacienteDTO dto = new PacienteDTO();
        BeanUtils.copyProperties(entity, dto);

        if (entity.getGenero() != null) {
            dto.setGenero(entity.getGenero().name());
        }
        if (entity.getRacaCor() != null) {
            dto.setRacaCor(entity.getRacaCor().name());
        } else {
            dto.setRacaCor(null);
        }
        if (entity.getTipoSanguineo() != null) {
            dto.setTipoSanguineo(entity.getTipoSanguineo().name());
        } else {
            dto.setTipoSanguineo(null);
        }

        if (entity.getEndereco() != null) {
            com.clientehm.model.EnderecoDTO enderecoDTO = new com.clientehm.model.EnderecoDTO();
            BeanUtils.copyProperties(entity.getEndereco(), enderecoDTO);
            dto.setEndereco(enderecoDTO);
        }

        return dto;
    }

    private void mapCreateDTOToEntity(PacienteCreateDTO dto, PacienteEntity entity) {
        entity.setNome(dto.getNome());
        entity.setDataNascimento(dto.getDataNascimento());
        entity.setCpf(dto.getCpf());
        entity.setRg(dto.getRg());
        try {
            if (StringUtils.hasText(dto.getGenero())) {
                entity.setGenero(PacienteEntity.Genero.valueOf(dto.getGenero().toUpperCase()));
            }
            if (StringUtils.hasText(dto.getRacaCor())) {
                entity.setRacaCor(PacienteEntity.RacaCor.valueOf(dto.getRacaCor().toUpperCase()));
            }
            if (StringUtils.hasText(dto.getTipoSanguineo())) {
                entity.setTipoSanguineo(PacienteEntity.TipoSanguineo.valueOf(dto.getTipoSanguineo().toUpperCase()));
            }
        } catch (IllegalArgumentException e) {
            logger.error("Valor de Enum inválido fornecido na criação: {}", e.getMessage());
            throw new IllegalArgumentException("Valor inválido para Gênero, Raça/Cor ou Tipo Sanguíneo: " + e.getMessage());
        }
        entity.setTelefone(dto.getTelefone());
        entity.setEmail(dto.getEmail());
        entity.setNomeMae(dto.getNomeMae());
        entity.setNomePai(dto.getNomePai());
        entity.setDataEntrada(dto.getDataEntrada() != null ? dto.getDataEntrada() : LocalDate.now());
        entity.setCartaoSus(dto.getCartaoSus());
        entity.setNacionalidade(dto.getNacionalidade());
        entity.setOcupacao(dto.getOcupacao());

        entity.setAlergiasDeclaradas(dto.getAlergiasDeclaradas());
        entity.setComorbidadesDeclaradas(dto.getComorbidadesDeclaradas());
        entity.setMedicamentosContinuos(dto.getMedicamentosContinuos());

        if (dto.getEndereco() != null) {
            Endereco endereco = new Endereco();
            BeanUtils.copyProperties(dto.getEndereco(), endereco);
            entity.setEndereco(endereco);
        }
    }

    private void mapUpdateDTOToEntity(PacienteUpdateDTO dto, PacienteEntity entity) {
        if (StringUtils.hasText(dto.getNome())) entity.setNome(dto.getNome());
        if (dto.getDataNascimento() != null) entity.setDataNascimento(dto.getDataNascimento());
        if (StringUtils.hasText(dto.getRg())) entity.setRg(dto.getRg());

        if (dto.getGenero() != null) {
            if (StringUtils.hasText(dto.getGenero())) {
                try {
                    entity.setGenero(PacienteEntity.Genero.valueOf(dto.getGenero().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Gênero inválido: " + dto.getGenero());
                }
            }
        }

        if (StringUtils.hasText(dto.getTelefone())) entity.setTelefone(dto.getTelefone());
        if (StringUtils.hasText(dto.getEmail())) {
            Optional<PacienteEntity> pacienteComEmail = pacienteRepository.findByEmail(dto.getEmail());
            if (pacienteComEmail.isPresent() && !pacienteComEmail.get().getId().equals(entity.getId())) {
                throw new EmailAlreadyExistsException("Email " + dto.getEmail() + " já cadastrado para outro paciente.");
            }
            entity.setEmail(dto.getEmail());
        }
        if (StringUtils.hasText(dto.getNomeMae())) entity.setNomeMae(dto.getNomeMae());
        if (dto.getNomePai() != null) entity.setNomePai(StringUtils.hasText(dto.getNomePai()) ? dto.getNomePai() : null);
        if (dto.getDataEntrada() != null) entity.setDataEntrada(dto.getDataEntrada());
        if (dto.getCartaoSus() != null) entity.setCartaoSus(StringUtils.hasText(dto.getCartaoSus()) ? dto.getCartaoSus() : null);

        if (dto.getRacaCor() != null) {
            if (StringUtils.hasText(dto.getRacaCor())) {
                try {
                    entity.setRacaCor(PacienteEntity.RacaCor.valueOf(dto.getRacaCor().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Raça/Cor inválida: " + dto.getRacaCor());
                }
            } else {
                entity.setRacaCor(null);
            }
        }

        if (dto.getTipoSanguineo() != null) {
            if (StringUtils.hasText(dto.getTipoSanguineo())) {
                try {
                    entity.setTipoSanguineo(PacienteEntity.TipoSanguineo.valueOf(dto.getTipoSanguineo().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Tipo Sanguíneo inválido: " + dto.getTipoSanguineo());
                }
            } else {
                entity.setTipoSanguineo(null);
            }
        }

        if (dto.getNacionalidade() != null) entity.setNacionalidade(StringUtils.hasText(dto.getNacionalidade()) ? dto.getNacionalidade() : null);
        if (dto.getOcupacao() != null) entity.setOcupacao(StringUtils.hasText(dto.getOcupacao()) ? dto.getOcupacao() : null);

        if (dto.getAlergiasDeclaradas() != null) {
            entity.setAlergiasDeclaradas(StringUtils.hasText(dto.getAlergiasDeclaradas()) ? dto.getAlergiasDeclaradas().trim() : null);
        }
        if (dto.getComorbidadesDeclaradas() != null) {
            entity.setComorbidadesDeclaradas(StringUtils.hasText(dto.getComorbidadesDeclaradas()) ? dto.getComorbidadesDeclaradas().trim() : null);
        }
        if (dto.getMedicamentosContinuos() != null) {
            entity.setMedicamentosContinuos(StringUtils.hasText(dto.getMedicamentosContinuos()) ? dto.getMedicamentosContinuos().trim() : null);
        }

        if (dto.getEndereco() != null) {
            Endereco endereco = entity.getEndereco() != null ? entity.getEndereco() : new Endereco();
            if(StringUtils.hasText(dto.getEndereco().getLogradouro())) endereco.setLogradouro(dto.getEndereco().getLogradouro());
            if(StringUtils.hasText(dto.getEndereco().getNumero())) endereco.setNumero(dto.getEndereco().getNumero());
            endereco.setComplemento(dto.getEndereco().getComplemento());
            if(StringUtils.hasText(dto.getEndereco().getBairro())) endereco.setBairro(dto.getEndereco().getBairro());
            if(StringUtils.hasText(dto.getEndereco().getCidade())) endereco.setCidade(dto.getEndereco().getCidade());
            if(StringUtils.hasText(dto.getEndereco().getEstado())) endereco.setEstado(dto.getEndereco().getEstado());
            if(StringUtils.hasText(dto.getEndereco().getCep())) endereco.setCep(dto.getEndereco().getCep());
            entity.setEndereco(endereco);
        }
    }

    @Transactional
    public PacienteDTO criarPaciente(PacienteCreateDTO pacienteCreateDTO) {
        logger.info("SERVICE: Tentando criar paciente com CPF: {}", pacienteCreateDTO.getCpf());
        pacienteRepository.findByCpf(pacienteCreateDTO.getCpf()).ifPresent(p -> {
            throw new CpfAlreadyExistsException("CPF " + pacienteCreateDTO.getCpf() + " já cadastrado.");
        });
        pacienteRepository.findByEmail(pacienteCreateDTO.getEmail()).ifPresent(p -> {
            throw new EmailAlreadyExistsException("Email " + pacienteCreateDTO.getEmail() + " já cadastrado.");
        });

        PacienteEntity pacienteEntity = new PacienteEntity();
        mapCreateDTOToEntity(pacienteCreateDTO, pacienteEntity); // Chama o método atualizado

        PacienteEntity pacienteSalvo = pacienteRepository.save(pacienteEntity);
        logger.info("SERVICE: Paciente criado com ID: {}", pacienteSalvo.getId());
        return convertToDTO(pacienteSalvo); // Chama o método atualizado
    }

    @Transactional(readOnly = true)
    public Page<PacienteDTO> buscarTodosPacientes(Pageable pageable, String nome, String cpf) {
        logger.info("SERVICE: Buscando pacientes. Filtros: nome='{}', cpf='{}'", nome, cpf);
        Page<PacienteEntity> pacientesPage;
        if (StringUtils.hasText(cpf)) {
            logger.info("SERVICE: Filtrando por CPF que começa com: {}", cpf);
            pacientesPage = pacienteRepository.findByCpfStartingWith(cpf, pageable);
        } else if (StringUtils.hasText(nome)) {
            logger.info("SERVICE: Filtrando por nome contendo: {}", nome);
            pacientesPage = pacienteRepository.findByNomeContainingIgnoreCase(nome, pageable);
        } else {
            logger.info("SERVICE: Buscando todos os pacientes (sem filtros de nome/cpf específicos).");
            pacientesPage = pacienteRepository.findAll(pageable);
        }
        return pacientesPage.map(this::convertToDTO); // Chama o método atualizado
    }

    @Transactional(readOnly = true)
    public PacienteDTO buscarPacientePorId(Long id) {
        logger.info("SERVICE: Buscando paciente com ID: {}", id);
        PacienteEntity pacienteEntity = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com ID: " + id));
        return convertToDTO(pacienteEntity); // Chama o método atualizado
    }

    @Transactional
    public PacienteDTO atualizarPaciente(Long id, PacienteUpdateDTO pacienteUpdateDTO) {
        logger.info("SERVICE: Atualizando paciente com ID: {}", id);
        PacienteEntity pacienteEntity = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com ID: " + id));

        mapUpdateDTOToEntity(pacienteUpdateDTO, pacienteEntity); // Chama o método atualizado

        PacienteEntity pacienteAtualizado = pacienteRepository.save(pacienteEntity);
        logger.info("SERVICE: Paciente atualizado com ID: {}", pacienteAtualizado.getId());
        return convertToDTO(pacienteAtualizado); // Chama o método atualizado
    }

    @Transactional
    public void deletarPaciente(Long id) {
        logger.info("SERVICE: Deletando paciente com ID: {}", id);
        if (!pacienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Paciente não encontrado com ID: " + id);
        }
        pacienteRepository.deleteById(id);
        logger.info("SERVICE: Paciente deletado com ID: {}", id);
    }
}