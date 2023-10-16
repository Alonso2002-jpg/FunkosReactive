package org.develop.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Un adaptador de tipo personalizado para la serializacion y deserializacion de objetos
 * de tipo LocalDateTime a y desde formato JSON. Este adaptador se utiliza con la libreria Gson.
 */
public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");



    /**
     * Escribe un objeto LocalDateTime en un JsonWriter.
     *
     * @param out   El escritor JSON.
     * @param value El valor LocalDateTime a escribir.
     * @throws IOException Si ocurre un error de E/S durante la escritura.
     */
    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(formatter.format(value));
        }
    }

    /**
     * Lee un objeto LocalDateTime de un JsonReader.
     *
     * @param in El lector JSON.
     * @return El objeto LocalDateTime leído.
     * @throws IOException Si ocurre un error de E/S durante la lectura.
     */
    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        return null;
    }
}
