package org.develop.model;

import lombok.Builder;
import lombok.Data;
import org.develop.locale.MyLocale;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Clase que representa un Funko, un objeto coleccionable relacionado con figuras
 * de vinilo. Cada Funko tiene un identificador inico, un nombre, un modelo, un precio,
 * una fecha de lanzamiento y fechas de creacion y actualizacion.
 *
 * @author Alonso Cruz, Joselyn Obando
 */
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

    /**
     * Convierte el Funko en una representacion de cadena.
     *
     * @return Una cadena que representa el Funko.
     */
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

    /**
     * Establece los atributos del Funko a partir de una cadena dada.
     *
     * @param line La cadena que contiene los atributos del Funko en un formato especifico.
     * @return El objeto Funko con los atributos establecidos.
     */
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
