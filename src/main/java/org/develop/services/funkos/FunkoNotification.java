package org.develop.services.funkos;

import org.develop.model.Funko;
import org.develop.model.Notificacion;
import reactor.core.publisher.Flux;

/**
 * Interfaz que define metodos para obtener notificaciones de objetos Funko y para notificar eventos relacionados con Funko.
 * Las notificaciones son encapsuladas en objetos de tipo Notificacion que contienen informacion sobre el evento y el objeto Funko asociado.
 */
public interface FunkoNotification {

    /**
     * Obtiene notificaciones como un flujo (Flux) de objetos de tipo Notificacion&lt;Funko&gt;.
     *
     * @return Un flujo (Flux) de notificaciones de objetos Funko.
     */
    Flux<Notificacion<Funko>> getNotificationAsFlux();

    /**
     * Notifica un evento relacionado con un objeto Funko encapsulado en un objeto Notificacion.
     *
     * @param notificacion Objeto Notificacion que contiene la informacion del evento y el objeto Funko asociado.
     */
    void notify(Notificacion<Funko> notificacion);
}
