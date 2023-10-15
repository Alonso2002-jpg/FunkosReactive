package org.develop.services.database;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.Properties;
import java.util.stream.Collectors;

public class DatabaseManager {

    private static DatabaseManager instance;
    private final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private final ConnectionFactory connectionFactory;
    private final ConnectionPool pool;
    private String serverUrl;
    private String dataBaseName;
    private boolean chargeInit;
    private String conURL;
    private String initScript;

    private DatabaseManager(){
        configFromProperties();

        connectionFactory = ConnectionFactories.get(serverUrl);

        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration
                .builder(connectionFactory)
                .maxIdleTime(Duration.ofMillis(1000))
                .maxSize(20)
                .build();

        pool = new ConnectionPool(configuration);

        if (chargeInit){
            initTables();
        }
    }

    public static DatabaseManager getInstance(){
        if (instance == null){
            instance=new DatabaseManager();
        }
        return instance;
    }

    private synchronized void configFromProperties() {
        try {
            Properties properties = new Properties();
            properties.load(DatabaseManager.class.getClassLoader().getResourceAsStream("config.properties"));

            serverUrl = properties.getProperty("database.url", "jdbc:h2");
            dataBaseName = properties.getProperty("database.name", "Funkos");
            chargeInit = Boolean.parseBoolean(properties.getProperty("database.initDatabase", "false"));
            conURL = properties.getProperty("database.connectionUrl", serverUrl + ":" + dataBaseName + ".db");
            System.out.println(conURL);
            initScript = properties.getProperty("database.initScript", "init.sql");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void initTables(){
        logger.debug("Borrando tablas de la Base de Datos");
        executeScript("delete.sql").block();
        logger.debug("Inicializando tablas de la BD");
        executeScript("init.sql").block();
        logger.debug("Tabla inicializada correctamente");
    }

        public synchronized Mono<Void> executeScript(String script){
            logger.debug("Executing of Init Script: " + script);
                    return Mono.usingWhen(
                connectionFactory.create(),
                connection -> {
                    logger.debug("Creando conexión con la base de datos");
                    String scriptContent = null;
                    try {
                        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(script)) {
                            if (inputStream == null) {
                                return Mono.error(new IOException("No se ha encontrado el fichero de script de inicialización de la base de datos"));
                            } else {
                                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                                    scriptContent = reader.lines().collect(Collectors.joining("\n"));
                                }
                            }
                        }
                        // logger.debug(scriptContent);
                        Statement statement = connection.createStatement(scriptContent);
                        return Mono.from(statement.execute());
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                },
                Connection::close
        ).then();
    }

    public ConnectionPool getConnectionPool(){
        return this.pool;
    }
}
