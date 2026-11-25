package utils;

import exceptions.AppException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Leo una sola vez las propiedades de configuración de la aplicación. */
public class PropertiesReader {
    private static final String FILE_NAME = "/config.properties";
    private Properties p;
    private static PropertiesReader instance = null;

    /** Cargo las propiedades desde config.properties. */
    private PropertiesReader() throws AppException {
        p = new Properties();
        try (InputStream lector = PropertiesReader.class.getResourceAsStream(FILE_NAME)) {
            if (lector == null) {
                throw new AppException("El archivo config.properties no se encuentra en src/main/resources.");
            }
            p.load(lector);
        } catch (IOException e) {
            throw new AppException(e.getMessage());
        }
    }

    /** Devuelvo el lector único y lo creo si todavía no existe. */
    public static PropertiesReader getInstance() throws AppException {
        if (instance == null){
            instance = new PropertiesReader();
        }

        return instance;
    }

    /** Devuelvo el valor asociado a la propiedad indicada. */
    public String get(String propiedad){
        return p.getProperty(propiedad);
    }
}
