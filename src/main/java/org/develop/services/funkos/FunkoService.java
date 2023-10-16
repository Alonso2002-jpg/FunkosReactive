package org.develop.services.funkos;

import org.develop.model.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Interfaz que define operaciones para el servicio relacionado con objetos Funko. Proporciona métodos para buscar, guardar, actualizar y eliminar Funkos, asi como realizar operaciones de respaldo e importacion.
 */
public interface FunkoService {
        // Buscar todos
    Flux<Funko> findAll();

    // Buscar por ID
    Mono<Funko> findById(Integer id);
    //Buscar por nombre
    Flux<Funko> findByName(String name);

    // Guardar
    Mono<Funko> save(Funko funko);

    // Actualizar
    Mono<Funko> update(Funko funko);

    // Borrar por ID
    Mono<Funko> deleteById(Integer id);

    // Borrar todos
    Mono<Void> deleteAll();

    Mono<Boolean> backup(String file);

    Flux<Funko> imported(String file);

}
