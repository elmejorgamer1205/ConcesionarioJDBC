package utils;

import exceptions.AppException;
import model.Coche;
import model.Extra;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Leo el fichero CSV y convierto sus filas en coches. */
public class CsvUtil {

    /** Leo los coches definidos en la ruta configurada y los devuelvo en una lista. */
    public static List<Coche> leerCochesCSV() throws AppException, IOException {
        List<Coche> coches = new ArrayList<>();
        String rutaCsv = PropertiesReader.getInstance().get("ruta.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(rutaCsv))) {
            String linea = br.readLine();

            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] datos = linea.split(";");
                String matricula = datos[0].trim();
                String marca = datos[1].trim();
                String modelo = datos[2].trim();
                String extras = datos[3].trim();
                double precio = Double.parseDouble(datos[4].trim());

                List<Extra> listaExtras = new ArrayList<>();

                if (!extras.isEmpty()) {
                    String[] extrasArray = extras.split("\\|");
                    for (String extra : extrasArray) {
                        if (!extra.trim().isEmpty()) {
                            listaExtras.add(new Extra(extra.trim()));
                        }
                    }
                }
                coches.add(new Coche(matricula, marca, modelo, listaExtras, precio, 0));
            }
        }
        return coches;
    }
}
