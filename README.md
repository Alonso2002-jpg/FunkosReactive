# Proyecto FUNKOS REACTIVE - Java con H2
***
Este proyecto es una aplicación simple en Java que utiliza H2 como base de datos. A continuación, se describen los pasos para configurar y ejecutar el proyecto.
## Requisitos
***
* Java 8 o superior
* Gradle
## Configuración
***

### Paso 1: Dependencias de Gradle
Agrega las siguientes dependencias a tu archivo `build.gradle`:

```kotlin
plugins {
    id("java")
}

group = "org.develop"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
     // Project Reactor
    implementation("io.projectreactor:reactor-core:3.5.10")
    // Para test: https://www.baeldung.com/reactive-streams-step-verifier-test-publisher
    // NO lo vamos a usar, pero lo dejo por si acaso
    // testImplementation("io.projectreactor:reactor-test:3.5.10")

    // R2DBC
    implementation("io.r2dbc:r2dbc-h2:1.0.0.RELEASE")
    implementation("io.r2dbc:r2dbc-pool:1.0.0.RELEASE")

   implementation("com.google.code.gson:gson:2.10.1")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
    testImplementation("org.mockito:mockito-core:5.5.0")

    implementation("org.projectlombok:lombok:1.18.28")
    testImplementation("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")

    implementation("ch.qos.logback:logback-classic:1.4.11")

}

tasks.test {
    useJUnitPlatform()
}
```
## Model
***
### Paso 2: Crear un enum Modelo
Crea un enum `Modelo` con sus cuatro valores enumerados `MARVEL`, `DISNEY`,`ANIME` y `OTROS`.

```java 
package org.develop.model;

public enum Modelo {
    MARVEL,DISNEY,ANIME,OTROS;
}
```
### Paso 3: Crear la clase Funko
Crea una clase `Funko` con los atributos `myId`, `id`, UUID`uuid`, `name`, Modelo `modelo`, `precio`, LocalDate `fecha_lanzamiento`, LocalDateTime `created_at`, LocalDateTime `updated_at`, tambien creamos metodo `toString` es la representacion en forma de cadena del objeto Funko, muestra los valores de algunos campos importantes.
Utilizamos `setFunko(String line)` para configurar un objeto Funko a partir de una cadena de datos `line`.

```java
package org.develop.model;

import lombok.Builder;
import lombok.Data;
import org.develop.locale.MyLocale;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Builder
public class Funko {
    private long myId;
    private int id;
    private UUID uuid;
    private String name;
    private Modelo modelo;
    private double precio;
    private LocalDate fecha_lanzamiento;
    @Builder.Default
    private LocalDateTime created_at = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updated_at = LocalDateTime.now();
    @Override
    public String toString() {
        return "Funko{" +
                "id=" + id +
                ", myid=" + myId +
                ", uuid=" + uuid +
                ", name='" + name + '\'' +
                ", modelo=" + modelo +
                ", precio=" + MyLocale.toLocalMoney(precio) +
                ", fecha_lanzamiento=" + MyLocale.toLocalDate(fecha_lanzamiento) +
                '}';
    }

    public Funko setFunko(String line){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String[] lineas = line.split(",");
        setUuid(UUID.fromString(lineas[0].length()>36?lineas[0].substring(0,35):lineas[0]));
        setName(lineas[1]);
        setModelo(Modelo.valueOf(lineas[2]));
        setPrecio(Double.parseDouble(lineas[3]));
        setFecha_lanzamiento(LocalDate.parse(lineas[4],formatter));

        return this;
    }
}
```
### Paso 4: Crear la clase MyIDGenerator
Crea una clase `MyIDGenerator`, creamos una instance static tipo `MyIDGenerator` con los atributos `id`, un locker tipo `Lock` que se utiliza para sincronizar el acceso a la variable `id`. Se utiliza un `ReentrantLock` que garantiza que solo un hilo a la vez pueda incrementar el ID.
Tenemos dos metodos ´getInstance´ este método estático se utiliza para obtener la única instancia de MyIDGenerator. Si no existe una instancia previa, se crea una y se devuelve.
Esto garantiza que siempre se utilice la misma instancia de generador de IDs en toda la aplicación. Ademas `getIDandIncrement` este método incrementa el valor del ID y lo devuelve. Antes de incrementar el ID, adquiere un bloqueo a través del objeto locker para garantizar que la operación sea atómica y que no se produzcan condiciones de carrera si varios hilos intentan obtener un ID al mismo tiempo.
El proposito de esta clase es proporcionar un mecanismo seguro y unico para generar IDs incrementales que pueden utilizarse en otras partes de la aplicacion para identificar objetos, transacciones o cualquier otra entidad que requiera un ID unico y creciente. El uso de un Singleton y un bloqueo garantiza que no se produzcan colisiones de IDs y que se mantenga la integridad de los valores de ID.

