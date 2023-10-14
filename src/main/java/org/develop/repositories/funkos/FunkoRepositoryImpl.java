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

public class FunkoRepositoryImpl implements FunkoRepository{

    private static FunkoRepositoryImpl instance;
    private final Logger logger = LoggerFactory.getLogger(FunkoRepositoryImpl.class);

    private final ConnectionPool connectionFactory;
    private final MyIDGenerator idGenerator;

    private FunkoRepositoryImpl(DatabaseManager databaseManager,MyIDGenerator idGenerator){
        this.connectionFactory = databaseManager.getConnectionPool();
        this.idGenerator = idGenerator;
    }

    public static FunkoRepositoryImpl getInstance(DatabaseManager db, MyIDGenerator idGenerator){
        if (instance == null){
            instance= new FunkoRepositoryImpl(db,idGenerator);
        }
        return instance;
    }
    @Override
    public Flux<Funko> findAll() {
        logger.debug("Buscando todos los alumnos");
        String sql = "SELECT * FROM ALUMNOS";
        return Flux.usingWhen(
                connectionFactory.create(),
                connection -> Flux.from(connection.createStatement(sql).execute())
                        .flatMap(result -> result.map((row, rowMetadata) ->
                                Funko.builder()
                                        .id(row.get("id",Integer.class))
                                        .myId(row.get("my_id",Long.class))
                                        .name(row.get("name",String.class))
                                        .uuid(row.get("uuid", UUID.class))
                                        .modelo(row.get("modelo", Modelo.class))
                                        .precio(row.get("precio",Double.class))
                                        .fecha_lanzamiento(row.get("fecha_lanzamiento", LocalDate.class))
                                        .created_at(row.get("created_at", LocalDateTime.class))
                                        .updated_at(row.get("updated_at", LocalDateTime.class))
                                        .build()
                        )),
                Connection::close
        );
    }

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
                                        .myId(row.get("my_id",Long.class))
                                        .name(row.get("name",String.class))
                                        .uuid(row.get("uuid", UUID.class))
                                        .modelo(row.get("modelo", Modelo.class))
                                        .precio(row.get("precio",Double.class))
                                        .fecha_lanzamiento(row.get("fecha_lanzamiento", LocalDate.class))
                                        .created_at(row.get("created_at", LocalDateTime.class))
                                        .updated_at(row.get("updated_at", LocalDateTime.class))
                                        .build()
                ))),
                Connection::close
        );
    }

    @Override
    public Mono<Funko> save(Funko funko) {
        logger.debug("Saving Funko on DB");
        String sql = "INSERT INTO FUNKO (myid,uuid.name,modelo,precio,fecha_lanzamiento) VALUES (?,?,?,?,?,?)";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(sql)
                        .bind(0, idGenerator.getIDandIncrement())
                        .bind(1, funko.getUuid())
                        .bind(2, funko.getName())
                        .bind(3,funko.getModelo())
                        .bind(4,funko.getPrecio())
                        .bind(5,funko.getFecha_lanzamiento())
                        .execute()
        ).then(Mono.just(funko)),
        Connection::close
        );
    }

    @Override
    public Mono<Funko> update(Funko funko) {
        logger.debug("Updating Funko on DB");
        String sql = "UPDATE FUNKO SET name = ? , modelo = ?, precio = ?, updated_at = ? WHERE id = ?";
        funko.setUpdated_at(LocalDateTime.now());
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(sql)
                        .bind(0,funko.getName())
                        .bind(1,funko.getModelo())
                        .bind(2,funko.getPrecio())
                        .bind(3,funko.getUpdated_at())
                        .execute()

                ).then(Mono.just(funko)),
                Connection::close
        );
    }

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
                                .modelo(row.get("modelo", Modelo.class))
                                .fecha_lanzamiento(row.get("fecha_lanzamiento",LocalDate.class))
                                .created_at(row.get("created_at",LocalDateTime.class))
                                .updated_at(row.get("updated_at", LocalDateTime.class))
                                .build()
                )),Connection::close
        );
    }
}
