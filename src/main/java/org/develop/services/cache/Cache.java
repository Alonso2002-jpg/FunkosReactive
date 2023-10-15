package org.develop.services.cache;

import reactor.core.publisher.Mono;

public interface Cache<K,V> {

    Mono<Void> put(K key, V value);
    Mono<V> get(K key);
    Mono<Void> remove(K key);
    Mono<Void> clear();
    Mono<Void> shutdown();
}