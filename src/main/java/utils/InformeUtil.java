package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/** Escribo el informe del concesionario en un fichero de texto. */
public class InformeUtil {

    /** Guardo las líneas recibidas junto con el encabezado y la fecha del informe. */
    public static void escribirInforme(List<String> lineas) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("informe_concesionario.txt"))) {
            bw.write("==========================================");
            bw.newLine();
            bw.write("   INFORME RESUMEN DEL CONCESIONARIO");
            bw.newLine();
            bw.write("   Fecha: " + LocalDate.now());
            bw.newLine();
            bw.write("==========================================");
            bw.newLine();
            bw.newLine();

            for (String linea : lineas) {
                bw.write(linea);
                bw.newLine();
            }

            bw.newLine();
            bw.write("==========================================");
        }
    }
}
