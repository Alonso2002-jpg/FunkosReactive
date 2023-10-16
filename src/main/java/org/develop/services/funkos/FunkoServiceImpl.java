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

/**
 * Implementacion de la interfaz FunkoService que proporciona operaciones para buscar, guardar, actualizar y eliminar Funkos. Tambien permite realizar operaciones de respaldo e importación. Ademas, gestiona notificaciones relacionadas con los Funkos.
 */
public class FunkoServiceImpl implements FunkoService{
    private static final int CACHE_SIZE = 10;

    private static FunkoServiceImpl instance;
    private final FunkoCache cache;
    private final FunkoNotification notification;
    private final Logger logger = LoggerFactory.getLogger(FunkoServiceImpl.class);
    private final FunkoRepository funkoRepository;
    private final BackupManagerImpl backupManager;

    /**
     * Crea una nueva instancia de FunkoServiceImpl.
     *
     * @param funkoRepository Repositorio de Funkos utilizado para acceder a los datos de los Funkos.
     * @param notification    Servicio de notificacion utilizado para enviar notificaciones relacionadas con los Funkos.
     * @param backupManager   Administrador de respaldo utilizado para realizar operaciones de respaldo e importacion.
     */
    private FunkoServiceImpl(FunkoRepository funkoRepository, FunkoNotification notification,BackupManagerImpl backupManager){
        this.funkoRepository=funkoRepository;
        this.cache = new FunkoCacheImpl(CACHE_SIZE);
        this.notification = notification;
        this.backupManager = backupManager;

    }

    /**
     * Obtiene una instancia de FunkoServiceImpl. Si no existe una instancia previamente creada, se crea una nueva instancia y se la devuelve.
     *
     * @param funkoRepository Repositorio de Funkos utilizado para acceder a los datos de los Funkos.
     * @param notification    Servicio de notificacion utilizado para enviar notificaciones relacionadas con los Funkos.
     * @param backupManager   Administrador de respaldo utilizado para realizar operaciones de respaldo e importacion.
     * @return Instancia de FunkoServiceImpl.
     */
    public static FunkoServiceImpl getInstance(FunkoRepository funkoRepository, FunkoNotification notification,BackupManagerImpl backupManager){
        if (instance==null) instance=new FunkoServiceImpl(funkoRepository,notification,backupManager);

        return instance;
    }

    /**
     * Recupera todos los Funkos disponibles en la base de datos.
     *
     * @return Un flujo de Funkos que representa todos los Funkos disponibles.
     */
    @Override
    public Flux<Funko> findAll() {
        logger.debug("Buscando todos los Funkos");
        return funkoRepository.findAll();
    }

    /**
     * Busca un Funko por su ID.
     *
     * @param id El ID del Funko que se desea buscar.
     * @return Un mono que emite el Funko encontrado, si existe.
     * @throws FunkoNotFoundException Si no se encuentra un Funko con el ID proporcionado.
     */
    @Override
    public Mono<Funko> findById(Integer id) {
        logger.debug("Buscando Funko por ID: " + id);
        return cache.get(id)
                .switchIfEmpty(funkoRepository.findById(id)
                        .flatMap(funko -> cache.put(funko.getId(),funko)
                                .then(Mono.just(funko)))
                        .switchIfEmpty(Mono.error(new FunkoNotFoundException("Funko with id " + id + " not found"))));
    }

    /**
     * Busca Funkos por su nombre.
     *
     * @param name El nombre o parte del nombre de los Funkos que se desean buscar.
     * @return Un flujo de Funkos que representan los Funkos encontrados con el nombre especificado.
     */
    @Override
    public Flux<Funko> findByName(String name) {
        logger.debug("Buscando todos los funkos por nombre: " + name);
        return funkoRepository.findByName(name);
    }

    /**
     * Guarda un nuevo Funko en la base de datos sin generar una notificacion de nueva creacion.
     *
     * @param funko El Funko que se va a guardar.
     * @return Un mono que emite el Funko guardado en la base de datos.
     */
    public Mono<Funko> saveWithOutNotification(Funko funko){
         return funkoRepository.save(funko)
                .flatMap(saved -> funkoRepository.findByUuid(saved.getUuid()));
    }

