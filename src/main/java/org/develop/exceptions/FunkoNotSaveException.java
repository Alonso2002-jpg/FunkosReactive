package org.develop.exceptions;


/**
 * Una excepcióo que representa una situacion en la que un objeto Funko no se pudo guardar correctamente
 * en la aplicacion. Se utiliza para indicar que ha ocurrido un error al intentar guardar un objeto Funko.
 */
public class FunkoNotSaveException extends FunkoException{

    /**
     * Crea una nueva instancia de FunkoNotSaveException con el mensaje de error especificado.
     *
     * @param message El mensaje que describe la causa de la excepcion.
     */
    public FunkoNotSaveException(String message) {
        super(message);
    }
}
