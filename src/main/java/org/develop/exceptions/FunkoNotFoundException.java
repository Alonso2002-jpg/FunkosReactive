package org.develop.exceptions;

/**
 * Una excepcion que representa una situacion en la que un objeto Funko no se encuentra en la aplicacion.
 * Se utiliza para indicar que no se pudo encontrar un objeto Funko especifico en una operacion o consulta.
 */
public class FunkoNotFoundException extends FunkoException{

    /**
     * Crea una nueva instancia de FunkoNotFoundException con el mensaje de error especificado.
     *
     * @param message El mensaje que describe la causa de la excepcion.
     */
    public FunkoNotFoundException(String message) {
        super(message);
    }
}
