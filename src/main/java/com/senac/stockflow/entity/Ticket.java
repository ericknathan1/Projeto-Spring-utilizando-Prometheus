package com.senac.stockflow.entity;

import com.senac.stockflow.enums.StatusVenda;
import com.senac.stockflow.enums.TipoIngresso;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // UUID é mais profissional para tickets

    @ManyToOne
    @JoinColumn(name = "evento_id")
    private Evento evento;

    private String clienteEmail; // Simples, sem tabela de usuários por enquanto

    @Enumerated(EnumType.STRING)
    private TipoIngresso tipo; // VIP, PISTA, MEIA

    @Enumerated(EnumType.STRING)
    private StatusVenda status; // APROVADO, RECUSADO, CANCELADO

    private LocalDateTime dataCompra;

    private BigDecimal valorFinal;

    public Ticket(UUID id, Evento evento, String clienteEmail, TipoIngresso tipo, StatusVenda status, LocalDateTime dataCompra, BigDecimal valorFinal) {
        this.id = id;
        this.evento = evento;
        this.clienteEmail = clienteEmail;
        this.tipo = tipo;
        this.status = status;
        this.dataCompra = dataCompra;
        this.valorFinal = valorFinal;
    }

    public Ticket() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public String getClienteEmail() {
        return clienteEmail;
    }

    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
    }

    public TipoIngresso getTipo() {
        return tipo;
    }

    public void setTipo(TipoIngresso tipo) {
        this.tipo = tipo;
    }

    public StatusVenda getStatus() {
        return status;
    }

    public void setStatus(StatusVenda status) {
        this.status = status;
    }

    public LocalDateTime getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDateTime dataCompra) {
        this.dataCompra = dataCompra;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }
}
