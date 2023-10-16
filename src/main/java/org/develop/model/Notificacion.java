package org.develop.model;

/**
 * Clase que representa una notificacion genérica que puede contener cualquier tipo de contenido.
 *
 * @param <T> El tipo de contenido que la notificacion llevara.
 */
public class Notificacion <T>{
    private Tipo tipo;
    private T contenido;

    /**
     * Crea una nueva instancia de Notificacion con un tipo y contenido especificados.
     *
     * @param tipo      El tipo de notificacion (NEW, UPDATED, DELETED, u otro).
     * @param contenido El contenido asociado a la notificacion.
     */
    public Notificacion(Tipo tipo, T contenido) {
        this.tipo = tipo;
        this.contenido = contenido;
    }

    /**
     * Obtiene el tipo de la notificacion.
     *
     * @return El tipo de la notificacion.
     */
    public Tipo getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de la notificacion.
     *
     * @param tipo El tipo de la notificacion.
     */
    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene el contenido de la notificacion.
     *
     * @return El contenido de la notificacion.
     */
    public T getContenido() {
        return contenido;
    }

    /**
     * Establece el contenido de la notificacion.
     *
     * @param contenido El contenido de la notificacion.
     */
    public void setContenido(T contenido) {
        this.contenido = contenido;
    }

    /**
     * Devuelve una representacion en cadena de la notificacion en el formato:
     * "Notificacion{tipo=TIPO, contenido=CONTENIDO}".
     *
     * @return Una cadena que representa la notificacion.
     */
    @Override
    public String toString() {
        return "Notificacion{" +
                "tipo=" + tipo +
                ", contenido=" + contenido +
                '}';
    }


    /**
     * Enumeracion que define los posibles tipos de notificacion.
     */
    public enum Tipo {
        NEW, UPDATED, DELETED
    }
}
