package org.develop.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Un adaptador de tipo personalizado para la serializacion y deserializacion de objetos
 * de tipo LocalDate a y desde formato JSON. Este adaptador se utiliza con la libreria Gson.
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    /**
     * Escribe un objeto LocalDate en un JsonWriter.
     *
     * @param out   El escritor JSON.
     * @param value El valor LocalDate a escribir.
     * @throws IOException Si ocurre un error de E/S durante la escritura.
     */
    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(formatter.format(value));
        }
    }

    /**
     * Lee un objeto LocalDate de un JsonReader
     *
     * @param in El lector JSON.
     * @return El objeto LocalDate leido.
     * @throws IOException Si ocurre un error de E/S durante la lectura.
     */
    @Override
    public LocalDate read(JsonReader in) throws IOException {
        return null;
    }

}
