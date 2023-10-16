package org.develop.backupManager;

import org.develop.model.Funko;
import org.develop.model.Modelo;
import org.develop.services.files.BackupManager;
import org.develop.services.files.BackupManagerImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BackupManagerImplTest {

    private BackupManagerImpl backupManager;
    private Funko funko1, funko2;

    @BeforeEach
    void setup(){
        backupManager = BackupManagerImpl.getInstance();

        funko1 = Funko.builder()
                .id(1)
                .uuid(UUID.randomUUID())
                .name("test")
                .modelo(Modelo.OTROS)
                .precio(1.0)
                .fecha_lanzamiento(LocalDate.of(2024,1,20))
                .build();

        funko2 = Funko.builder()
                .id(2)
                .uuid(UUID.randomUUID())
                .name("test2")
                .modelo(Modelo.MARVEL)
                .precio(1.5)
                .fecha_lanzamiento(LocalDate.of(2026,4,10))
                .build();
    }


    @Test
    void writeFile() {
        var listFunks = List.of(funko1,funko2);
        File file = new File(Paths.get("").toAbsolutePath().toString() + File.separator + "data" + File.separator + "testFunkos.json");

        boolean res = backupManager.writeFile(file.getName(),listFunks).block();

        assertAll(
                ()-> assertTrue(res),
                ()-> assertTrue(file.exists())
        );
    }

    @Test
    void readFile() {

    var listFunks = backupManager.readFile("funkos.csv").collectList().block();

        assertAll(
                ()-> assertNotNull(listFunks),
                ()-> assertFalse(listFunks.isEmpty()),
                ()-> assertEquals(listFunks.size(),90)
        );
    }
}