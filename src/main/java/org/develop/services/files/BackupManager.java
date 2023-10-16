package org.develop.services.files;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Interfaz para gestionar la lectura y escritura de datos desde/hacia archivos de respaldo.
 *
 * @param <T> El tipo de datos que se lee o escribe desde/hacia el archivo.
 */
public interface BackupManager <T>{

    /**
     * Lee datos desde un archivo en una secuencia reactiva.
     *
     * @param path La ruta del archivo desde donde se leeran los datos.
     * @return Una secuencia reactiva que emite elementos de tipo T leidos desde el archivo.
     */
    Flux<T> readFile(String path);

    /**
     * Escribe una lista de datos en un archivo.
     *
     * @param path La ruta del archivo donde se escribiran los datos.
     * @param list La lista de elementos de tipo T que se escribiran en el archivo.
     * @return Un mono (Mono) que indica si la escritura fue exitosa (true) o no (false).
     */
    Mono<Boolean> writeFile(String path, List<T> list);
}
