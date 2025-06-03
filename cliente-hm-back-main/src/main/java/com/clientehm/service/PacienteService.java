package com.clientehm.service;

import com.clientehm.entity.PacienteEntity;
import com.clientehm.entity.EnderecoEntity;
import com.clientehm.entity.ContatoEntity; // NOVO IMPORT
import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.exception.CpfAlreadyExistsException;
import com.clientehm.exception.EmailAlreadyExistsException;
import com.clientehm.model.PacienteCreateDTO;
import com.clientehm.model.PacienteDTO;
import com.clientehm.model.PacienteUpdateDTO;
import com.clientehm.model.EnderecoCreateDTO;
import com.clientehm.model.EnderecoDTO;
import com.clientehm.model.EnderecoUpdateDTO;
import com.clientehm.repository.PacienteRepository;
import com.clientehm.repository.EnderecoRepository;
import com.clientehm.repository.ContatoRepository; // NOVO IMPORT
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

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired // NOVO
    private ContatoRepository contatoRepository;

    private PacienteDTO convertToDTO(PacienteEntity entity) {
        if (entity == null) return null;
        PacienteDTO dto = new PacienteDTO();
        // Copia as propriedades, excluindo 'endereco' e 'contato' que agora precisam de mapeamento customizado
        BeanUtils.copyProperties(entity, dto, "endereco", "contato");

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

        // Converte EnderecoEntity para EnderecoDTO
        if (entity.getEndereco() != null) {
            EnderecoEntity enderecoEntity = entity.getEndereco();
            EnderecoDTO enderecoDTO = new EnderecoDTO();
            BeanUtils.copyProperties(enderecoEntity, enderecoDTO);
            dto.setEndereco(enderecoDTO);
        } else {
            dto.setEndereco(null);
        }

        // NOVO: Converte ContatoEntity para campos no PacienteDTO
        if (entity.getContato() != null) {
            dto.setTelefone(entity.getContato().getTelefone());
            dto.setEmail(entity.getContato().getEmail());
        } else {
            dto.setTelefone(null);
            dto.setEmail(null);
        }

        dto.setAlergiasDeclaradas(entity.getAlergiasDeclaradas());
        dto.setComorbidadesDeclaradas(entity.getComorbidadesDeclaradas());
        dto.setMedicamentosContinuos(entity.getMedicamentosContinuos());

        return dto;
    }

    private void mapCreateDTOToEntity(PacienteCreateDTO dto, PacienteEntity entity) {
        // Copia campos básicos de Paciente
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
            } else {
                entity.setRacaCor(null);
            }
            if (StringUtils.hasText(dto.getTipoSanguineo())) {
                entity.setTipoSanguineo(PacienteEntity.TipoSanguineo.valueOf(dto.getTipoSanguineo().toUpperCase()));
            } else {
                entity.setTipoSanguineo(null);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Valor de Enum inválido fornecido na criação: {}", e.getMessage());
            throw new IllegalArgumentException("Valor inválido para Gênero, Raça/Cor ou Tipo Sanguíneo: " + e.getMessage());
        }
        entity.setNomeMae(dto.getNomeMae());
        entity.setNomePai(dto.getNomePai());
        entity.setDataEntrada(dto.getDataEntrada() != null ? dto.getDataEntrada() : LocalDate.now());
        entity.setCartaoSus(dto.getCartaoSus());
        entity.setNacionalidade(dto.getNacionalidade());
        entity.setOcupacao(dto.getOcupacao());
        entity.setAlergiasDeclaradas(dto.getAlergiasDeclaradas());
        entity.setComorbidadesDeclaradas(dto.getComorbidadesDeclaradas());
        entity.setMedicamentosContinuos(dto.getMedicamentosContinuos());

        // Lida com Endereco
        if (dto.getEndereco() != null) {
            EnderecoCreateDTO enderecoCreateDTO = dto.getEndereco();
            EnderecoEntity enderecoEntity = new EnderecoEntity();
            BeanUtils.copyProperties(enderecoCreateDTO, enderecoEntity, "id");
            entity.setEndereco(enderecoEntity);
        }

        // NOVO: Lida com Contato
        if (StringUtils.hasText(dto.getTelefone()) || StringUtils.hasText(dto.getEmail())) {
            ContatoEntity contatoEntity = new ContatoEntity();
            contatoEntity.setTelefone(StringUtils.hasText(dto.getTelefone()) ? dto.getTelefone() : null);
            contatoEntity.setEmail(StringUtils.hasText(dto.getEmail()) ? dto.getEmail().trim().toLowerCase() : null);
            // Se ContatoEntity tivesse uma referência de volta:
            // contatoEntity.setPaciente(entity);
            entity.setContato(contatoEntity);
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

        // ... (outros campos de PacienteEntity) ...
        if (StringUtils.hasText(dto.getNomeMae())) entity.setNomeMae(dto.getNomeMae());
        // ... etc.

        // Lida com Atualização de Endereco (manter como estava)
        if (dto.getEndereco() != null) {
            // ... lógica de atualização de endereço ...
        }

        // NOVO: Lida com Atualização de Contato
        boolean contatoInfoPresentInDto = StringUtils.hasText(dto.getTelefone()) || StringUtils.hasText(dto.getEmail());
        ContatoEntity contatoEntity = entity.getContato();

        if (contatoEntity == null && contatoInfoPresentInDto) {
            contatoEntity = new ContatoEntity();
            entity.setContato(contatoEntity);
        }

        if (contatoEntity != null) {
            // Verifica se o telefone foi fornecido no DTO para atualização
            if (dto.getTelefone() != null) {
                String novoTelefone = StringUtils.hasText(dto.getTelefone()) ? dto.getTelefone() : null;
                if (!java.util.Objects.equals(contatoEntity.getTelefone(), novoTelefone)) {
                    contatoEntity.setTelefone(novoTelefone);
                }
            }
            // Verifica se o email foi fornecido no DTO para atualização
            if (dto.getEmail() != null) {
                String novoEmail = StringUtils.hasText(dto.getEmail()) ? dto.getEmail().trim().toLowerCase() : null;

                // Se o email está sendo alterado PARA UM VALOR NÃO NULO e é diferente do email atual no contato
                if (novoEmail != null && !novoEmail.equalsIgnoreCase(contatoEntity.getEmail())) {
                    Optional<ContatoEntity> contatoComEmail = contatoRepository.findByEmail(novoEmail);
                    if (contatoComEmail.isPresent() && !contatoComEmail.get().getId().equals(contatoEntity.getId())) {
                        throw new EmailAlreadyExistsException("Email " + novoEmail + " já cadastrado para outro contato.");
                    }
                }
                // Atualiza se o novo email (mesmo que nulo) for diferente do email existente.
                if (!java.util.Objects.equals(contatoEntity.getEmail(), novoEmail)) {
                    contatoEntity.setEmail(novoEmail);
                }
            }
        }
    }

    @Transactional
    public PacienteDTO criarPaciente(PacienteCreateDTO pacienteCreateDTO) {
        logger.info("SERVICE: Tentando criar paciente com CPF: {}", pacienteCreateDTO.getCpf());
        pacienteRepository.findByCpf(pacienteCreateDTO.getCpf()).ifPresent(p -> {
            throw new CpfAlreadyExistsException("CPF " + pacienteCreateDTO.getCpf() + " já cadastrado.");
        });

        // NOVO: Verificar unicidade do email no ContatoRepository
        if (StringUtils.hasText(pacienteCreateDTO.getEmail())) {
            contatoRepository.findByEmail(pacienteCreateDTO.getEmail().trim().toLowerCase()).ifPresent(c -> {
                throw new EmailAlreadyExistsException("Email " + pacienteCreateDTO.getEmail() + " já cadastrado.");
            });
        }

        PacienteEntity pacienteEntity = new PacienteEntity();
        mapCreateDTOToEntity(pacienteCreateDTO, pacienteEntity);

        PacienteEntity pacienteSalvo = pacienteRepository.save(pacienteEntity);
        logger.info("SERVICE: Paciente criado com ID: {}", pacienteSalvo.getId());
        return convertToDTO(pacienteSalvo);
    }

    @Transactional(readOnly = true)
    public Page<PacienteDTO> buscarTodosPacientes(Pageable pageable, String nome, String cpf) {
        logger.info("SERVICE: Buscando pacientes. Filtros: nome='{}', cpf='{}'", nome, cpf);
        Page<PacienteEntity> pacientesPage;
        // ATENÇÃO: Se a busca por email/telefone for um requisito que afeta a paginação/filtros principais,
        // esta lógica precisará de Specification para incluir joins com ContatoEntity.
        // Por ora, a busca principal por nome/cpf continua na PacienteEntity.
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
        return pacientesPage.map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public PacienteDTO buscarPacientePorId(Long id) {
        logger.info("SERVICE: Buscando paciente com ID: {}", id);
        PacienteEntity pacienteEntity = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com ID: " + id));
        // Eagerly fetch LAZY associations if needed by DTO
        if (pacienteEntity.getEndereco() != null) {
            pacienteEntity.getEndereco().getCep(); // Access a field to trigger load
        }
        if (pacienteEntity.getContato() != null) {
            pacienteEntity.getContato().getEmail(); // Access a field to trigger load
        }
        return convertToDTO(pacienteEntity);
    }

    @Transactional
    public PacienteDTO atualizarPaciente(Long id, PacienteUpdateDTO pacienteUpdateDTO) {
        logger.info("SERVICE: Atualizando paciente com ID: {}", id);
        PacienteEntity pacienteEntity = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com ID: " + id));

        // Eagerly fetch LAZY associations if they might be updated
        if (pacienteUpdateDTO.getEndereco() != null && pacienteEntity.getEndereco() != null) {
            pacienteEntity.getEndereco().getCep();
        }
        if ((StringUtils.hasText(pacienteUpdateDTO.getTelefone()) || StringUtils.hasText(pacienteUpdateDTO.getEmail())) && pacienteEntity.getContato() != null) {
            pacienteEntity.getContato().getId();
        }

        mapUpdateDTOToEntity(pacienteUpdateDTO, pacienteEntity);

        PacienteEntity pacienteAtualizado = pacienteRepository.save(pacienteEntity);
        logger.info("SERVICE: Paciente atualizado com ID: {}", pacienteAtualizado.getId());
        return convertToDTO(pacienteAtualizado);
    }

    @Transactional
    public void deletarPaciente(Long id) {
        logger.info("SERVICE: Deletando paciente com ID: {}", id);
        PacienteEntity paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com ID: " + id));
        // A EnderecoEntity e ContatoEntity associadas serão removidas devido a orphanRemoval=true
        pacienteRepository.delete(paciente);
        logger.info("SERVICE: Paciente deletado com ID: {}", id);
    }
}