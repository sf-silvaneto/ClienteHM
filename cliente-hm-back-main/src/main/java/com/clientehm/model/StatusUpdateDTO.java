package com.clientehm.model;

import com.clientehm.entity.StatusMedico;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateDTO {
    @NotNull(message = "Status não pode ser nulo")
    private StatusMedico status;

    public StatusMedico getStatus() {
        return status;
    }

    public void setStatus(StatusMedico status) {
        this.status = status;
    }
}