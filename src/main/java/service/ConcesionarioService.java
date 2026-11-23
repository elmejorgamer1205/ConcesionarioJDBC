package service;

import exceptions.AppException;
import exceptions.BBDDException;
import model.Coche;
import model.Extra;
import model.Propietarios;
import utils.CsvUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ConcesionarioService {

    public void registrarPropietario(Propietarios p) throws BBDDException {
        String sql = "INSERT INTO propietarios (dni, nombre, apellidos, telefono) VALUES (?, ?, ?, ?)";

        try (Connection connection = ConexionBBDD.getConexion(); PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setString(1, p.getDni());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getApellidos());
            ps.setInt(4, p.getTelefono());

            ps.executeUpdate();
            System.out.println("Propietario/s registrado/s correctamente");

        }catch (SQLException e){
            System.out.println("Error durante INSERT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void insertarCoche(Coche c) throws BBDDException{
        String sql = "INSERT INTO coches (matricula, marca, modelo, extras, precio) VALUES (?, ?, ?, ?, ?)";

        String extras = "";
        if (c.getExtras() != null && !c.getExtras().isEmpty()) {
           extras = c.getExtras().stream()
                   .map(Extra::getNombre)
                   .collect(Collectors.joining(", "));
        }

        try (Connection connection = ConexionBBDD.getConexion(); PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setString(1, c.getMatricula());
            ps.setString(2, c.getMaraca());
            ps.setString(3, c.getModelo());
            ps.setString(4, extras);
            ps.setDouble(5, c.getPrecio());

            ps.executeUpdate();
            System.out.println("Coche/s registrado/s correctamente");

        }catch (SQLException e){
            System.out.println("Error durante INSERT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void importarCSV() throws BBDDException{
        String sql = "INSERT INTO coches (matricula, marca, modelo, extras, precio) VALUE (?, ?, ?, ?, ?)";
        Connection connection = null;

        try {
            List<Coche> coches = CsvUtil.leerCochesCSV();

            connection = ConexionBBDD.getConexion();
            connection.setAutoCommit(false);


        }catch (){

        }
    }
}
