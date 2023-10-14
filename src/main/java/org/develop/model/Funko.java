package org.develop.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.develop.locale.MyLocale;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Funko {
    private long myId;
    private int id;
    private UUID uuid;
    private String name;
    private Modelo modelo;
    private double precio;
    private LocalDate fecha_lanzamiento;
    private LocalDateTime created_at = LocalDateTime.now();
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
