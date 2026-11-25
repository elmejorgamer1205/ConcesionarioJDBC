package config;

import model.MotorBBDD;
import service.ConexionBBDD;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** Creo las tablas y procedimientos que necesita el concesionario en la base de datos activa. */
public class DatabaseManager {

    /** Preparo el DDL y creo las estructuras si todavía no existen. */
    public static void DDL() {
        boolean esSQLite = (ConexionBBDD.getMotorActivo() == MotorBBDD.SQLITE);
        String autoInc = esSQLite ? "INTEGER PRIMARY KEY AUTOINCREMENT" : "INT PRIMARY KEY AUTO_INCREMENT";

        String sqlCreatePropietarios = "CREATE TABLE IF NOT EXISTS propietarios (" +
                " id_propietario " + autoInc + "," +
                " dni VARCHAR(10) NOT NULL UNIQUE," +
                " nombre VARCHAR(100) NOT NULL," +
                " apellidos VARCHAR(150) NOT NULL," +
                " telefono VARCHAR(15)" +
                ")";

        String sqlCreateCoches = "CREATE TABLE IF NOT EXISTS coches (" +
                " matricula VARCHAR(10) PRIMARY KEY NOT NULL," +
                " marca VARCHAR(50) NOT NULL," +
                " modelo VARCHAR(50) NOT NULL," +
                " extras VARCHAR(255) NOT NULL," +
                " precio DECIMAL(10,2) NOT NULL," +
                " id_propietario INT," +
                " CONSTRAINT fk_coches_propietario FOREIGN KEY (id_propietario) REFERENCES propietarios(id_propietario)" +
                ")";

        String sqlCreateTraspasos = "CREATE TABLE IF NOT EXISTS traspasos (" +
                " id_traspaso " + autoInc + "," +
                " matricula_coche VARCHAR(10) NOT NULL," +
                " id_vendedor INT," +
                " id_comprador INT NOT NULL," +
                " monto_economico DECIMAL(10,2) NOT NULL," +
                " CONSTRAINT fk_traspasos_coche FOREIGN KEY (matricula_coche) REFERENCES coches(matricula)," +
                " CONSTRAINT fk_traspasos_vendedor FOREIGN KEY (id_vendedor) REFERENCES propietarios(id_propietario)," +
                " CONSTRAINT fk_traspasos_comprador FOREIGN KEY (id_comprador) REFERENCES propietarios(id_propietario)" +
                ")";

        try (Connection conn = ConexionBBDD.getConexion()) {
            ejecutarSentencias(conn, esSQLite,
                    sqlCreatePropietarios, sqlCreateCoches, sqlCreateTraspasos);
            System.out.println("Tablas creadas correctamente en " + ConexionBBDD.getMotorActivo() + ".");
        } catch (Exception e) {
            System.err.println("Error al crear tablas en " + ConexionBBDD.getMotorActivo() + ": " + e.getMessage());
        }
    }

    /** Ejecuto las sentencias SQL, activo claves foráneas en SQLite y creo el procedimiento en MySQL. */
    private static void ejecutarSentencias(Connection conn, boolean esSQLite, String... sentencias) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            if (esSQLite) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
            for (String sql : sentencias) {
                stmt.execute(sql);
            }
            if (!esSQLite) {
                stmt.execute("DROP PROCEDURE IF EXISTS sp_coches_por_marca");
                stmt.execute("CREATE PROCEDURE sp_coches_por_marca(IN p_marca VARCHAR(50)) " +
                        "BEGIN " +
                        "   SELECT * FROM coches WHERE marca = p_marca; " +
                        "END");
            }
        }
    }
}