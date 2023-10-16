package org.develop.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Clase que proporciona la funcionalidad de generación de identificadores unicos.
 * Esta clase garantiza que los identificadores generados sean unicos incluso en entornos
 * multi-hilo gracias al uso de un mecanismo de bloqueo.
 */
public class MyIDGenerator {

    private static MyIDGenerator instance;
    private static long id = 0;

    private static final Lock locker = new ReentrantLock(true);

    private MyIDGenerator(){}


    /**
     * Obtiene una instancia unica de la clase MyIDGenerator. Si la instancia aun no ha sido creada,
     * se crea una nueva y se devuelve.
     *
     * @return Una instancia de MyIDGenerator.
     */
    public static MyIDGenerator getInstance(){
        if (instance == null){
            instance = new MyIDGenerator();
        }
        return instance;
    }

    /**
     * Genera un identificador unico y lo incrementa en uno.
     *
     * @return El identificador unico generado.
     */
    public Long getIDandIncrement(){
        locker.lock();
        id++;
        locker.unlock();
        return id;
    }
}
