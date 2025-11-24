package com.senac.stockflow.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "eventos")
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private LocalDate data;

    private BigDecimal precoBase;

    private Integer capacidadeTotal;

    // Campo CRÍTICO para monitoramento
    private Integer estoqueDisponivel;

    // Método auxiliar para diminuir estoque
    public void debitarEstoque() {
        if (this.estoqueDisponivel > 0) {
            this.estoqueDisponivel--;
        } else {
            throw new RuntimeException("Sold Out!");
        }
    }

    public Evento() {
    }

    public Evento(Long id, String nome, LocalDate data, BigDecimal precoBase, Integer capacidadeTotal, Integer estoqueDisponivel) {
        this.id = id;
        this.nome = nome;
        this.data = data;
        this.precoBase = precoBase;
        this.capacidadeTotal = capacidadeTotal;
        this.estoqueDisponivel = estoqueDisponivel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public BigDecimal getPrecoBase() {
        return precoBase;
    }

    public void setPrecoBase(BigDecimal precoBase) {
        this.precoBase = precoBase;
    }

    public Integer getCapacidadeTotal() {
        return capacidadeTotal;
    }

    public void setCapacidadeTotal(Integer capacidadeTotal) {
        this.capacidadeTotal = capacidadeTotal;
    }

    public Integer getEstoqueDisponivel() {
        return estoqueDisponivel;
    }

    public void setEstoqueDisponivel(Integer estoqueDisponivel) {
        this.estoqueDisponivel = estoqueDisponivel;
    }
}
