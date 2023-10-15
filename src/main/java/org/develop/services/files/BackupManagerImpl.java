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
        logger.debug("Escribiendo fichero JSON");
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
