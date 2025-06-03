package com.clientehm.service;

// Mantenha os imports existentes para PacienteEntity, exceções, DTOs (PacienteCreateDTO, PacienteDTO, PacienteUpdateDTO, EnderecoDTO, EnderecoCreateDTO, EnderecoUpdateDTO)
import com.clientehm.entity.PacienteEntity;
// import com.clientehm.entity.Endereco; // REMOVA ESTA LINHA (se referir ao Embeddable antigo)
import com.clientehm.entity.EnderecoEntity; // ADICIONE ESTA LINHA
import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.exception.CpfAlreadyExistsException;
import com.clientehm.exception.EmailAlreadyExistsException;
import com.clientehm.model.PacienteCreateDTO;
import com.clientehm.model.PacienteDTO;
import com.clientehm.model.PacienteUpdateDTO;
import com.clientehm.model.EnderecoCreateDTO; // Mantenha
import com.clientehm.model.EnderecoDTO;       // Mantenha
import com.clientehm.model.EnderecoUpdateDTO; // Mantenha
import com.clientehm.repository.PacienteRepository;
import com.clientehm.repository.EnderecoRepository; // ADICIONE ESTA LINHA
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

    @Autowired // ADICIONE ESTA LINHA
    private EnderecoRepository enderecoRepository;

    private PacienteDTO convertToDTO(PacienteEntity entity) {
        if (entity == null) return null;
        PacienteDTO dto = new PacienteDTO();
        // Copia as propriedades, excluindo 'endereco' que agora precisa de mapeamento customizado
        BeanUtils.copyProperties(entity, dto, "endereco");

        if (entity.getGenero() != null) {
            dto.setGenero(entity.getGenero().name());
        }
        if (entity.getRacaCor() != null) {
            dto.setRacaCor(entity.getRacaCor().name());
        } else {
            dto.setRacaCor(null); // Define explicitamente como nulo se não presente
        }
        if (entity.getTipoSanguineo() != null) {
            dto.setTipoSanguineo(entity.getTipoSanguineo().name());
        } else {
            dto.setTipoSanguineo(null); // Define explicitamente como nulo se não presente
        }

        // Converte EnderecoEntity para EnderecoDTO
        if (entity.getEndereco() != null) {
            EnderecoEntity enderecoEntity = entity.getEndereco();
            EnderecoDTO enderecoDTO = new EnderecoDTO();
            BeanUtils.copyProperties(enderecoEntity, enderecoDTO); // Nomes devem coincidir
            dto.setEndereco(enderecoDTO);
        } else {
            dto.setEndereco(null); // Garante que o DTO de endereço seja nulo se não houver entidade
        }
        // Garante que outros campos como alergias, comorbidades, medicamentos sejam copiados se forem campos diretos
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
                entity.setRacaCor(null); // Padrão ou tratar conforme necessário
            }
            if (StringUtils.hasText(dto.getTipoSanguineo())) {
                entity.setTipoSanguineo(PacienteEntity.TipoSanguineo.valueOf(dto.getTipoSanguineo().toUpperCase()));
            } else {
                entity.setTipoSanguineo(null); // Padrão ou tratar conforme necessário
            }
        } catch (IllegalArgumentException e) {
            logger.error("Valor de Enum inválido fornecido na criação: {}", e.getMessage());
            // Considerar relançar ou uma exceção mais específica se for crítico
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

        // Lida com Endereco
        if (dto.getEndereco() != null) {
            EnderecoCreateDTO enderecoCreateDTO = dto.getEndereco();
            EnderecoEntity enderecoEntity = new EnderecoEntity(); // Cria nova Entidade
            BeanUtils.copyProperties(enderecoCreateDTO, enderecoEntity, "id"); // "id" é gerado
            // Se EnderecoEntity tivesse uma referência @OneToOne de volta para PacienteEntity:
            // enderecoEntity.setPaciente(entity);
            entity.setEndereco(enderecoEntity); // Associa com PacienteEntity
        }
    }

    private void mapUpdateDTOToEntity(PacienteUpdateDTO dto, PacienteEntity entity) {
        // Atualiza campos básicos de Paciente (verificações de nulo são importantes aqui)
        if (StringUtils.hasText(dto.getNome())) entity.setNome(dto.getNome());
        if (dto.getDataNascimento() != null) entity.setDataNascimento(dto.getDataNascimento());
        if (StringUtils.hasText(dto.getRg())) entity.setRg(dto.getRg());

        if (dto.getGenero() != null) { // Verifica se genero foi fornecido no DTO
            if (StringUtils.hasText(dto.getGenero())) {
                try {
                    entity.setGenero(PacienteEntity.Genero.valueOf(dto.getGenero().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Gênero inválido: " + dto.getGenero());
                }
            } // Se dto.getGenero() for uma string vazia, não atualizará, mantendo o existente ou nulo
        }

        if (StringUtils.hasText(dto.getTelefone())) entity.setTelefone(dto.getTelefone());
        if (StringUtils.hasText(dto.getEmail())) {
            // Verifica unicidade do email apenas se estiver sendo alterado
            if (!dto.getEmail().equalsIgnoreCase(entity.getEmail())) {
                Optional<PacienteEntity> pacienteComEmail = pacienteRepository.findByEmail(dto.getEmail());
                if (pacienteComEmail.isPresent() && !pacienteComEmail.get().getId().equals(entity.getId())) {
                    throw new EmailAlreadyExistsException("Email " + dto.getEmail() + " já cadastrado para outro paciente.");
                }
            }
            entity.setEmail(dto.getEmail());
        }
        if (StringUtils.hasText(dto.getNomeMae())) entity.setNomeMae(dto.getNomeMae());
        if (dto.getNomePai() != null) entity.setNomePai(StringUtils.hasText(dto.getNomePai()) ? dto.getNomePai() : null); // Permite limpar
        if (dto.getDataEntrada() != null) entity.setDataEntrada(dto.getDataEntrada());
        if (dto.getCartaoSus() != null) entity.setCartaoSus(StringUtils.hasText(dto.getCartaoSus()) ? dto.getCartaoSus() : null); // Permite limpar

        if (dto.getRacaCor() != null) {
            if (StringUtils.hasText(dto.getRacaCor())) {
                try {
                    entity.setRacaCor(PacienteEntity.RacaCor.valueOf(dto.getRacaCor().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Raça/Cor inválida: " + dto.getRacaCor());
                }
            } else {
                entity.setRacaCor(null); // Define explicitamente como nulo se string vazia fornecida
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
                entity.setTipoSanguineo(null); // Define explicitamente como nulo se string vazia fornecida
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

        // Lida com Atualização de Endereco
        if (dto.getEndereco() != null) {
            EnderecoUpdateDTO enderecoUpdateDTO = dto.getEndereco();
            EnderecoEntity enderecoEntity = entity.getEndereco();

            if (enderecoEntity == null) { // Se não houver endereço existente para o paciente, cria um novo
                if (StringUtils.hasText(enderecoUpdateDTO.getCep()) && StringUtils.hasText(enderecoUpdateDTO.getLogradouro())) { // Verificação básica
                    enderecoEntity = new EnderecoEntity();
                    // Se EnderecoEntity tivesse uma referência @OneToOne de volta para PacienteEntity:
                    // enderecoEntity.setPaciente(entity);
                    entity.setEndereco(enderecoEntity); // Associa com PacienteEntity
                }
            }

            // Só atualiza se uma entidade de endereço existir ou tiver acabado de ser criada
            if (enderecoEntity != null) {
                // Atualiza campos se forem fornecidos no DTO
                if (StringUtils.hasText(enderecoUpdateDTO.getLogradouro())) enderecoEntity.setLogradouro(enderecoUpdateDTO.getLogradouro());
                if (StringUtils.hasText(enderecoUpdateDTO.getNumero())) enderecoEntity.setNumero(enderecoUpdateDTO.getNumero());

                // Para complemento, permite definir como nulo ou string vazia se o DTO fornecer explicitamente
                if (enderecoUpdateDTO.getComplemento() != null) { // Verifica se o campo em si está presente no DTO
                    enderecoEntity.setComplemento(StringUtils.hasText(enderecoUpdateDTO.getComplemento()) ? enderecoUpdateDTO.getComplemento() : null);
                }

                if (StringUtils.hasText(enderecoUpdateDTO.getBairro())) enderecoEntity.setBairro(enderecoUpdateDTO.getBairro());
                if (StringUtils.hasText(enderecoUpdateDTO.getCidade())) enderecoEntity.setCidade(enderecoUpdateDTO.getCidade());
                if (StringUtils.hasText(enderecoUpdateDTO.getEstado())) enderecoEntity.setEstado(enderecoUpdateDTO.getEstado());
                if (StringUtils.hasText(enderecoUpdateDTO.getCep())) enderecoEntity.setCep(enderecoUpdateDTO.getCep());
            }
        }
        // Se dto.getEndereco() for nulo, não estamos alterando o endereço (poderia ser removido por orphanRemoval se entity.setEndereco(null) fosse chamado, mas essa não é a lógica atual)
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
        mapCreateDTOToEntity(pacienteCreateDTO, pacienteEntity); // Usa o mapeamento atualizado

        PacienteEntity pacienteSalvo = pacienteRepository.save(pacienteEntity);
        logger.info("SERVICE: Paciente criado com ID: {}", pacienteSalvo.getId());
        return convertToDTO(pacienteSalvo); // Usa a conversão atualizada
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
        return pacientesPage.map(this::convertToDTO); // Usa a conversão atualizada
    }

    @Transactional(readOnly = true)
    public PacienteDTO buscarPacientePorId(Long id) {
        logger.info("SERVICE: Buscando paciente com ID: {}", id);
        PacienteEntity pacienteEntity = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com ID: " + id));
        // Carrega EnderecoEntity se LAZY e não carregado automaticamente pelo findById
        if (pacienteEntity.getEndereco() != null) {
            pacienteEntity.getEndereco().getCep(); // Acessa um campo para disparar o carregamento
        }
        return convertToDTO(pacienteEntity); // Usa a conversão atualizada
    }

    @Transactional
    public PacienteDTO atualizarPaciente(Long id, PacienteUpdateDTO pacienteUpdateDTO) {
        logger.info("SERVICE: Atualizando paciente com ID: {}", id);
        PacienteEntity pacienteEntity = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com ID: " + id));

        // Dispara o carregamento de EnderecoEntity se LAZY e uma atualização para ele for fornecida
        if (pacienteUpdateDTO.getEndereco() != null && pacienteEntity.getEndereco() != null) {
            pacienteEntity.getEndereco().getCep(); // Acessa um campo para garantir que está carregado
        }

        mapUpdateDTOToEntity(pacienteUpdateDTO, pacienteEntity); // Usa o mapeamento atualizado

        PacienteEntity pacienteAtualizado = pacienteRepository.save(pacienteEntity);
        logger.info("SERVICE: Paciente atualizado com ID: {}", pacienteAtualizado.getId());
        return convertToDTO(pacienteAtualizado); // Usa a conversão atualizada
    }

    @Transactional
    public void deletarPaciente(Long id) {
        logger.info("SERVICE: Deletando paciente com ID: {}", id);
        PacienteEntity paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com ID: " + id));

        // A EnderecoEntity associada será removida devido a orphanRemoval=true
        // no mapeamento @OneToOne em PacienteEntity quando a PacienteEntity for deletada.
        pacienteRepository.delete(paciente);
        logger.info("SERVICE: Paciente deletado com ID: {}", id);
    }
}