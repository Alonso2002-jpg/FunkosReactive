package org.develop.services.funkos;

import org.develop.exceptions.FunkoNotFoundException;
import org.develop.model.Funko;
import org.develop.model.Notificacion;
import org.develop.repositories.funkos.FunkoRepository;
import org.develop.services.files.BackupManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class FunkoServiceImpl implements FunkoService{
    private static final int CACHE_SIZE = 10;

    private static FunkoServiceImpl instance;
    private final FunkoCache cache;
    private final FunkoNotification notification;
    private final Logger logger = LoggerFactory.getLogger(FunkoServiceImpl.class);
    private final FunkoRepository funkoRepository;
    private final BackupManagerImpl backupManager;

    private FunkoServiceImpl(FunkoRepository funkoRepository, FunkoNotification notification,BackupManagerImpl backupManager){
        this.funkoRepository=funkoRepository;
        this.cache = new FunkoCacheImpl(CACHE_SIZE);
        this.notification = notification;
        this.backupManager = backupManager;

    }

    public static FunkoServiceImpl getInstance(FunkoRepository funkoRepository, FunkoNotification notification,BackupManagerImpl backupManager){
        if (instance==null) instance=new FunkoServiceImpl(funkoRepository,notification,backupManager);

        return instance;
    }
    @Override
    public Flux<Funko> findAll() {
        logger.debug("Buscando todos los Funkos");
        return funkoRepository.findAll();
    }

    @Override
    public Mono<Funko> findById(Integer id) {
        logger.debug("Buscando Funko por ID: " + id);
        return cache.get(id)
                .switchIfEmpty(funkoRepository.findById(id)
                        .flatMap(funko -> cache.put(funko.getId(),funko)
                                .then(Mono.just(funko)))
                        .switchIfEmpty(Mono.error(new FunkoNotFoundException("Funko with id " + id + " not found"))));
    }

    @Override
    public Flux<Funko> findByName(String name) {
        logger.debug("Buscando todos los funkos por nombre: " + name);
        return funkoRepository.findByName(name);
    }

    @Override
    public Mono<Funko> save(Funko funko) {
        logger.debug("Guardando Funko " + funko.getName());
        return funkoRepository.save(funko)
                .flatMap(saved -> funkoRepository.findByUuid(saved.getUuid()))
                .doOnSuccess(fkSaved -> notification.notify(new Notificacion<>(Notificacion.Tipo.NEW,fkSaved)));
    }

    @Override
    public Mono<Funko> update(Funko funko) {
        logger.debug("Actualizando Funko " + funko.getName());
        return funkoRepository.update(funko)
                .flatMap(updated->findById(updated.getId()))
                .doOnSuccess(fkUpdated->notification.notify(new Notificacion<>(Notificacion.Tipo.UPDATED,fkUpdated)));
    }

    @Override
    public Mono<Funko> deleteById(Integer id) {
        logger.debug("Eliminando Funko con ID " + id);
        return funkoRepository.findById(id)
                .switchIfEmpty(Mono.error(new FunkoNotFoundException("Funko with id " + id + " not found")))
                .flatMap(funko -> cache.remove(funko.getId())
                        .then(funkoRepository.deleteById(funko.getId()))
                        .thenReturn(funko))
                .doOnSuccess(deleted -> notification.notify(new Notificacion<>(Notificacion.Tipo.DELETED,deleted)));
    }

    @Override
    public Mono<Void> deleteAll() {
        logger.debug("Eliminando todos los Funkos");
        cache.clear();
        return funkoRepository.deleteAll()
                .then(Mono.empty());
    }

    @Override
    public Mono<Boolean> backup(String file) {
        logger.debug("Realizando Backup de Funkos");
        return findAll()
                .collectList()
                .flatMap(funkos -> backupManager.writeFile(file,funkos));
    }

    @Override
    public Flux<Funko> imported(String file) {
        return backupManager.readFile(file);
    }

    public Flux<Notificacion<Funko>> getNotifications(){
        return notification.getNotificationAsFlux();
    }
}
