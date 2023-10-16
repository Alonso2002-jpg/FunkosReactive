package org.develop.cache;

import org.develop.model.Funko;
import org.develop.model.Modelo;
import org.develop.services.funkos.FunkoCacheImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class FunkoCacheImplTest {

    private FunkoCacheImpl funkoCache;
    private Funko funko1,funko2;

    @BeforeEach
    void setup(){
        funkoCache = new FunkoCacheImpl(10);

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

    @AfterEach
    void teardown(){
        funkoCache.clear();
    }

    @Test
    void cacheSizeTest(){
        assertEquals(10,funkoCache.getMaxSize());
    }
    @Test
    void putTest() {
        funkoCache.put(funko1.getId(),funko1).block();

        assertAll(
                ()-> assertFalse(funkoCache.getCache().isEmpty()),
                ()-> assertEquals(funkoCache.getCache().size(),1)
        );
    }

    @Test
    void getTest() {
        funkoCache.put(funko1.getId(),funko1).block();
        Optional<Funko> funkoCach = funkoCache.get(funko1.getId()).blockOptional();

        assertAll(
                ()-> assertTrue(funkoCach.isPresent()),
                ()-> assertEquals(funko1.getId(),funkoCach.get().getId()),
                ()-> assertEquals(funko1.getUuid(),funkoCach.get().getUuid())
        );
    }

    @Test
    void removeTest() {
        funkoCache.put(funko1.getId(),funko1).block();
        funkoCache.put(funko2.getId(),funko2).block();
        funkoCache.remove(funko1.getId()).block();

        assertAll(
                ()-> assertFalse(funkoCache.getCache().isEmpty()),
                ()-> assertEquals(funkoCache.getCache().size(),1)
        );
    }


    @Test
    void clearForTimeTest() throws InterruptedException {
        funkoCache.put(funko1.getId(),funko1).block();
        funkoCache.getCleaner().scheduleAtFixedRate(funkoCache::clear,1,1, TimeUnit.MINUTES);
        Thread.sleep(61000);

        assertAll(
                ()-> assertTrue(funkoCache.getCache().isEmpty())
        );
    }

    @Test
    void shutdownTest() {
        funkoCache.shutdown();
        assertTrue(funkoCache.getCleaner().isShutdown());
    }
}