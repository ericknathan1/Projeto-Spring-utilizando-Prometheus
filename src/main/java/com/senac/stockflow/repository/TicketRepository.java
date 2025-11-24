package com.senac.stockflow.repository;
import com.senac.stockflow.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    // Custom method: Buscar todos os tickets de um evento específico
    // O Spring monta o SQL sozinho baseado no nome do método!
    List<Ticket> findByEventoId(Long eventoId);

    // Custom method: Contar quantos ingressos um e-mail já comprou (Para evitar cambistas, por exemplo)
    Integer countByClienteEmail(String clienteEmail);
}
