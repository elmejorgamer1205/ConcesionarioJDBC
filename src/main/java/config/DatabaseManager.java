package config;

public class DatabaseManager {

    private static void DDL(){
        String sqlDropCoches = "DROP TABLE IF EXISTS coches";
        String sqlCreateUsuarios = "CREATE TABLE coches (" +
                " matricula VARCHAR(10) PRIMARY KEY NOT NULL," +
                " marca VARCHAR(50) NOT NULL," +
                " modelo VARCHAR(50) NOT NULL," +
                " extras VARCHAR(255) NOT NULL," +
                " precio DECIMAL(10,2) NOT NULL," +
                " id_propietario INT";

        String sqlDropPropietarios = "DROP TABLE IF EXISTS propietarios";
        String sqlCreatePropietarios = "CREATE TABLE propietarios (" +
                " id_propietario INT PRIMARY KEY," +
                " dni VARCHAR(10) NOT NULL," +
                " nombre VARCHAR(100) NOT NULL," +
                " apellidos VARCHAR(150) NOT NULL" +
                " telefono VARCHAR(15)" +
                ")";

        String sqlDropTraspasos = "DROP TABLE IF EXISTS traspasos";
        String sqlCreateTraspasos = "CREATE TABLE traspasos (" +
                " id_traspaso INT PRIMARY KEY," +
                " matricula_coche VARCHAR(10) NOT NULL," +
                " id_vendedor INT," +
                " id_comprador INT NOT NULL," +
                " monto_economico DECIMAL(10,2) NOT NULL," +
                " CONSTRAINT fk_traspasos_coche" +
                " FOREGIN KEY (matricula_coche) REFERENCES coches(matricula)," +
                " CONSTRAINT fk_traspasos_vendedor" +
                " FOREGIN KEY (id_vendedor) REFERENCES propietarios(id_propietarios)" +
                " CONSTRAINT fk_traspasos_comprador" +
                " FOREGIN KEY (id_comprador) REFERENCES propietarios(id_propietarios)" +
                ")";
    }
}