```java
package org.develop.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyIDGenerator {

    private static MyIDGenerator instance;
    private static long id = 0;

    private static final Lock locker = new ReentrantLock(true);

    private MyIDGenerator(){}

    public static MyIDGenerator getInstance(){
        if (instance == null){
            instance = new MyIDGenerator();
        }
        return instance;
    }

    public Long getIDandIncrement(){
        locker.lock();
        id++;
        locker.unlock();
        return id;
    }
}
```
### Paso 5: Crear la clase Notificacion
Creamos la clase `Notificacion` se utiliza para representar notificaciones con un tipo y un contenido genérico, lo que la hace flexible y útil en diversas situaciones donde necesites notificar eventos, como la creación, actualización o eliminación de elementos, y desees especificar el tipo y el contenido de la notificación de manera genérica.
La clase tiene dos atributos:
`tipo`: Un atributo que indica el tipo de la notificación. Puede ser uno de los valores definidos en el enum Tipo, que incluye NEW (nuevo), UPDATED (actualizado) y DELETED (eliminado).
`contenido`: Un atributo que almacena el contenido de la notificación, que puede ser de cualquier tipo especificado al crear una instancia de la clase Notificacion.

```java
package org.develop.model;

public class Notificacion <T>{
    private Tipo tipo;
    private T contenido;
    public Notificacion(Tipo tipo, T contenido) {
        this.tipo = tipo;
        this.contenido = contenido;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public T getContenido() {
        return contenido;
    }

    public void setContenido(T contenido) {
        this.contenido = contenido;
    }

    @Override
    public String toString() {
        return "Notificacion{" +
                "tipo=" + tipo +
                ", contenido=" + contenido +
                '}';
    }

    public enum Tipo {
        NEW, UPDATED, DELETED
    }
}
```

## Adapters
***
### Paso 6: Crear la clase LocalDateAdapter
Crea una clase `LocalDateAdapter` es una clase personalizada que extiende `TypeAdapter` de la biblioteca Gson, que se utiliza para convertir objetos LocalDate en JSON y viceversa.
la clase LocalDateAdapter se utiliza para personalizar cómo se representan las fechas LocalDate al escribir objetos JSON utilizando la biblioteca Gson. El formato de fecha en JSON será "año-mes-día" cuando se utilice esta clase para serializar objetos `LocalDate`. Ten en cuenta que si necesitas también la capacidad de deserializar fechas desde JSON a objetos `LocalDate`.
```java
package org.develop.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(formatter.format(value));
        }
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        return null;
    }

}

```
### Paso 7: Crear la clase LocalDateTimeAdapter
La clase `LocalDateTimeAdapter` es una clase personalizada que extiende TypeAdapter de la biblioteca Gson y se utiliza para convertir objetos LocalDateTime a JSON y viceversa.
El método `write` toma un objeto `LocalDateTime` y lo convierte en una representación en formato JSON.
Si el valor de LocalDateTime es nulo, se escribe un valor nulo en el flujo JSON utilizando out.nullValue().
Si el valor de LocalDateTime no es nulo, se formatea la fecha y hora utilizando el patrón "yyyy-MM-dd" y se escribe en el flujo JSON con out.value(formatter.format(value)). Esto significa que la fecha y hora se representarán en JSON como una cadena con el formato "año-mes-día".
El método `read` no está implementado en esta clase, y siempre devuelve null. Esto significa que esta clase solo se utiliza para la escritura (serialización) de objetos LocalDateTime a JSON y no se encarga de la lectura (deserialización) desde JSON a objetos LocalDateTime.

