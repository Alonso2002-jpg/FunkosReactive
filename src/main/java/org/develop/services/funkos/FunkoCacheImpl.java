package org.develop.services.funkos;

import lombok.Getter;
import org.develop.exceptions.FunkoNotFoundException;
import org.develop.model.Funko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Implementacion de una cache para objetos Funko que almacena y recupera objetos Funko utilizando identificadores enteros (ID).
 * La cache tiene un tamano maximo y utiliza un algoritmo de eliminacion de objetos mas antiguos cuando se alcanza el tamano maximo.
 * Tambien se encarga de eliminar automaticamente los objetos caducados de la cache.
 */
public class FunkoCacheImpl implements FunkoCache{
    private final Logger logger = LoggerFactory.getLogger(FunkoCacheImpl.class);
    /**
     * Tamano maximo de la cache.
     */
    @Getter
    private final int maxSize;
    @Getter
    private final Map<Integer, Funko> cache;
    @Getter
    private final ScheduledExecutorService cleaner;

    /**
     * Crea una nueva instancia de FunkoCacheImpl con el tamano maximo especificado.
     *
     * @param maxSize Tamano maximo de la cache.
     */
    public FunkoCacheImpl(int maxSize){
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<>(maxSize,0.75f,true){
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Funko> eldest) {
                return size() > maxSize;
            }
        };

        this.cleaner = Executors.newSingleThreadScheduledExecutor();
        this.cleaner.scheduleAtFixedRate(this::clear,2,2, TimeUnit.MINUTES);
    }

    /**
     * Agrega un objeto Funko a la cache con la clave especificada.
     *
     * @param key   Clave para identificar el objeto Funko en la cache.
     * @param value Objeto Funko que se va a almacenar en la cache.
     * @return Una instancia de Mono<Void> que representa la operacion de almacenamiento en cache.
     */
    @Override
    public Mono<Void> put(Integer key, Funko value) {
        logger.debug("Añadinedo Funko en la Cache id: " + key);
        return Mono.fromRunnable(()->cache.put(key,value));
    }

    /**
     * Obtiene un objeto Funko de la cache utilizando la clave especificada.
     *
     * @param key Clave para identificar el objeto Funko en la cache.
     * @return Una instancia de Mono<Funko> que representa el objeto Funko recuperado de la cache, si existe.
     */
    @Override
    public Mono<Funko> get(Integer key) {
        logger.debug("Obteniendo Funko de la Cache con id: " + key);
        return Mono.justOrEmpty(cache.get(key));
    }

    /**
     * Elimina un objeto Funko de la cache utilizando la clave especificada.
     *
     * @param key Clave para identificar el objeto Funko en la cache.
     * @return Una instancia de Mono<Void> que representa la operacion de eliminacion de la cache.
     */
    @Override
    public Mono<Void> remove(Integer key) {
        logger.debug("Eliminando Funko de la Cache con id: " + key);
        return Mono.fromRunnable(()-> cache.remove(key));
    }

    /**
     * Elimina automaticamente los objetos Funko caducados de la cache.
     */
    @Override
    public void clear() {
        cache.entrySet().removeIf(entry -> {
            boolean shouldRemove = entry.getValue().getUpdated_at().plusMinutes(1).isBefore(LocalDateTime.now());
            if (shouldRemove) {
                logger.debug("Autoeliminando por caducidad funko de cache con id: " + entry.getKey());
            }
            return shouldRemove;
        });
    }

    /**
     * Detiene el servicio de limpieza de la cache.
     */
    @Override
    public void shutdown() {
        cleaner.shutdown();
    }
}
