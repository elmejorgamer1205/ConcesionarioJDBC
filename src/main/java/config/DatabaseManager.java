package config;

public class DatabaseManager {

    private static void DDL(){
        String sqlDropCoches = "DROP TABLE IF EXISTS coches";
        String sqlCreateUsuarios = "CREATE TABLE coches (" +
                " matricula VARCHAR(10) PRIMARY KEY NOT NULL," +
                " marca VARCHAR(50) NOT NULL UNIQUE," +
                " modelo VARCHAR(50) NOT NULL UNIQUE," +
                " extras VARCHAR(255) NOT NULL UNIQUE," +
                " precio DECIMAL(10,2) NOT NULL UNIQUE," +
                " id_propietario INT";
    }
}
