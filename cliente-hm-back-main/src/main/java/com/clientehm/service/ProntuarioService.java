package com.clientehm.service;

import com.clientehm.entity.PacienteEntity;
import com.clientehm.entity.ProntuarioEntity;
import com.clientehm.entity.HistoricoMedicoEntity;
import com.clientehm.entity.Endereco;
import com.clientehm.exception.ResourceNotFoundException;
import com.clientehm.model.NovoProntuarioRequestDTO;
import com.clientehm.repository.PacienteRepository;
import com.clientehm.repository.ProntuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ProntuarioService {

    private static final Logger logger = LoggerFactory.getLogger(ProntuarioService.class);

    @Autowired
    private ProntuarioRepository prontuarioRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Transactional(readOnly = true)
    public Page<ProntuarioEntity> buscarTodos(Pageable pageable) {
        // Adicionar lógica de filtro/busca avançada aqui se necessário
        return prontuarioRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public ProntuarioEntity buscarPorId(Long id) {
        return prontuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado com ID: " + id));
    }

    @Transactional
    public ProntuarioEntity criarProntuario(NovoProntuarioRequestDTO novoProntuarioDTO, String nomeAdminResponsavel) {
        logger.info("Criando novo prontuário para o paciente: {}", novoProntuarioDTO.getPaciente().getNome());

        PacienteEntity paciente = pacienteRepository.findByCpf(novoProntuarioDTO.getPaciente().getCpf())
                .orElseGet(() -> {
                    PacienteEntity novoPaciente = new PacienteEntity();
                    novoPaciente.setNome(novoProntuarioDTO.getPaciente().getNome());
                    novoPaciente.setDataNascimento(novoProntuarioDTO.getPaciente().getDataNascimento());
                    novoPaciente.setCpf(novoProntuarioDTO.getPaciente().getCpf());
                    try {
                        novoPaciente.setGenero(PacienteEntity.Genero.valueOf(novoProntuarioDTO.getPaciente().getGenero().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Gênero inválido: " + novoProntuarioDTO.getPaciente().getGenero());
                    }
                    novoPaciente.setTelefone(novoProntuarioDTO.getPaciente().getTelefone());
                    novoPaciente.setEmail(novoProntuarioDTO.getPaciente().getEmail());

                    Endereco endereco = new Endereco();

                    endereco.setLogradouro(novoProntuarioDTO.getPaciente().getEndereco().getLogradouro());
                    endereco.setNumero(novoProntuarioDTO.getPaciente().getEndereco().getNumero());
                    endereco.setComplemento(novoProntuarioDTO.getPaciente().getEndereco().getComplemento());
                    endereco.setBairro(novoProntuarioDTO.getPaciente().getEndereco().getBairro());
                    endereco.setCidade(novoProntuarioDTO.getPaciente().getEndereco().getCidade());
                    endereco.setEstado(novoProntuarioDTO.getPaciente().getEndereco().getEstado());
                    endereco.setCep(novoProntuarioDTO.getPaciente().getEndereco().getCep());
                    novoPaciente.setEndereco(endereco);
                    return pacienteRepository.save(novoPaciente);
                });

        ProntuarioEntity prontuario = new ProntuarioEntity();
        prontuario.setPaciente(paciente);
        prontuario.setNumeroProntuario(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        try {
            prontuario.setTipoTratamento(ProntuarioEntity.TipoTratamento.valueOf(novoProntuarioDTO.getTipoTratamento().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de tratamento inválido: " + novoProntuarioDTO.getTipoTratamento());
        }
        prontuario.setDataInicio(LocalDate.now());
        prontuario.setStatus(ProntuarioEntity.StatusProntuario.ATIVO);

        HistoricoMedicoEntity historicoInicial = new HistoricoMedicoEntity();
        historicoInicial.setDescricao(novoProntuarioDTO.getHistoricoMedico().getDescricao());
        historicoInicial.setResponsavel(nomeAdminResponsavel);
        historicoInicial.setData(LocalDateTime.now());
        historicoInicial.setProntuario(prontuario);
        prontuario.getHistoricoMedico().add(historicoInicial);

        ProntuarioEntity prontuarioSalvo = prontuarioRepository.save(prontuario);
        logger.info("Prontuário {} criado com sucesso para o paciente {}", prontuarioSalvo.getNumeroProntuario(), paciente.getNome());
        return prontuarioSalvo;
    }
}