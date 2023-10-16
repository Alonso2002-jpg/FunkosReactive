package org.develop.repositories.funkos;

import io.r2dbc.pool.ConnectionPool;
import org.develop.model.Funko;
import org.develop.model.Modelo;
import org.develop.model.MyIDGenerator;
import org.develop.services.database.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementacion de la interfaz FunkoRepository que proporciona metodos para interactuar con la base de datos
 * y realizar operaciones CRUD en objetos Funko.
 */
public class FunkoRepositoryImpl implements FunkoRepository{

    private static FunkoRepositoryImpl instance;
    private final Logger logger = LoggerFactory.getLogger(FunkoRepositoryImpl.class);

    private final ConnectionPool connectionFactory;
    private final MyIDGenerator idGenerator;

    private FunkoRepositoryImpl(DatabaseManager databaseManager,MyIDGenerator idGenerator){
        this.connectionFactory = databaseManager.getConnectionPool();
        this.idGenerator = idGenerator;
    }

    /**
     * Obtiene una instancia de la clase FunkoRepositoryImpl. Si no se ha creado una instancia previamente,
     * crea una nueva instancia y la devuelve.
     *
     * @param db         El objeto DatabaseManager para gestionar la conexion a la base de datos.
     * @param idGenerator El generador de identificadores unico para Funkos.
     * @return Una instancia de FunkoRepositoryImpl.
     */
    public static FunkoRepositoryImpl getInstance(DatabaseManager db, MyIDGenerator idGenerator){
        if (instance == null){
            instance= new FunkoRepositoryImpl(db,idGenerator);
        }
        return instance;
    }

    /**
     * Busca y devuelve todos los objetos Funko almacenados en la base de datos.
     *
     * @return Un flujo (Flux) que contiene todos los Funkos encontrados en la base de datos.
     */

    @Override
    public Flux<Funko> findAll() {
        logger.debug("Buscando todos los alumnos");
        String sql = "SELECT * FROM FUNKO";
        return Flux.usingWhen(
                connectionFactory.create(),
                connection -> Flux.from(connection.createStatement(sql).execute())
                        .flatMap(result -> result.map((row, rowMetadata) ->
                                Funko.builder()
                                        .id(row.get("id",Integer.class))
                                        .myId(row.get("myid",Long.class))
                                        .name(row.get("name",String.class))
                                        .uuid(row.get("uuid", UUID.class))
                                        .modelo(Modelo.valueOf(row.get("modelo", Object.class).toString()))
                                        .precio(row.get("precio", Double.class))
                                        .fecha_lanzamiento(row.get("fecha_lanzamiento", LocalDate.class))
                                        .created_at(row.get("created_at", LocalDateTime.class))
                                        .updated_at(row.get("updated_at", LocalDateTime.class))
                                        .build()
                        )),
                Connection::close
        );
    }

