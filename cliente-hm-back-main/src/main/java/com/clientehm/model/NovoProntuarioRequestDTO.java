package com.clientehm.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

// Baseado em NovoProntuarioRequest do frontend
public class NovoProntuarioRequestDTO {

    @NotNull(message = "Dados do paciente são obrigatórios")
    @Valid
    private PacienteRequestDTO paciente;

    @NotBlank(message = "Tipo de tratamento é obrigatório")
    private String tipoTratamento; // Usar String para representar o Enum vindo do frontend

    @NotNull(message = "Histórico médico inicial é obrigatório")
    @Valid
    private HistoricoMedicoInicialDTO historicoMedico;

    // Getters e Setters
    public PacienteRequestDTO getPaciente() { return paciente; }
    public void setPaciente(PacienteRequestDTO paciente) { this.paciente = paciente; }
    public String getTipoTratamento() { return tipoTratamento; }
    public void setTipoTratamento(String tipoTratamento) { this.tipoTratamento = tipoTratamento; }
    public HistoricoMedicoInicialDTO getHistoricoMedico() { return historicoMedico; }
    public void setHistoricoMedico(HistoricoMedicoInicialDTO historicoMedico) { this.historicoMedico = historicoMedico; }

    // Classes aninhadas para o request do paciente e histórico
    public static class PacienteRequestDTO {
        @NotBlank(message = "Nome do paciente é obrigatório")
        @Size(min = 3, message = "Nome do paciente deve ter no mínimo 3 caracteres")
        private String nome;

        @NotNull(message = "Data de nascimento é obrigatória")
        @PastOrPresent(message = "Data de nascimento deve ser no passado ou presente")
        private LocalDate dataNascimento; // Alterado de String para LocalDate para melhor validação

        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter 11 dígitos") // Validação básica, considere uma mais robusta
        private String cpf;

        @NotBlank(message = "Gênero é obrigatório")
        private String genero; // Enum: MASCULINO, FEMININO, OUTRO, NAO_INFORMADO

        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(regexp = "^\\d{10,11}$", message = "Telefone deve conter 10 ou 11 dígitos")
        private String telefone;

        @NotBlank(message = "Email é obrigatório")
        @jakarta.validation.constraints.Email(message = "Formato de email inválido")
        private String email;

        @NotNull(message = "Endereço é obrigatório")
        @Valid
        private EnderecoRequestDTO endereco;

        // Getters e Setters para PacienteRequestDTO
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public LocalDate getDataNascimento() { return dataNascimento; }
        public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
        public String getCpf() { return cpf; }
        public void setCpf(String cpf) { this.cpf = cpf; }
        public String getGenero() { return genero; }
        public void setGenero(String genero) { this.genero = genero; }
        public String getTelefone() { return telefone; }
        public void setTelefone(String telefone) { this.telefone = telefone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public EnderecoRequestDTO getEndereco() { return endereco; }
        public void setEndereco(EnderecoRequestDTO endereco) { this.endereco = endereco; }
    }

    public static class EnderecoRequestDTO {
        @NotBlank(message = "Logradouro é obrigatório")
        private String logradouro;
        @NotBlank(message = "Número é obrigatório")
        private String numero;
        private String complemento;
        @NotBlank(message = "Bairro é obrigatório")
        private String bairro;
        @NotBlank(message = "Cidade é obrigatória")
        private String cidade;
        @NotBlank(message = "Estado é obrigatório")
        @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres (UF)")
        private String estado;
        @NotBlank(message = "CEP é obrigatório")
        @Pattern(regexp = "^\\d{8}$", message = "CEP deve conter 8 dígitos")
        private String cep;

        // Getters e Setters para EnderecoRequestDTO
        public String getLogradouro() { return logradouro; }
        public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
        public String getNumero() { return numero; }
        public void setNumero(String numero) { this.numero = numero; }
        public String getComplemento() { return complemento; }
        public void setComplemento(String complemento) { this.complemento = complemento; }
        public String getBairro() { return bairro; }
        public void setBairro(String bairro) { this.bairro = bairro; }
        public String getCidade() { return cidade; }
        public void setCidade(String cidade) { this.cidade = cidade; }
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
        public String getCep() { return cep; }
        public void setCep(String cep) { this.cep = cep; }
    }

    public static class HistoricoMedicoInicialDTO {
        @NotBlank(message = "Descrição do histórico médico é obrigatória")
        @Size(min = 10, message = "Descrição do histórico deve ter no mínimo 10 caracteres")
        private String descricao;

        // Getters e Setters
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
    }
}