```java
package org.develop.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(formatter.format(value));
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        return null;
    }
}
```

## Locale
***
### Paso 8: Crear la clase MyLocale
La clase MyLocale proporciona dos métodos estáticos para formatear fechas y dinero en función de un objeto Locale específico. En este caso, estás utilizando un objeto Locale con el idioma español (es) y la región España (ES).
El metodo `toLocalDate(LocalDate date)` este método toma un objeto LocalDate como entrada y formatea la fecha en un formato localizado, utilizando las configuraciones del Locale predeterminado. La fecha se formatea en un estilo medio (FormatStyle.MEDIUM), que se adapta al formato local. El objeto Locale utilizado para la formatear la fecha es el Locale predeterminado del sistema. Esto significa que la fecha se formateará en función de las configuraciones de idioma y región del sistema en el que se ejecute la aplicación.
`toLocalMoney(double money)` este método toma un valor numérico como entrada y lo formatea en una representación de moneda localizada. Utiliza el objeto Locale predeterminado del sistema para determinar el formato de moneda. Esto garantiza que la cantidad de dinero se formatee según las configuraciones de idioma y región del sistema.

```java
package org.develop.locale;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class MyLocale {
    private static final Locale locale = new Locale("es","ES");

    //Estoy utilizando el objeto Locale creado para definir el formato de fecha y dinero
    //el problema es que no reconoce algunos simbolos.
    public static String toLocalDate(LocalDate date) {
        return date.format(
                DateTimeFormatter
                        .ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
        );
    }

    public static String toLocalMoney(double money) {
        return NumberFormat.getCurrencyInstance(Locale.getDefault()).format(money);
    }

}
```
## Exceptions
***
### Paso 9: Crear la clase FunkoException
La clase `FunkoException` una clase de excepción personalizada que extiende la clase `RuntimeException`.
`FunkoException(String message)`: La clase tiene un constructor que toma un mensaje como argumento. Este mensaje debería ser una descripción o información adicional sobre la excepción que se está lanzando. Cuando se crea una instancia de FunkoException con un mensaje, ese mensaje se pasa a la clase base RuntimeException (superclase) usando super(message). Esto significa que el mensaje se almacena en la excepción y se puede recuperar más tarde cuando se capture y maneje la excepción.
```java
package org.develop.exceptions;

public class FunkoException extends RuntimeException{
    public FunkoException(String message){
        super(message);
    }
}
```
### Paso 10: Crear la clase FunkoNotFoundException

La clase `FunkoNotFoundException` es una subclase de la clase `FunkoException`, y su propósito es manejar excepciones específicas relacionadas con la situación en la que no se encuentra un objeto `Funko` dentro de tu aplicación. Esta clase extiende `FunkoException`, que a su vez extiende RuntimeException.
```java
package org.develop.exceptions;

public class FunkoNotFoundException extends FunkoException{

    public FunkoNotFoundException(String message) {
        super(message);
    }
}
```
### Paso 11: Crear la clase FunkoNotSaveException
La clase `FunkoNotSaveException` es otra subclase de la clase `FunkoException` y se utiliza para manejar excepciones específicas relacionadas con la incapacidad de guardar un objeto `Funko` en tu aplicación o sistema. Al igual que la clase `FunkoNotFoundException`, esta clase extiende `FunkoException`, que, a su vez, extiende `RuntimeException`.
```java
package org.develop.exceptions;

public class FunkoNotSaveException extends FunkoException{
    public FunkoNotSaveException(String message) {
        super(message);
    }
}
```
## Servicios de Almacenamiento - DATABASE
***
### Paso 12: Crear la clase DatabaseManager
Creamos la clase `DatabaseManager` esta se encarga de la administración de conexiones y operaciones en una base de datos, proporcionando funcionalidades como la inicialización de tablas, la ejecución de scripts y la gestión de conexiones de base de datos.
```java
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

        connectionFactory = ConnectionFactories.get(conURL);

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
```
## Repositories - CRUD
***
### Paso 13: CRUDRepository
Creamos la interfaz CRUDRepository que define un conjunto de métodos genéricos para realizar operaciones básicas de administración de datos (CRUD) en una fuente de datos, como una base de datos o cualquier otra colección de elementos. La implementación de esta interfaz permitirá realizar estas operaciones CRUD en un tipo específico de datos, proporcionando flexibilidad y reutilización en el diseño de aplicaciones.

