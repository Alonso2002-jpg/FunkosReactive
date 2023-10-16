package org.develop.services.funkos;

import org.develop.model.Funko;
import org.develop.model.Notificacion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

/**
 * Implementacion de la interfaz FunkoNotification que proporciona funcionalidad para obtener notificaciones relacionadas con objetos Funko y notificar eventos relacionados con Funko.
 */
public class FunkoNotificationImpl implements FunkoNotification{
    private static FunkoNotificationImpl instance;
    private final Flux<Notificacion<Funko>> funkosNotificationFlux;
    private FluxSink<Notificacion<Funko>> funkosNotification;

    /**
     * Constructor privado para garantizar el patron Singleton.
     * Inicializa el flujo (Flux) de notificaciones de Funko.
     */
    private FunkoNotificationImpl(){
        this.funkosNotificationFlux = Flux.<Notificacion<Funko>> create(emitter -> this.funkosNotification = emitter).share();
    }

    /**
     * Obtiene una instancia de FunkoNotificationImpl utilizando el patrón Singleton.
     *
     * @return Instancia de FunkoNotificationImpl.
     */
    public static FunkoNotificationImpl getInstance(){
        if (instance == null) instance= new FunkoNotificationImpl();
        return instance;
    }

    /**
     * Obtiene notificaciones como un flujo (Flux) de objetos de tipo Notificacion&lt;Funko&gt;.
     *
     * @return Un flujo (Flux) de notificaciones de objetos Funko.
     */
    @Override
    public Flux<Notificacion<Funko>> getNotificationAsFlux() {
        return funkosNotificationFlux;
    }

    /**
     * Notifica un evento relacionado con un objeto Funko encapsulado en un objeto Notificacion.
     *
     * @param notificacion Objeto Notificacion que contiene la informacion del evento y el objeto Funko asociado.
     */
    @Override
    public void notify(Notificacion<Funko> notificacion) {
        funkosNotification.next(notificacion);
    }
}
