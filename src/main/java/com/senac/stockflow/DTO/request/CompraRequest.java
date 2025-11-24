package com.senac.stockflow.DTO.request;

import com.senac.stockflow.enums.TipoIngresso;

public record CompraRequest(
        Long eventoId,
        String email,
        TipoIngresso tipo


) {
    @Override
    public Long eventoId() {
        return eventoId;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public TipoIngresso tipo() {
        return tipo;
    }
}
