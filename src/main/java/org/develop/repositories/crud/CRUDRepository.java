package org.develop.repositories.crud;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interfaz generica que define operaciones CRUD (Crear, Leer, Actualizar, Eliminar) basicas para
 * trabajar con entidades de un repositorio. Proporciona metodos comunes para interactuar con los datos.
 *
 * @param <T>  El tipo de entidad con la que trabaja el repositorio.
 * @param <ID> El tipo de identificador unico utilizado para buscar y gestionar entidades.
 */
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
