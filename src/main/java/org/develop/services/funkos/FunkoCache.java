package org.develop.services.funkos;

import org.develop.model.Funko;
import org.develop.services.cache.Cache;

/**
 * Interfaz que representa una cache específica para objetos Funko, donde los objetos Funko se almacenan y recuperan
 * utilizando identificadores enteros (ID).
 *
 * @param <K> Tipo de dato de la clave utilizada para identificar los objetos Funko en la cache.
 * @param <V> Tipo de objeto Funko que se almacena en la cache.
 */
public interface FunkoCache extends Cache<Integer, Funko> {
}
