package service;

import exceptions.AppException;
import exceptions.BBDDException;
import model.MotorBBDD;
import utils.PropertiesReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

    public static ConexionBBDD getInstance() throws BBDDException {
        if (instance == null){
            instance = new ConexionBBDD();
        }
        return instance;
    }

    public static MotorBBDD getMotorActivo() {
        return motorActivo;
    }

    public static void setMotorActivo(MotorBBDD motor){
        motorActivo = motor;
    }

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

    private static Connection getConexionSQL() throws SQLException, BBDDException {
        if (instance == null) {
            getInstance();
        }
        if (conexionSQL == null || conexionSQL.isClosed()) {
            conexionSQL = DriverManager.getConnection(url, user, pass);
        }
        return conexionSQL;
    }

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