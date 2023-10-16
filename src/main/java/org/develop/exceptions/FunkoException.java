package org.develop.exceptions;

/**
 * Una excepción personalizada que se utiliza para representar problemas relacionados con objetos Funko
 * en la aplicacion. Puede ser lanzada cuando se encuentran errores o situaciones excepcionales al tratar
 * con objetos Funko.
 */
public class FunkoException extends RuntimeException{

    /**
     * Crea una nueva instancia de FunkoException con el mensaje de error especificado.
     *
     * @param message El mensaje que describe la causa de la excepcion.
     */
    public FunkoException(String message){
        super(message);
    }
}
