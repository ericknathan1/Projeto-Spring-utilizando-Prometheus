package com.senac.stockflow.repository;
import com.senac.stockflow.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    // O JpaRepository já te dá: save(), findAll(), findById(), delete()

    // Custom method (Opcional): Buscar evento pelo nome para facilitar testes
    Optional<Evento> findByNome(String nome);
}
