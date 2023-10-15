package org.develop.services.files;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BackupManager <T>{

    Flux<T> readFile(String path);

    Mono<Boolean> writeFile(String path, List<T> list);
}
