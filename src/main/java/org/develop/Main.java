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

    }
}