```java
package org.develop.repositories.crud;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CRUDRepository <T,ID>{
     // Métodos que vamos a usar
    // Buscar todos
    Flux<T> findAll();

    // Buscar por ID
    Mono<T> findById(ID id);

    // Guardar
    Mono<T> save(T t);

    // Actualizar
    Mono<T> update(T t);

    // Borrar por ID
    Mono<Boolean> deleteById(ID id);

    // Borrar todos
    Mono<Void> deleteAll();
}
```
## Repositories - FUNKOS
***
### Paso 14: FunkoRepository
Creamos la interfaz `FunkoRepository` que proporciona métodos específicos para la gestión de Funkos, además de heredar operaciones de CRUD genéricas de la interfaz CRUDRepository. Esta estructura facilita la gestión y consulta de objetos Funko en una aplicación, ya que define un contrato claro para realizar operaciones comunes en una fuente de datos que almacena Funkos.
```java
package org.develop.repositories.funkos;

import org.develop.model.Funko;
import org.develop.repositories.crud.CRUDRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FunkoRepository extends CRUDRepository<Funko, Integer> {

    Flux<Funko> findByName(String name);
    Mono<Funko> findByUuid(UUID uuid);
}

```
### Paso 15: FunkoRepositoryImpl
Creamos la implementación de `FunkoRepository` proporciona una forma de gestionar `Funkos` en una base de datos utilizando programación reactiva y `R2DBC`. Los métodos implementados permiten realizar operaciones de búsqueda, inserción, actualización y eliminación de Funkos en la base de datos de manera eficiente y reactiva.
```java
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
                                .modelo(Modelo.valueOf(row.get("modelo", Object.class).toString()))
                                .precio(row.get("precio", Double.class))
                                .fecha_lanzamiento(row.get("fecha_lanzamiento",LocalDate.class))
                                .created_at(row.get("created_at",LocalDateTime.class))
                                .updated_at(row.get("updated_at", LocalDateTime.class))
                                .build()
                )),Connection::close
        );
    }

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
```
## Servicios de Almacenamiento - FILES
***
### Paso 16: Crear la clase BackupManager
Creamos la interfaz `BackupManager<T>` que define métodos para la lectura y escritura de archivos que contienen datos de tipo T. Esta interfaz es genérica, lo que significa que puedes utilizarla para gestionar archivos que almacenan diferentes tipos de datos.
```java
package org.develop.services.files;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BackupManager <T>{

    Flux<T> readFile(String path);

    Mono<Boolean> writeFile(String path, List<T> list);
}
```
### Paso 17: Crear la clase BackupManagerImpl
Creamos la clase `BackupManagerImpl` que proporciona una implementación específica para la lectura y escritura de datos de objetos Funko en diferentes formatos de archivo (CSV y JSON). La implementación utiliza GSON para la serialización y deserialización de objetos y está diseñada para funcionar de manera reactiva mediante el uso de tipos de Reactor (Flux y Mono).
```java
package org.develop.services.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.develop.adapters.LocalDateAdapter;
import org.develop.adapters.LocalDateTimeAdapter;
import org.develop.model.Funko;
import org.develop.model.Modelo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class BackupManagerImpl implements BackupManager<Funko> {
    private static BackupManagerImpl instance;
    private Logger logger = LoggerFactory.getLogger(BackupManagerImpl.class);

    private BackupManagerImpl(){

    }

    public static BackupManagerImpl getInstance(){
        if (instance == null) instance=new BackupManagerImpl();

        return instance;
    }
    @Override
    public Flux<Funko> readFile(String nomFile) {
        logger.debug("Leyendo fichero CSV");
        String path = Paths.get("").toAbsolutePath().toString() + File.separator + "data" + File.separator + nomFile;
        return Flux.create(sink->{
        try(BufferedReader reader =new BufferedReader(new FileReader(path))){
                String line;
                String[] lineas;
                reader.readLine();
                while ((line = reader.readLine()) != null){
                        Funko fk = Funko.builder().build();
                        fk.setFunko(line);
                    sink.next(fk);
                }
                sink.complete();
        }catch (Exception e){
                System.out.println(e.getMessage());
        }
        });
    }

    @Override
    public Mono<Boolean> writeFile(String nomFile, List<Funko> list) {
        logger.debug("Escribiendo fichero JSON");
        String path = Paths.get("").toAbsolutePath().toString() + File.separator + "data" + File.separator + nomFile;
        return Mono.create(sink ->{
                   Gson gs = new GsonBuilder()
                            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                            .setPrettyPrinting()
                            .create();

                    boolean success = false;
                    try (FileWriter writer = new FileWriter(path)) {
                        gs.toJson(list, writer);
                        success = true;
                    } catch (Exception e) {
                        logger.error("Error: "+e.getMessage(), e);
                }
                    sink.success(success);
                }
                );
    }
}
```
## Servicios de Almacenamiento - FUNKOS
***
### Paso 18: FunkoCache
Creamos la interfaz ´FunkoCache´ que  extiende la interfaz Cache y se utiliza para definir métodos específicos para el almacenamiento en caché y recuperación de objetos Funko utilizando claves de tipo Integer. La implementación concreta de esta interfaz deberá proporcionar la funcionalidad para administrar la caché de Funkos de acuerdo con las necesidades del sistema.
```java
package org.develop.services.funkos;

import org.develop.model.Funko;
import org.develop.services.cache.Cache;

public interface FunkoCache extends Cache<Integer, Funko> {
}

```
### Paso 19: FunkoCacheImpl
Creamos la clase `FunkoCacheImpl` es una implementación de caché para objetos Funko. Permite almacenar objetos Funko en una estructura de datos con un tamaño máximo y un mecanismo de limpieza automática para eliminar objetos caducados. La implementación es reactiva, ya que utiliza tipos de Reactor (Mono) para interactuar con la caché.
```java
package org.develop.services.funkos;

import lombok.Getter;
import org.develop.exceptions.FunkoNotFoundException;
import org.develop.model.Funko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FunkoCacheImpl implements FunkoCache{
    private final Logger logger = LoggerFactory.getLogger(FunkoCacheImpl.class);
    @Getter
    private final int maxSize;
    @Getter
    private final Map<Integer, Funko> cache;
    @Getter
    private final ScheduledExecutorService cleaner;

    public FunkoCacheImpl(int maxSize){
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<>(maxSize,0.75f,true){
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Funko> eldest) {
                return size() > maxSize;
            }
        };

        this.cleaner = Executors.newSingleThreadScheduledExecutor();
        this.cleaner.scheduleAtFixedRate(this::clear,2,2, TimeUnit.MINUTES);
    }
    @Override
    public Mono<Void> put(Integer key, Funko value) {
        logger.debug("Añadinedo Funko en la Cache id: " + key);
        return Mono.fromRunnable(()->cache.put(key,value));
    }

    @Override
    public Mono<Funko> get(Integer key) {
        logger.debug("Obteniendo Funko de la Cache con id: " + key);
        return Mono.justOrEmpty(cache.get(key));
    }

    @Override
    public Mono<Void> remove(Integer key) {
        logger.debug("Eliminando Funko de la Cache con id: " + key);
        return Mono.fromRunnable(()-> cache.remove(key));
    }

    @Override
    public void clear() {
        cache.entrySet().removeIf(entry -> {
            boolean shouldRemove = entry.getValue().getUpdated_at().plusMinutes(1).isBefore(LocalDateTime.now());
            if (shouldRemove) {
                logger.debug("Autoeliminando por caducidad funko de cache con id: " + entry.getKey());
            }
            return shouldRemove;
        });
    }


    @Override
    public void shutdown() {
        cleaner.shutdown();
    }
}
```
### Paso 20: FunkoNotification
Creamos la interfaz `FunkoNotification` proporciona métodos para obtener notificaciones relacionadas con objetos Funko a través de un Fluxy para enviar notificaciones a otras partes del sistema. Esto es útil cuando necesitas comunicar eventos y cambios relacionados con los objetosFunko dentro de una aplicación o sistema.
```java
package org.develop.services.funkos;

import org.develop.model.Funko;
import org.develop.model.Notificacion;
import reactor.core.publisher.Flux;

public interface FunkoNotification {
    Flux<Notificacion<Funko>> getNotificationAsFlux();

    void notify(Notificacion<Funko> notificacion);
}
```
### Paso 21: FunkoNotificationImpl
Creamos la clase `FunkoNotificationImpl` proporciona una implementación de notificaciones reactivas relacionadas con objetos Funko. Esta implementación permite a otras partes del sistema suscribirse a notificaciones de objetos Funko y recibir actualizaciones en tiempo real cuando ocurran eventos relacionados con estos objetos.
```java
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

```
### Paso 22: FunkoService
Creamos la interfaz `FunkoService` define métodos relacionados con la gestión de objetos Funko.
Estos métodos permiten realizar operaciones de búsqueda, inserción, actualización, eliminación y copia de seguridad de objetos `Funko dentro de una aplicación o sistema. Las implementaciones concretas de esta interfaz deberán proporcionar la funcionalidad para interactuar con la base de datos o el almacenamiento correspondiente.
```java
package org.develop.services.funkos;

