package org.develop.repositories.crud;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CRUDRepository <T,ID>{
     // Métodos que vamos a usar
    // Buscar todos
    Flux<T> findAll();

    // Buscar por ID
    Mono<T> findById(ID id);

    // Guardar
    Mono<T> save(T t);

    // Actualizar
    Mono<T> update(T t);

    // Borrar por ID
    Mono<Boolean> deleteById(ID id);

    // Borrar todos
    Mono<Void> deleteAll();
}
