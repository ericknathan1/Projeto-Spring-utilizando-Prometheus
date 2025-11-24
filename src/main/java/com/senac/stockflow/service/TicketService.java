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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

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

        // REGISTRANDO O GAUGE NO CONSTRUTOR
        // Dizemos ao Prometheus: "Fique de olho nessa variável 'estoqueGauge'"
        meterRegistry.gauge("ticket.estoque.total", estoqueGauge);
    }

    // Método para criar um evento inicial (Seed) e atualizar o Gauge
    public Evento criarEventoInicial() {
        Evento evento = new Evento(null, "Show de Lançamento - SoundCheck",
                null, new BigDecimal("100.00"), 500, 100); // Começa com 100 ingressos

        // Sincroniza o Gauge com o banco
        estoqueGauge.set(evento.getEstoqueDisponivel());
        return eventoRepository.save(evento);
    }

    public Ticket realizarCompra(Long eventoId, String email, TipoIngresso tipo) {
        // 1. MONITORAMENTO DE LATÊNCIA (TIMER)
        // Tudo que estiver dentro deste .record() será cronometrado
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            // --- SIMULAÇÃO DE CAOS (Lentidão Aleatória) ---
            // Simula uma espera de banco de dados (0 a 2 segundos)
            long sleepTime = (long) (Math.random() * 2000);
            Thread.sleep(sleepTime);

            Evento evento = eventoRepository.findById(eventoId)
                    .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

            // --- SIMULAÇÃO DE CAOS (Erro de Pagamento) ---
            // 10% de chance do pagamento falhar (simulando Gateway fora do ar)
            if (Math.random() < 0.1) {
                gerarMetricaErro("erro_pagamento");
                throw new RuntimeException("Erro no Gateway de Pagamento!");
            }

            // Lógica de Estoque
            if (evento.getEstoqueDisponivel() > 0) {
                evento.debitarEstoque();
                eventoRepository.save(evento);

                // ATUALIZA O GAUGE (Importante!)
                // Atualiza a variável que o Prometheus está olhando
                estoqueGauge.set(evento.getEstoqueDisponivel());

                // Cria o Ticket
                Ticket ticket = new Ticket();
                ticket.setEvento(evento);
                ticket.setClienteEmail(email);
                ticket.setTipo(tipo);
                ticket.setStatus(StatusVenda.SUCESSO);
                ticket.setDataCompra(LocalDateTime.now());
                ticket.setValorFinal(evento.getPrecoBase()); // Simplificado

                Ticket salvo = ticketRepository.save(ticket);

                // 2. MONITORAMENTO DE NEGÓCIO (COUNTER)
                // Incrementa venda realizada com sucesso, separando por tipo (VIP, PISTA)
                meterRegistry.counter("ticket.vendas.sucesso", "tipo", tipo.name()).increment();

                return salvo;

            } else {
                // Caso de Sold Out
                gerarMetricaErro("estoque_esgotado");
                throw new RuntimeException("SOLD OUT! Ingressos Esgotados.");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            // Se der erro, paramos o timer e lançamos a exceção para o Controller
            throw e;
        } finally {
            // Para o cronômetro e registra o tempo
            sample.stop(meterRegistry.timer("ticket.compra.latencia"));
        }
    }

    // Método auxiliar para contar erros
    private void gerarMetricaErro(String motivo) {
        Counter.builder("ticket.vendas.erro")
                .tag("motivo", motivo)
                .register(meterRegistry)
                .increment();
    }
}
