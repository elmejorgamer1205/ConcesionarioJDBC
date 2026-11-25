package service;

import exceptions.AppException;
import exceptions.BBDDException;
import model.Coche;
import model.Extra;
import model.MotorBBDD;
import model.Propietarios;
import utils.CsvUtil;
import utils.InformeUtil;

import java.io.IOException;
import java.sql.*;
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
            throw new BBDDException("Error durante el INSERT: " + e.getMessage());
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
            throw new BBDDException("Error durante el INSERT: " + e.getMessage());
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
        String sql = "SELECT * FROM coches WHERE id_propietario IS NULL";

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
        String sql = "SELECT c.*, p.dni FROM coches c INNER JOIN propietarios p ON c.id_propietario = p.id_propietario";

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

    public void modificarCoche(Coche c) throws BBDDException {
        String sql = "UPDATE coches SET marca = ?, modelo = ?, extras = ?, precio = ? WHERE matricula = ?";

        String extras = "";
        if (c.getExtras() != null && !c.getExtras().isEmpty()) {
            extras = c.getExtras().stream()
                    .map(Extra::getNombre)
                    .collect(Collectors.joining(", "));
        }

        try (Connection connection = ConexionBBDD.getConexion(); PreparedStatement ps = connection.prepareStatement(sql)) {
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

    public void borrarCoche(String matricula) throws BBDDException {
        Connection connection = null;
        try {
            connection = ConexionBBDD.getConexion();
            connection.setAutoCommit(false);

            //Borra primero los traspasos asociados
            String sqlTraspasos = "DELETE FROM traspasos WHERE matricula_coche = ?";
            try (PreparedStatement psTraspasos = connection.prepareStatement(sqlTraspasos)) {
                psTraspasos.setString(1, matricula);
                psTraspasos.executeUpdate();
            }

            //Borra después el coche
            String sqlCoche = "DELETE FROM coches WHERE matricula = ?";
            try (PreparedStatement psCoche = connection.prepareStatement(sqlCoche)) {
                psCoche.setString(1, matricula);
                psCoche.executeUpdate();
            }

            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new BBDDException("Error al borrar el coche: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void realizarTraspaso(String dniComprador, String matricula, double monto) throws BBDDException {
        Connection connection = null;
        try {
            connection = ConexionBBDD.getConexion();
            connection.setAutoCommit(false);

            //Valida el comprador y obtiene su id_propietario
            int idComprador;
            String sqlComprador = "SELECT id_propietario FROM propietarios WHERE dni = ?";
            try (PreparedStatement psComp = connection.prepareStatement(sqlComprador)) {
                psComp.setString(1, dniComprador);
                try (ResultSet rsComp = psComp.executeQuery()) {
                    if (rsComp.next()) {
                        idComprador = rsComp.getInt("id_propietario");
                    } else {
                        throw new BBDDException("No existe ningún propietario con el DNI: " + dniComprador);
                    }
                }
            }

            //Valida el coche y obtiene su dueño actual
            Integer idVendedor = null;
            String sqlCoche = "SELECT id_propietario FROM coches WHERE matricula = ?";
            try (PreparedStatement psCoche = connection.prepareStatement(sqlCoche)) {
                psCoche.setString(1, matricula);
                try (ResultSet rsCoche = psCoche.executeQuery()) {
                    if (rsCoche.next()) {
                        int idProp = rsCoche.getInt("id_propietario");
                        if (!rsCoche.wasNull()) {
                            idVendedor = idProp;
                        }
                    } else {
                        throw new BBDDException("No existe ningún coche con la matrícula: " + matricula);
                    }
                }
            }

            //Actualiza el nuevo propietario del coche
            String sqlUpdate = "UPDATE coches SET id_propietario = ? WHERE matricula = ?";
            try (PreparedStatement psUpdate = connection.prepareStatement(sqlUpdate)) {
                psUpdate.setInt(1, idComprador);
                psUpdate.setString(2, matricula);
                psUpdate.executeUpdate();
            }

            //Registra el historial del traspaso
            String sqlInsert = "INSERT INTO traspasos (matricula_coche, id_vendedor, id_comprador, monto_economico) VALUES (?, ?, ?, ?)";
            try (PreparedStatement psInsert = connection.prepareStatement(sqlInsert)) {
                psInsert.setString(1, matricula);
                if (idVendedor != null) {
                    psInsert.setInt(2, idVendedor);
                } else {
                    psInsert.setNull(2, java.sql.Types.INTEGER);
                }
                psInsert.setInt(3, idComprador);
                psInsert.setDouble(4, monto);
                psInsert.executeUpdate();
            }

            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new BBDDException("No se pudo realizar el traspaso: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void ejecutarProcedimientoMarca(String marca) throws BBDDException {
        if (ConexionBBDD.getMotorActivo() == MotorBBDD.SQLITE) {
            throw new BBDDException("Esta opción solo está disponible en MySQL.");
        }

        String sql = "{call sp_coches_por_marca(?)}";

        try (Connection connection = ConexionBBDD.getConexion();
             CallableStatement cs = connection.prepareCall(sql)) {

            cs.setString(1, marca);

            try (ResultSet rs = cs.executeQuery()) {
                boolean hayResultados = false;
                while (rs.next()) {
                    hayResultados = true;
                    System.out.println(
                                    "Matrícula: " + rs.getString("matricula") +
                                    " | Modelo: " + rs.getString("modelo") +
                                    " | Extras: " + rs.getString("extras") +
                                    " | Precio: " + String.format("%.2f", rs.getDouble("precio")) + "€"
                    );
                }
                if (!hayResultados) {
                    System.out.println("No hay coches registrados de la marca: " + marca);
                }
            }
        } catch (SQLException e) {
            throw new BBDDException("Error al ejecutar sp_coches_por_marca: " + e.getMessage());
        }
    }

    public void generarInforme() throws BBDDException {
        List<String> lineas = new ArrayList<>();

        String sqlStock = "SELECT COUNT(*) AS total, SUM(precio) AS valor FROM coches WHERE id_propietario IS NULL";
        String sqlVendidos = "SELECT COUNT(*) AS total FROM coches WHERE id_propietario IS NOT NULL";
        String sqlPropietarios = "SELECT COUNT(*) AS total FROM propietarios";
        String sqlTraspasos = "SELECT COUNT(*) AS total, SUM(monto_economico) AS ingresos FROM traspasos";
        String sqlMarcas = "SELECT marca, COUNT(*) AS cantidad FROM coches GROUP BY marca";

        try (Connection connection = ConexionBBDD.getConexion()) {

            lineas.add("--- ESTADO DE VEHÍCULOS ---");
            try (PreparedStatement ps = connection.prepareStatement(sqlStock);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lineas.add("Coches en stock (concesionario): " + rs.getInt("total"));
                    lineas.add("Valor total del stock: " + String.format("%.2f", rs.getDouble("valor")) + " €");
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(sqlVendidos);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lineas.add("Coches con propietario: " + rs.getInt("total"));
                }
            }

            lineas.add("");
            lineas.add("--- CLIENTES Y TRASPASOS ---");
            try (PreparedStatement ps = connection.prepareStatement(sqlPropietarios);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lineas.add("Propietarios registrados: " + rs.getInt("total"));
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(sqlTraspasos);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lineas.add("Traspasos realizados: " + rs.getInt("total"));
                    lineas.add("Ingresos por traspasos: " + String.format("%.2f", rs.getDouble("ingresos")) + " €");
                }
            }

            lineas.add("");
            lineas.add("--- COCHES POR MARCA ---");
            try (PreparedStatement ps = connection.prepareStatement(sqlMarcas);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lineas.add("- " + rs.getString("marca") + ": " + rs.getInt("cantidad") + " unidad(es)");
                }
            }

            InformeUtil.escribirInforme(lineas);
            System.out.println("Informe generado correctamente en informe_concesionario.txt");

        } catch (SQLException | IOException e) {
            throw new BBDDException("Error al generar el informe: " + e.getMessage());
        }
    }
}