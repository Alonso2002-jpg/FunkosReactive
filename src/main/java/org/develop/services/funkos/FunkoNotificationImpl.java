package org.develop.services.funkos;

import org.develop.model.Funko;
import org.develop.model.Notificacion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

public class FunkoNotificationImpl implements FunkoNotification{
    private static FunkoNotificationImpl instance;
    private final Flux<Notificacion<Funko>> funkosNotificationFlux;
    private FluxSink<Notificacion<Funko>> funkosNotification;

    private FunkoNotificationImpl(){
        this.funkosNotificationFlux = Flux.<Notificacion<Funko>> create(emitter -> this.funkosNotification = emitter).share();
    }

    public static FunkoNotificationImpl getInstance(){
        if (instance == null) instance= new FunkoNotificationImpl();
        return instance;
    }
    @Override
    public Flux<Notificacion<Funko>> getNotificationAsFlux() {
        return funkosNotificationFlux;
    }

    @Override
    public void notify(Notificacion<Funko> notificacion) {
        funkosNotification.next(notificacion);
    }
}
