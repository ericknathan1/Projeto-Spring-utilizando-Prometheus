package com.senac.stockflow.service;

import com.senac.stockflow.entity.Evento;
import com.senac.stockflow.entity.Ticket;
import com.senac.stockflow.enums.StatusVenda;
import com.senac.stockflow.enums.TipoIngresso;
import com.senac.stockflow.repository.EventoRepository;
import com.senac.stockflow.repository.TicketRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TicketService {
    private final EventoRepository eventoRepository;
    private final TicketRepository ticketRepository;
    private final MeterRegistry meterRegistry;

    // Variável atômica para o Gauge (O Prometheus vai ler o valor dela)
    private final AtomicInteger estoqueGauge = new AtomicInteger(0);

    public TicketService(EventoRepository eventoRepository,
                         TicketRepository ticketRepository,
                         MeterRegistry meterRegistry) {
        this.eventoRepository = eventoRepository;
        this.ticketRepository = ticketRepository;
        this.meterRegistry = meterRegistry;

        meterRegistry.gauge("ticket.estoque.total", estoqueGauge);
    }

    // --- CORREÇÃO AQUI ---
    // Agora este método REUTILIZA o ID 1 em vez de criar ID 2, 3, etc.
    public Evento criarEventoInicial() {
        // Tenta achar o evento 1. Se não achar, cria um objeto vazio.
        Evento evento = eventoRepository.findById(1L).orElse(new Evento());

        // Força os dados do Evento 1 (Reset)
        // Se não setar o ID manualmente na criação, o banco auto-incrementa
        if(evento.getId() == null) {
            evento = new Evento(null, "Show de Lançamento - SoundCheck",
                    null, new BigDecimal("100.00"), 10000, 10000);
            // O save inicial vai gerar o ID 1 se o banco estiver vazio
        } else {
            // Se já existe, só atualizamos o estoque
            evento.setEstoqueDisponivel(10000);
            evento.setCapacidadeTotal(10000);
        }

        estoqueGauge.set(evento.getEstoqueDisponivel());
        return eventoRepository.save(evento);
    }
    // ---------------------

    public Ticket realizarCompra(Long eventoId, String email, TipoIngresso tipo) {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            long sleepTime = (long) (Math.random() * 500);
            Thread.sleep(sleepTime);

            Evento evento = eventoRepository.findById(eventoId)
                    .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

            if (Math.random() < 0.1) {
                gerarMetricaErro("erro_pagamento");
                throw new RuntimeException("Erro no Gateway de Pagamento!");
            }

            if (evento.getEstoqueDisponivel() > 0) {
                evento.debitarEstoque();
                eventoRepository.save(evento);

                estoqueGauge.set(evento.getEstoqueDisponivel());

                Ticket ticket = new Ticket();
                ticket.setEvento(evento);
                ticket.setClienteEmail(email);
                ticket.setTipo(tipo);
                ticket.setStatus(StatusVenda.SUCESSO);
                ticket.setDataCompra(LocalDateTime.now());
                ticket.setValorFinal(evento.getPrecoBase());

                Ticket salvo = ticketRepository.save(ticket);

                meterRegistry.counter("ticket.vendas.sucesso", "tipo", tipo.name()).increment();

                return salvo;

            } else {
                gerarMetricaErro("estoque_esgotado");
                throw new RuntimeException("SOLD OUT! Ingressos Esgotados.");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            throw e;
        } finally {
            sample.stop(meterRegistry.timer("ticket.compra.latencia"));
        }
    }

    private void gerarMetricaErro(String motivo) {
        Counter.builder("ticket.vendas.erro")
                .tag("motivo", motivo)
                .register(meterRegistry)
                .increment();
    }
}