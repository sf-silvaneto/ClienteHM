package com.main.mapper;

import com.main.domain.entity.AdministradorEntity;
import com.main.api.model.AdministradorRegistroDTO;
import com.main.api.model.AdministradorDadosDTO;
import com.main.api.model.ProntuarioDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class AdministradorMapper {

    @Autowired
    private ModelMapper modelMapper;

    public AdministradorEntity toEntity(AdministradorRegistroDTO registroDTO) {
        AdministradorEntity entity = modelMapper.map(registroDTO, AdministradorEntity.class);
        return entity;
    }

    public AdministradorDadosDTO toDadosDTO(AdministradorEntity admin) {
        if (admin == null) return null;
        AdministradorDadosDTO dto = new AdministradorDadosDTO();
        dto.setId(admin.getId());
        dto.setNome(admin.getNome());
        dto.setEmail(admin.getEmail());
        String role = admin.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replace("ROLE_", ""))
                .findFirst()
                .orElse("USER");
        dto.setRole(role);
        return dto;
    }

    public Map<String, Object> toLoginResponseMap(AdministradorEntity admin, String token) {
        Map<String, Object> response = new HashMap<>();
        response.put("mensagem", "Login realizado com sucesso");
        response.put("token", token);
        response.put("id", admin.getId().toString());
        response.put("nome", admin.getNome());
        response.put("email", admin.getEmail());
        String role = admin.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replace("ROLE_", ""))
                .findFirst()
                .orElse("USER");
        response.put("role", role);
        return response;
    }

    public ProntuarioDTO.AdministradorBasicDTO toAdminBasicDTO(AdministradorEntity admin) {
        if (admin == null) {
            return null;
        }
        ProntuarioDTO.AdministradorBasicDTO dto = new ProntuarioDTO.AdministradorBasicDTO();
        dto.setId(admin.getId());
        dto.setNome(admin.getNome());
        dto.setEmail(admin.getEmail());
        return dto;
    }
}