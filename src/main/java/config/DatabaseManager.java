package config;

import service.ConexionBBDD;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    public static void DDL() {

        String sqlDropPropietarios = "DROP TABLE IF EXISTS propietarios";
        String sqlCreatePropietarios = "CREATE TABLE propietarios (" +
                " id_propietario INT PRIMARY KEY," +
                " dni VARCHAR(10) NOT NULL," +
                " nombre VARCHAR(100) NOT NULL," +
                " apellidos VARCHAR(150) NOT NULL," +
                " telefono VARCHAR(15)" +
                ")";

        String sqlDropCoches = "DROP TABLE IF EXISTS coches";
        String sqlCreateCoches = "CREATE TABLE coches (" +
                " matricula VARCHAR(10) PRIMARY KEY NOT NULL," +
                " marca VARCHAR(50) NOT NULL," +
                " modelo VARCHAR(50) NOT NULL," +
                " extras VARCHAR(255) NOT NULL," +
                " precio DECIMAL(10,2) NOT NULL," +
                " id_propietario INT," +
                " CONSTRAINT fk_coches_propietario FOREIGN KEY (id_propietario) REFERENCES propietarios(id_propietario)" +
                ")";

        String sqlDropTraspasos = "DROP TABLE IF EXISTS traspasos";
        String sqlCreateTraspasos = "CREATE TABLE traspasos (" +
                " id_traspaso INT PRIMARY KEY," +
                " matricula_coche VARCHAR(10) NOT NULL," +
                " id_vendedor INT," +
                " id_comprador INT NOT NULL," +
                " monto_economico DECIMAL(10,2) NOT NULL," +
                " CONSTRAINT fk_traspasos_coche FOREIGN KEY (matricula_coche) REFERENCES coches(matricula)," +
                " CONSTRAINT fk_traspasos_vendedor FOREIGN KEY (id_vendedor) REFERENCES propietarios(id_propietario)," +
                " CONSTRAINT fk_traspasos_comprador FOREIGN KEY (id_comprador) REFERENCES propietarios(id_propietario)" +
                ")";

        try (Connection connMySQL = ConexionBBDD.getConexionSQL()) {
            ejecutarSentencias(connMySQL, false,
                    sqlDropTraspasos, sqlDropCoches, sqlDropPropietarios,
                    sqlCreatePropietarios, sqlCreateCoches, sqlCreateTraspasos);
            System.out.println("Tablas creadas correctamente en MySQL.");
        } catch (Exception e) {
            System.err.println("Error al crear tablas en MySQL: " + e.getMessage());
        }

        try (Connection connLite = ConexionBBDD.getConexionLite()) {
            ejecutarSentencias(connLite, true,
                    sqlDropTraspasos, sqlDropCoches, sqlDropPropietarios,
                    sqlCreatePropietarios, sqlCreateCoches, sqlCreateTraspasos);
            System.out.println("Tablas creadas correctamente en SQLite.");
        } catch (Exception e) {
            System.err.println("Error al crear tablas en SQLite: " + e.getMessage());
        }
    }

    private static void ejecutarSentencias(Connection conn, boolean esSQLite, String... sentencias) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            if (esSQLite) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
            for (String sql : sentencias) {
                stmt.execute(sql);
            }
        }
    }
}