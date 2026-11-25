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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConcesionarioService {

    public void registrarPropietario(Propietarios p) throws BBDDException {
        String sql = "INSERT INTO propietarios (dni, nombre, apellidos, telefono) VALUES (?, ?, ?, ?)";

        try (Connection connection = ConexionBBDD.getConexion(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, p.getDni());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getApellidos());
            ps.setInt(4, p.getTelefono());

            ps.executeUpdate();
            System.out.println("Propietario/s registrado/s correctamente");

        } catch (SQLException e) {
            System.out.println("Error durante INSERT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void insertarCoche(Coche c) throws BBDDException {
        String sql = "INSERT INTO coches (matricula, marca, modelo, extras, precio) VALUES (?, ?, ?, ?, ?)";

        String extras = "";
        if (c.getExtras() != null && !c.getExtras().isEmpty()) {
            extras = c.getExtras().stream()
                    .map(Extra::getNombre)
                    .collect(Collectors.joining(", "));
        }

        try (Connection connection = ConexionBBDD.getConexion(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, c.getMatricula());
            ps.setString(2, c.getMaraca());
            ps.setString(3, c.getModelo());
            ps.setString(4, extras);
            ps.setDouble(5, c.getPrecio());

            ps.executeUpdate();
            System.out.println("Coche/s registrado/s correctamente");

        } catch (SQLException e) {
            System.out.println("Error durante INSERT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void importarCSV() throws BBDDException {
        String sql = "INSERT INTO coches (matricula, marca, modelo, extras, precio) VALUES (?, ?, ?, ?, ?)";
        Connection connection = null;

        try {
            List<Coche> coches = CsvUtil.leerCochesCSV();

            connection = ConexionBBDD.getConexion();
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                for (Coche c : coches) {
                    String extras = "";
                    if (c.getExtras() != null && !c.getExtras().isEmpty()) {
                        extras = c.getExtras().stream()
                                .map(Extra::getNombre)
                                .collect(Collectors.joining(", "));
                    }

                    ps.setString(1, c.getMatricula());
                    ps.setString(2, c.getMaraca());
                    ps.setString(3, c.getModelo());
                    ps.setString(4, extras);
                    ps.setDouble(5, c.getPrecio());
                    ps.executeUpdate();
                }
            }

            connection.commit();
            System.out.println("Se ha importado " + coches.size() + " coche/s correctamente ");

        } catch (AppException | SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    System.out.println("Importación cancelada, se ha realizado Rolback");
                } catch (SQLException e1) {
                    System.out.println("Error al hacer Rollback" + e1.getMessage());
                }
            }
            throw new BBDDException("Error al importar coches desde el csv: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar la conexión con la base de datos: " + e.getMessage());
                }
            }
        }
    }

    public List<Coche> listarCochesConcesionario() throws BBDDException {
        List<Coche> coches = new ArrayList<>();
        String sql = "SELECT * FROM coches WHERE id_propietarios IS NULL";

        try (Connection connection = ConexionBBDD.getConexion(); PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String matricula = rs.getString("matricula");
                String marca = rs.getString("marca");
                String modelo = rs.getString("modelo");
                String extra = rs.getString("extras");
                double precio = rs.getDouble("precio");

                List<Extra> extras = new ArrayList<>();
                if (extra != null && !extra.trim().isEmpty()) {
                    for (String e : extra.split(",")) {
                        if (!e.trim().isEmpty()) {
                            extras.add(new Extra(e.trim()));
                        }
                    }
                }
                coches.add(new Coche(matricula, marca, modelo, extras, precio, 0));
            }
        } catch (SQLException e) {
            throw new BBDDException("Error al listar coches del concesionario: " + e.getMessage());
        }
        return coches;
    }

    public List<String> listarCochesPropietarios() throws BBDDException {
        List<String> resultados = new ArrayList<>();
        String sql = "SELECT c.*, p.dni FROM coches c INNER JOIN propietarios p ON c.id_propietario = p.id";

        try (Connection connection = ConexionBBDD.getConexion(); PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String matricula = rs.getString("matricula");
                String marca = rs.getString("marca");
                String modelo = rs.getString("modelo");
                String extras = rs.getString("extras");
                double precio = rs.getDouble("precio");
                String dni = rs.getString("dni");

                String infoExtras = (extras != null && !extras.trim().isEmpty()) ? extras : "Sin extras";

                resultados.add("Matrícula: " + matricula + " | Marca: " + marca + " | Modelo: " + modelo +
                        " | Extras: " + infoExtras + " | Precio: " + precio + "€ | Propietario DNI: " + dni);
            }
        } catch (SQLException e) {
            throw new BBDDException("Error al listar coches con propietarios: " + e.getMessage());
        }
        return resultados;
    }

    public void modificarCoche(Coche c) throws BBDDException{
        String sql = "UPDATE coches SET marca = ?, modelo = ?, extras = ?, precio = ? WHERE matricula = ?";

        String extras = "";
        if (c.getExtras() != null && !c.getExtras().isEmpty()) {
            extras = c.getExtras().stream()
                    .map(Extra::getNombre)
                    .collect(Collectors.joining(", "));
        }

        try (Connection connection = ConexionBBDD.getConexion(); PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, c.getMaraca());
            ps.setString(2, c.getModelo());
            ps.setString(3, extras);
            ps.setDouble(4, c.getPrecio());
            ps.setString(5, c.getMatricula());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new BBDDException("Error al modificar el/los coche/s: " + e.getMessage());
        }
    }

    public void borrarCoche(String matricula) throws BBDDException{
        String sql = "DELETE FROM coches WHERE matricula = ?";

        try (Connection connection = ConexionBBDD.getConexion(); PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, matricula);
            ps.executeUpdate();
        }catch (SQLException e){
            throw new BBDDException("Error al borrar el coche: " + e.getMessage());
        }
    }

    public void realizarTraspaso(String dniComprador, String matricula, double monto) throws BBDDException{
        int idComprador;
        String sqlComprador = "SELECT id FROM propietarios WHERE dni = ?";
        try (Connection connection = ConexionBBDD.getConexion(); PreparedStatement ps = connection.prepareStatement(sqlComprador)){

        }catch (SQLException e){
            throw new BBDDException("Hubo un error con el propietario: " + e.getMessage());
        }
    }

    public void ejecutarProcedimientoMarca() throws BBDDException{
        String sql = "";
    }

    public void generarInforme() throws BBDDException{
        String sql = "";
    }
}
