package org.develop.locale;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * Clase que proporciona funciones para formatear fechas y cantidades de dinero en un formato
 * localizado especifico (en este caso, espanol de Espana).
 */
public class MyLocale {
    private static final Locale locale = new Locale("es","ES");

    /**
     * Convierte una fecha LocalDate en una cadena formateada en el estilo medio localizado.
     *
     * @param date La fecha LocalDate a formatear.
     * @return Una cadena que representa la fecha formateada.
     */
    //Estoy utilizando el objeto Locale creado para definir el formato de fecha y dinero
    //el problema es que no reconoce algunos simbolos.
    public static String toLocalDate(LocalDate date) {
        return date.format(
                DateTimeFormatter
                        .ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
        );
    }

    /**
     * Convierte una cantidad de dinero en una cadena formateada en el formato de moneda localizado.
     *
     * @param money La cantidad de dinero a formatear.
     * @return Una cadena que representa la cantidad de dinero formateada en formato de moneda local.
     */
    public static String toLocalMoney(double money) {
        return NumberFormat.getCurrencyInstance(Locale.getDefault()).format(money);
    }

}