package org.develop.repositories.funkos;

import org.develop.model.Funko;
import org.develop.repositories.crud.CRUDRepository;
import reactor.core.publisher.Flux;

public interface FunkoRepository extends CRUDRepository<Funko, Integer> {

    Flux<Funko> findByName(String name);
}
