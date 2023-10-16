package org.develop.repositories;

import org.develop.model.Funko;
import org.develop.model.Modelo;
import org.develop.model.MyIDGenerator;
import org.develop.repositories.funkos.FunkoRepository;
import org.develop.repositories.funkos.FunkoRepositoryImpl;
import org.develop.services.database.DatabaseManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class FunkoRepositoryImplTest {

    private FunkoRepository funkoRepository;
    private Funko funko1,funko2;

    @BeforeEach
    void setup() throws SQLException {
        funkoRepository = FunkoRepositoryImpl.getInstance(DatabaseManager.getInstance(), MyIDGenerator.getInstance());
        DatabaseManager.getInstance().initTables();
        funko1 = Funko.builder()
                .uuid(UUID.randomUUID())
                .name("test")
                .modelo(Modelo.OTROS)
                .precio(1.0)
                .fecha_lanzamiento(LocalDate.of(2024,1,20))
                .build();

        funko2 = Funko.builder()
                .uuid(UUID.randomUUID())
                .name("test2")
                .modelo(Modelo.MARVEL)
                .precio(1.5)
                .fecha_lanzamiento(LocalDate.of(2026,4,10))
                .build();
    }

    @AfterEach
    void teardown() throws SQLException{
        DatabaseManager.getInstance().initTables();
    }
    @Test
    void saveTest() {
        Funko fkSave = funkoRepository.save(funko1).block();
        fkSave.setId(1);

        assertAll(
                ()-> assertNotNull(fkSave),
                ()-> assertNotNull(fkSave.getId()),
                ()-> assertNotNull(fkSave.getMyId()),
                ()-> assertEquals(fkSave.getUuid(),funko1.getUuid()),
                ()-> assertEquals(fkSave.getName(),funko1.getName()),
                ()-> assertNotNull(fkSave.getCreated_at()),
                ()-> assertNotNull(fkSave.getUpdated_at())
        );
    }

    @Test
    void updateTest() {
     Funko fknSave = funkoRepository.save(funko1).block();

        fknSave.setName(funko2.getName());
        fknSave.setModelo(funko2.getModelo());
        fknSave.setPrecio(funko2.getPrecio());
        fknSave.setFecha_lanzamiento(LocalDate.now());

        Funko funkoUpdt = funkoRepository.update(fknSave).block();

        assertAll(
                ()-> assertNotNull(funkoUpdt),
                ()-> assertEquals(funko1.getUuid(),funkoUpdt.getUuid()),
                ()-> assertEquals(funko2.getName(),funkoUpdt.getName()),
                ()-> assertEquals(funko2.getModelo(),funkoUpdt.getModelo()),
                ()-> assertEquals(funko2.getPrecio(),funkoUpdt.getPrecio()),
                ()-> assertEquals(LocalDate.now(),funkoUpdt.getFecha_lanzamiento())
        );
    }

    @Test
    void findAllTest() {
        funko1.setId(1);
        funko2.setId(2);

        funkoRepository.save(funko1).block();
        funkoRepository.save(funko2).block();

        List<Funko> list = funkoRepository.findAll().collectList().block();

        assertAll(
                ()-> assertFalse(list.isEmpty()),
                ()-> assertTrue((list.size() == 2)),
                ()-> assertEquals(list.get(0).getId(),funko1.getId()),
                ()-> assertEquals(list.get(1).getId(),funko2.getId())
        );
    }

    @Test
    void findByIdTest() {
    Funko fknSave = funkoRepository.save(funko1).block();
    fknSave.setId(1);
    fknSave.setMyId(1);

    Optional<Funko> fknId =funkoRepository.findById(1).blockOptional();

        assertAll(
            ()-> assertTrue(fknId.isPresent()),
            ()->assertEquals(fknSave.getUuid(),fknId.get().getUuid())
    );
    }

    @Test
    void findByIdErrorTest(){
     Optional<Funko> fknId = funkoRepository.findById(100).blockOptional();
        assertAll(
                ()-> assertTrue(fknId.isEmpty())
        );
    }

    @Test
    void deleteByIdTest() {
        Funko fknSave = funkoRepository.save(funko1).block();
        funkoRepository.deleteById(fknSave.getId()).block();
        Optional<Funko> foundFunko = funkoRepository.findById(fknSave.getId()).blockOptional();
         assertAll(() -> assertFalse(foundFunko.isPresent())
        );
    }

    @Test
    void deleteAllTest() {
        funkoRepository.save(funko1).block();
        funkoRepository.save(funko2).block();

        funkoRepository.deleteAll().block();
        var list = funkoRepository.findAll().collectList().block();
        assertEquals(0,list.size());
    }

    @Test
    void findByNameTest() {
        funko1.setId(1);
        funko1.setMyId(1);
        funkoRepository.save(funko1).block();
        funkoRepository.save(funko2).block();
        var list = funkoRepository.findByName("test").collectList().block();
        System.out.println(list);
        assertAll(
                ()-> assertFalse(list.isEmpty()),
                ()-> assertTrue(list.size() > 1),
                ()-> assertEquals(funko1.getUuid(),list.get(0).getUuid())
     );
    }

    @Test
    void findByUuidTest() {
        funkoRepository.save(funko1).block();
        var funkUUID = funkoRepository.findByUuid(funko1.getUuid()).blockOptional();

        assertAll(
                ()-> assertTrue(funkUUID.isPresent()),
                ()-> assertEquals(funkUUID.get().getUuid(), funko1.getUuid()),
                ()-> assertEquals(funkUUID.get().getName(),funko1.getName())
        );
    }
}