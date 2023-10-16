package org.develop.services.cache;

import reactor.core.publisher.Mono;

public interface Cache<K,V> {

    /**
     * Almacena un valor en la cache asociado a una clave.
     *
     * @param key La clave que se utilizara para almacenar el valor en la cache.
     * @param value El valor que se va a almacenar en la cache.
     * @return Un mono (Mono) que indica la finalizacion exitosa de la operacion.
     */
    Mono<Void> put(K key, V value);

    /**
     * Recupera el valor asociado a una clave en la cache.
     *
     * @param key La clave cuyo valor se desea recuperar de la cache.
     * @return Un mono (Mono) que contiene el valor asociado a la clave, o un mono vacio si la clave no existe en la cache.
     */
    Mono<V> get(K key);

    /**
     * Elimina una entrada de la cache mediante su clave.
     *
     * @param key La clave de la entrada que se va a eliminar de la cache.
     * @return Un mono (Mono) que indica la finalizacion exitosa de la operacion.
     */
    Mono<Void> remove(K key);

    /**
     * Elimina todos los elementos de la cache, dejándola vacia.
     */
    void clear();

    /**
     * Realiza una operacion de apagado o liberacion de recursos en la cache, si es necesario.
     */
    void shutdown();
}
