package com.senac.stockflow.controller;

import com.senac.stockflow.DTO.request.CompraRequest;
import com.senac.stockflow.entity.Evento;
import com.senac.stockflow.entity.Ticket;
import com.senac.stockflow.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // Endpoint para configurar o cenário (Cria o evento com 100 ingressos)
    // Chame isso uma vez antes de começar o teste de carga
    @PostMapping("/start")
    public ResponseEntity<Evento> iniciarVendas() {
        Evento evento = ticketService.criarEventoInicial();
        return ResponseEntity.ok(evento);
    }

    // O Endpoint principal de compra
    @PostMapping("/comprar")
    public ResponseEntity<?> comprarIngresso(@RequestBody CompraRequest request) {
        try {
            Ticket ticket = ticketService.realizarCompra(
                    request.eventoId(),
                    request.email(),
                    request.tipo()
            );
            return ResponseEntity.ok(ticket);

        } catch (RuntimeException e) {
            // Se der erro (Sold Out ou Erro de Pagamento simulado),
            // retornamos erro 500 ou 422 para o Grafana registrar a falha HTTP
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
