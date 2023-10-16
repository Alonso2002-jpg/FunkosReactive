package org.develop.services.funkos;

import org.develop.model.Funko;
import org.develop.model.Notificacion;
import reactor.core.publisher.Flux;

public interface FunkoNotification {
    Flux<Notificacion<Funko>> getNotificationAsFlux();

    void notify(Notificacion<Funko> notificacion);
}
