package org.develop;

import org.develop.model.MyIDGenerator;
import org.develop.repositories.funkos.FunkoRepositoryImpl;
import org.develop.services.database.DatabaseManager;
import org.develop.services.files.BackupManagerImpl;
import org.develop.services.funkos.FunkoNotification;
import org.develop.services.funkos.FunkoNotificationImpl;
import org.develop.services.funkos.FunkoServiceImpl;

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

        var impFunkos = funkoService.imported("funkos.csv");

        impFunkos.subscribe( fk -> funkoService.save(fk).subscribe(
                fkIn -> System.out.println("Funko Insertado")
        ));

        var allFunks = funkoService.findAll();
        allFunks.collectList().subscribe(
               System.out::println
        );
    }
}