    @Override
    public Mono<Funko> save(Funko funko) {
        logger.debug("Guardando Funko " + funko.getName());
        return saveWithOutNotification(funko)
                .doOnSuccess(fkSaved -> notification.notify(new Notificacion<>(Notificacion.Tipo.NEW,fkSaved)));
    }

    /**
     * Actualiza un Funko en la base de datos sin generar una notificacion de actualización.
     *
     * @param funko El Funko que se va a actualizar.
     * @return Un mono que emite el Funko actualizado en la base de datos.
     */
    public Mono<Funko> updateWithOutNotification(Funko funko){
        return funkoRepository.update(funko)
                .flatMap(updated->findById(updated.getId()));
    }

    @Override
    public Mono<Funko> update(Funko funko) {
        logger.debug("Actualizando Funko " + funko.getName());
        return updateWithOutNotification(funko)
                .doOnSuccess(fkUpdated->notification.notify(new Notificacion<>(Notificacion.Tipo.UPDATED,fkUpdated)));
    }

    /**
     * Elimina un Funko de la base de datos por su ID sin generar una notificacion de eliminacion.
     *
     * @param id El ID del Funko que se va a eliminar.
     * @return Un mono que emite el Funko eliminado.
     * @throws FunkoNotFoundException Si no se encuentra un Funko con el ID proporcionado.
     */
    public Mono<Funko> deleteByIdWithOutNotification(Integer id){
        return funkoRepository.findById(id)
                .switchIfEmpty(Mono.error(new FunkoNotFoundException("Funko with id " + id + " not found")))
                .flatMap(funko -> cache.remove(funko.getId())
                        .then(funkoRepository.deleteById(funko.getId()))
                        .thenReturn(funko));
    }

    /**
     * Elimina un Funko con el ID especificado y genera una notificacion de eliminacion.
     *
     * @param id El ID del Funko que se eliminara.
     * @return Un Mono que representa el Funko eliminado.
     * @throws FunkoNotFoundException si el Funko con el ID especificado no se encuentra.
     */
    @Override
    public Mono<Funko> deleteById(Integer id) {
        logger.debug("Eliminando Funko con ID " + id);
        return deleteByIdWithOutNotification(id)
                .doOnSuccess(deleted -> notification.notify(new Notificacion<>(Notificacion.Tipo.DELETED,deleted)));
    }


    /**
     * Elimina todos los Funkos de la base de datos y limpia la cache.
     *
     * @return Un Mono que indica que la eliminación se ha completado con exito.
     */
    @Override
    public Mono<Void> deleteAll() {
        logger.debug("Eliminando todos los Funkos");
        cache.clear();
        return funkoRepository.deleteAll()
                .then(Mono.empty());
    }

    /**
     * Realiza una copia de seguridad (backup) de los Funkos y los guarda en un archivo.
     *
     * @param file El nombre del archivo en el que se guardarán los Funkos.
     * @return Un Mono que indica si la copia de seguridad se realizo con exito (true) o no (false).
     */
    @Override
    public Mono<Boolean> backup(String file) {
        logger.debug("Realizando Backup de Funkos");
        return findAll()
                .collectList()
                .flatMap(funkos -> backupManager.writeFile(file,funkos));
    }

    /**
     * Importa Funkos desde un archivo y los emite como un flujo.
     *
     * @param file El nombre del archivo desde el cual importar los Funkos.
     * @return Un flujo de Funkos importados desde el archivo.
     */
    @Override
    public Flux<Funko> imported(String file) {
        return backupManager.readFile(file);
    }

    /**
     * Obtiene un flujo de notificaciones relacionadas con los Funkos. Estas notificaciones pueden incluir informacion sobre nuevas creaciones, actualizaciones o eliminaciones de Funkos.
     *
     * @return Un flujo de notificaciones de Funko.
     */
    public Flux<Notificacion<Funko>> getNotifications(){
        return notification.getNotificationAsFlux();
    }
}