import org.develop.model.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface FunkoService {
        // Buscar todos
    Flux<Funko> findAll();

    // Buscar por ID
    Mono<Funko> findById(Integer id);
    //Buscar por nombre
    Flux<Funko> findByName(String name);

    // Guardar
    Mono<Funko> save(Funko funko);

    // Actualizar
    Mono<Funko> update(Funko funko);

    // Borrar por ID
    Mono<Funko> deleteById(Integer id);

    // Borrar todos
    Mono<Void> deleteAll();

    Mono<Boolean> backup(String file);

    Flux<Funko> imported(String file);

}

```
### Paso 23: FunkoServiceImpl
Creamos la clase `FunkoServiceImpl` es una implementación de servicios que permite buscar, guardar, actualizar y eliminar objetos Funko, además de gestionar notificaciones y realizar operaciones de copia de seguridad y restauración de datos. La clase utiliza una caché para mejorar el rendimiento al acceder a los datos y proporciona notificaciones en tiempo real relacionadas con los objetos Funko.
```java
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


    public Mono<Funko> deleteByIdWithOutNotification(Integer id){
        return funkoRepository.findById(id)
                .switchIfEmpty(Mono.error(new FunkoNotFoundException("Funko with id " + id + " not found")))
                .flatMap(funko -> cache.remove(funko.getId())
                        .then(funkoRepository.deleteById(funko.getId()))
                        .thenReturn(funko));
    }
    @Override
    public Mono<Funko> deleteById(Integer id) {
        logger.debug("Eliminando Funko con ID " + id);
        return deleteByIdWithOutNotification(id)
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
```
Y luego en tu main

```java
package org.develop;

import org.develop.model.Funko;
import org.develop.model.Modelo;
import org.develop.model.MyIDGenerator;
import org.develop.repositories.funkos.FunkoRepositoryImpl;
import org.develop.services.database.DatabaseManager;
import org.develop.services.files.BackupManagerImpl;
import org.develop.services.funkos.FunkoNotification;
import org.develop.services.funkos.FunkoNotificationImpl;
import org.develop.services.funkos.FunkoServiceImpl;
import reactor.core.publisher.Flux;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        FunkoServiceImpl funkoService = FunkoServiceImpl.getInstance(FunkoRepositoryImpl.getInstance(DatabaseManager.getInstance(), MyIDGenerator.getInstance()), FunkoNotificationImpl.getInstance(), BackupManagerImpl.getInstance());
        System.out.println("Sistema de Notificaciones");

        funkoService.getNotifications().subscribe(
                notificacion -> {
                    switch (notificacion.getTipo()) {
                        case NEW:
                            System.out.println("🟢 Alumno insertado: " + notificacion.getContenido());
                            break;
                        case UPDATED:
                            System.out.println("🟠 Alumno actualizado: " + notificacion.getContenido());
                            break;
                        case DELETED:
                            System.out.println("🔴 Alumno eliminado: " + notificacion.getContenido());
                            break;
                    }
                },
                error -> System.err.println("Se ha producido un error: " + error),
                () -> System.out.println("Completado")
        );

        //Importando Funkos
        var impFunkos = funkoService.imported("funkos.csv");

        //Guardando Funkos en la BD (Solo 20)
        impFunkos
                .subscribe( fk -> funkoService.save(fk).subscribe(
                fkIn -> System.out.println("Funko Insertado")
        ));

//        //Obteniendo Funko con id: 6
//        var funkID = funkoService.findById(6);
//        funkID.subscribe(fkn -> {
//            System.out.println("Funko obtenido de la BD: " + fkn);
//            fkn.setName("Update Funko");
//            fkn.setPrecio(100.0);
//            //Actualizando Funko con id: 6
//            funkoService.update(fkn).subscribe(
//                fknUpd -> System.out.println("Funko Actualizando: " + fknUpd)
//        );});
//
//        //Eliminando Funko con id: 6
//        var delID = funkoService.deleteById(6);
//        delID.subscribe(fknDel -> System.out.println("Funko Eliminado : " + fknDel));
//
//        //Obteniendo 10 Funkos de la BD
//        var allFunks = funkoService.findAll();
//        allFunks.take(10)
//                .subscribe(System.out::println);
//
//        //Obteniendo Funkos con nombre: Stitch
//        funkoService.findByName("Stitch").subscribe(System.out::println);
//
//        //Haciendo Backup de Funkos
//        funkoService.backup("funkosBack.json").subscribe(succes -> System.out.println("Realizado? " + succes));
//
//        //Eliminando todos los Funkos de la BD
//        funkoService.deleteAll().subscribe();
//
//        //Obteniendo los Funkos de la BD (Vacia)
//        funkoService.findAll().subscribe(System.out::println);

        //Consultas de Funkos Reactivas
        //Funko mas caro
          funkoService.findAll()
                .collectList()
                .map(funkos -> funkos.stream()
                    .max(Comparator.comparingDouble(Funko::getPrecio))
                    .orElse(Funko.builder().build())
                )
                .subscribe(mostExpensive -> {
                    System.out.println("Funko mas Caro");
                    System.out.println(mostExpensive);
                });

        //Media de precios de Funkos
        funkoService.findAll()
                .map(Funko::getPrecio)
                .collect(Collectors.averagingDouble(precio->precio))
                .subscribe(avg -> System.out.println("La media de precios es: " +avg));

        //Funkos agrupados por Modelo
        funkoService.findAll()
            .collectMultimap(Funko::getModelo)
            .flatMapMany(map -> Flux.fromIterable(map.entrySet()))
            .subscribe(entry -> {
                Modelo modelo = entry.getKey();
                List<Funko> funkos = (List<Funko>) entry.getValue();
                System.out.println("Modelo: " + modelo);
                System.out.println("Funkos: " + funkos);
            });
        //Numero de Funkos por Modelo
        funkoService.findAll()
                .map(Funko::getModelo)
                .collect(Collectors.groupingBy(fk->fk, Collectors.counting()))
                .flatMapMany(map -> Flux.fromIterable(map.entrySet()))
                .subscribe(entry ->{
                    Modelo modelo = entry.getKey();
                    Long count = entry.getValue();
                    System.out.println(modelo + ": " + count);
                        }
                );
        //Funkos Lanzados en el 2023
        System.out.println("Funkos Lanzados en el 2023");
        funkoService.findAll()
                .filter(fk -> fk.getFecha_lanzamiento().toString().contains("2023"))
                .subscribe(System.out::println);

        //Numero de Funkos de Stitch
        funkoService.findByName("Stitch")
                .count()
                .subscribe(count -> System.out.println("Numero de Funkos de Stitch : " + count));

        //Funkos de Stitch
        funkoService.findByName("Stitch")
                .subscribe(System.out::println);

    }
}
```