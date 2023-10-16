package org.develop.repositories.funkos;

import org.develop.model.Funko;
import org.develop.repositories.crud.CRUDRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Interfaz que extiende la interfaz CRUDRepository y proporciona operaciones especificas para
 * trabajar con entidades Funko. Esta interfaz define metodos adicionales para buscar Funkos por nombre y UUID.
 */
public interface FunkoRepository extends CRUDRepository<Funko, Integer> {


    /**
     * Busca Funkos por nombre.
     *
     * @param name El nombre de los Funkos a buscar.
     * @return Un flujo (Flux) de Funkos que coinciden con el nombre especificado.
     */
    Flux<Funko> findByName(String name);

    /**
     * Busca un Funko por su UUID.
     *
     * @param uuid El UUID del Funko a buscar.
     * @return Un mono (Mono) que representa el Funko encontrado por UUID.
     */
    Mono<Funko> findByUuid(UUID uuid);
}
