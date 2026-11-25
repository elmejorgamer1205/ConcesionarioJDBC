package service;

import exceptions.AppException;
import exceptions.BBDDException;
import model.MotorBBDD;
import utils.PropertiesReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Gestiono la conexión activa con SQLite o MySQL usando los datos de configuración. */
public class ConexionBBDD {
    PropertiesReader p;

    private static String url;
    private static String user;
    private static String pass;
    private static String urlLite;

    private static ConexionBBDD instance;
    private static Connection conexionSQL;
    private static Connection conexionLite;
    private static MotorBBDD motorActivo = null;

    /** Cargo las direcciones y credenciales que necesito para conectar. */
    private ConexionBBDD() throws BBDDException {
        try {
            p = PropertiesReader.getInstance();
            url = p.get("db.URL");
            user = p.get("db.USER");
            pass = p.get("db.PASS");
            urlLite = p.get("lite.URL");

        } catch (AppException e) {
            throw new BBDDException("Error en la conexion: "+ e.getMessage());
        }
    }

    /** Devuelvo la instancia única de conexión y la creo si hace falta. */
    public static ConexionBBDD getInstance() throws BBDDException {
        if (instance == null){
            instance = new ConexionBBDD();
        }
        return instance;
    }

    /** Devuelvo el motor de base de datos que está seleccionado. */
    public static MotorBBDD getMotorActivo() {
        return motorActivo;
    }

    /** Selecciono el motor de base de datos que usarán las operaciones. */
    public static void setMotorActivo(MotorBBDD motor){
        motorActivo = motor;
    }

    /** Devuelvo una conexión al motor seleccionado. */
    public static Connection getConexion() throws BBDDException, SQLException {
        if(motorActivo == null) {
            throw new BBDDException("No se ha seleccionado ninguna base de datos.");
        }
        if (motorActivo == MotorBBDD.MYSQL){
            return getConexionSQL();
        }else {
            return getConexionLite();
        }
    }

    /** Abro o reutilizo la conexión con MySQL. */
    private static Connection getConexionSQL() throws SQLException, BBDDException {
        if (instance == null) {
            getInstance();
        }
        if (conexionSQL == null || conexionSQL.isClosed()) {
            conexionSQL = DriverManager.getConnection(url, user, pass);
        }
        return conexionSQL;
    }

    /** Abro o reutilizo la conexión con SQLite. */
    private static Connection getConexionLite() throws SQLException, BBDDException {
        if (instance == null) {
            getInstance();
        }
        if (conexionLite == null || conexionLite.isClosed()) {
            conexionLite = DriverManager.getConnection(urlLite);
        }
        return conexionLite;
    }
}