    /**
     * Busca un Funko en la base de datos por su ID y devuelve el Funko encontrado, si existe.
     *
     * @param id El ID del Funko que se va a buscar en la base de datos.
     * @return Un mono (Mono) que representa el Funko encontrado por su ID, o un mono vacío si no se encuentra.
     */
    @Override
    public Mono<Funko> findById(Integer id) {
        logger.debug("Buscando Funko por ID");
        String sql = "SELECT * FROM FUNKO WHERE id = ?";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(sql)
                        .bind(0, id)
                        .execute()
                ).flatMap(result -> Mono.from(result.map((row, rowMetadata) ->
                        Funko.builder()
                                        .id(row.get("id",Integer.class))
                                        .myId(row.get("myid",Long.class))
                                        .name(row.get("name",String.class))
                                        .uuid(row.get("uuid", UUID.class))
                                        .modelo(Modelo.valueOf(row.get("modelo", Object.class).toString()))
                                        .precio(row.get("precio",Double.class))
                                        .fecha_lanzamiento(row.get("fecha_lanzamiento", LocalDate.class))
                                        .created_at(row.get("created_at", LocalDateTime.class))
                                        .updated_at(row.get("updated_at", LocalDateTime.class))
                                        .build()
                ))),
                Connection::close
        );
    }

    /**
     * Guarda un Funko en la base de datos y devuelve el Funko guardado.
     *
     * @param funko El Funko que se va a guardar en la base de datos.
     * @return Un mono (Mono) que representa el Funko guardado.
     */
    @Override
    public Mono<Funko> save(Funko funko) {
        logger.debug("Saving Funko on DB");
        String sql = "INSERT INTO FUNKO (myid,uuid,name,modelo,precio,fecha_lanzamiento) VALUES (?,?,?,?,?,?)";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(sql)
                        .bind(0, idGenerator.getIDandIncrement())
                        .bind(1, funko.getUuid())
                        .bind(2, funko.getName())
                        .bind(3,funko.getModelo().toString())
                        .bind(4,funko.getPrecio())
                        .bind(5,funko.getFecha_lanzamiento())
                        .execute()
        ).then(Mono.just(funko)),
        Connection::close
        );
    }

    /**
     * Actualiza un Funko en la base de datos con los nuevos valores proporcionados y devuelve el Funko actualizado.
     *
     * @param funko El Funko con los valores actualizados que se va a guardar en la base de datos.
     * @return Un mono (Mono) que representa el Funko actualizado.
     */
    @Override
    public Mono<Funko> update(Funko funko) {
        logger.debug("Updating Funko on DB");
        String sql = "UPDATE FUNKO SET name = ? , modelo = ?, precio = ?, updated_at = ? WHERE id = ?";
        funko.setUpdated_at(LocalDateTime.now());
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(sql)
                        .bind(0,funko.getName())
                        .bind(1,funko.getModelo().toString())
                        .bind(2,funko.getPrecio())
                        .bind(3,funko.getUpdated_at())
                        .bind(4,funko.getId())
                        .execute()

                ).then(Mono.just(funko)),
                Connection::close
        );
    }

    /**
     * Elimina un Funko de la base de datos por su ID y devuelve un valor booleano que indica si la operacion fue exitosa.
     *
     * @param id El ID del Funko que se va a eliminar de la base de datos.
     * @return Un mono (Mono) que representa `true` si se eliminó con exito, o `false` si no se encontro el Funko.
     */
    @Override
    public Mono<Boolean> deleteById(Integer id) {
        logger.debug("Deleting Funko On DB");
        String sql = "DELETE FROM FUNKO WHERE id = ?";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(sql)
                        .bind(0,id)
                        .execute()
                ).flatMapMany(Result::getRowsUpdated)
                 .hasElements(),
                Connection::close
        );
    }

    /**
     * Elimina todos los Funkos de la base de datos y no devuelve ningun valor.
     *
     * @return Un mono (Mono) que completa la operacion sin devolver un valor.
     */
    @Override
    public Mono<Void> deleteAll() {
        logger.debug("Deleting All Funkos On DB");
        String sql = "DELETE FROM FUNKO";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(sql)
                           .execute()
                        ).then(),
                Connection::close
        );
    }

    /**
     * Busca y devuelve Funkos de la base de datos cuyos nombres contienen la cadena especificada.
     *
     * @param name El nombre o parte del nombre de los Funkos a buscar.
     * @return Un flujo (Flux) que contiene los Funkos encontrados en la base de datos que coinciden con el nombre especificado.
     */
    @Override
    public Flux<Funko> findByName(String name) {
        logger.debug("Finding Funko From DB with Name: " + name);
        String sql = "SELECT * FROM FUNKO WHERE name like ?";
        return Flux.usingWhen(
                connectionFactory.create(),
                connection -> Flux.from(connection.createStatement(sql)
                        .bind(0, "%"+name+"%")
                        .execute()
                ).flatMap(result -> result.map((row,rowMetaData)->
                        Funko.builder()
                                .id(row.get("id",Integer.class))
                                .myId(row.get("myid",Long.class))
                                .uuid(row.get("uuid", UUID.class))
                                .name(row.get("name",String.class))
                                .modelo(Modelo.valueOf(row.get("modelo", Object.class).toString()))
                                .precio(row.get("precio", Double.class))
                                .fecha_lanzamiento(row.get("fecha_lanzamiento",LocalDate.class))
                                .created_at(row.get("created_at",LocalDateTime.class))
                                .updated_at(row.get("updated_at", LocalDateTime.class))
                                .build()
                )),Connection::close
        );
    }

    /**
     * Busca un Funko en la base de datos por su UUID y devuelve el Funko encontrado, si existe.
     *
     * @param uuid El UUID del Funko que se va a buscar en la base de datos.
     * @return Un mono (Mono) que representa el Funko encontrado por su UUID, o un mono vacio si no se encuentra.
     */
    @Override
    public Mono<Funko> findByUuid(UUID uuid) {
        logger.debug("Buscando funko por uuid: " + uuid);
        String sql = "SELECT * FROM FUNKO WHERE uuid = ?";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(sql)
                        .bind(0, uuid)
                        .execute()
                ).flatMap(result -> Mono.from(result.map((row, rowMetadata) ->
                        Funko.builder()
                                        .id(row.get("id",Integer.class))
                                        .myId(row.get("myid",Long.class))
                                        .name(row.get("name",String.class))
                                        .uuid(row.get("uuid", UUID.class))
                                        .modelo(Modelo.valueOf(row.get("modelo", Object.class).toString()))
                                        .precio(row.get("precio",Double.class))
                                        .fecha_lanzamiento(row.get("fecha_lanzamiento", LocalDate.class))
                                        .created_at(row.get("created_at", LocalDateTime.class))
                                        .updated_at(row.get("updated_at", LocalDateTime.class))
                                        .build()
                ))),
                Connection::close
        );
    }
}
