package main;

import config.DatabaseManager;
import exceptions.BBDDException;
import model.Coche;
import model.Extra;
import model.MotorBBDD;
import model.Propietarios;
import service.ConcesionarioService;
import service.ConexionBBDD;
import utils.InformeUtil;
import utils.MiEntradaSalida;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class main {
    private static final ConcesionarioService SERVICE = new ConcesionarioService();

    public static void main(String[] args) {
        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            int opcion = MiEntradaSalida.leerEntero("Selecciona una opción: ");
            try {
                switch (opcion) {
                    case 1 -> conectar();
                    case 2 -> DatabaseManager.DDL();
                    case 3 -> registrarPropietario();
                    case 4 -> insertarCoche(null);
                    case 5 -> SERVICE.importarCSV();
                    case 6 -> listarConcesionario();
                    case 7 -> listarPropietarios();
                    case 8 -> modificarCoche();
                    case 9 -> SERVICE.borrarCoche(MiEntradaSalida.leerLinea("Matrícula: ").trim());
                    case 10 -> realizarTraspaso();
                    case 11 -> SERVICE.ejecutarProcedimientoMarca(MiEntradaSalida.leerLinea("Marca: ").trim());
                    case 12 -> generarInforme();
                    case 0 -> salir = true;
                    default -> System.out.println("Opción no válida.");
                }
            } catch (BBDDException | SQLException | IOException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
            if (!salir) System.out.println();
        }
        System.out.println("Hasta pronto.");
    }

    private static void mostrarMenu() {
        System.out.println("=== CONCESIONARIO ===");
        System.out.println("1. Conectar BBDD (SQLite/MySQL)");
        System.out.println("2. Inicializar/Crear tablas");
        System.out.println("3. Registrar propietario");
        System.out.println("4. Insertar coche");
        System.out.println("5. Importar coches (CSV)");
        System.out.println("6. Listar coches del concesionario");
        System.out.println("7. Listar coches con propietario");
        System.out.println("8. Modificar coche");
        System.out.println("9. Borrar coche");
        System.out.println("10. Realizar traspaso");
        System.out.println("11. Ejecutar procedimiento por marca (MySQL)");
        System.out.println("12. Generar informe resumen");
        System.out.println("0. Salir");
    }

    private static void conectar() throws SQLException, BBDDException {
        System.out.println("Motor: 1. SQLite  2. MySQL");
        int motor = MiEntradaSalida.leerEntero("Selecciona el motor: ");
        if (motor != 1 && motor != 2) {
            System.out.println("Opción no válida.");
            return;
        }
        ConexionBBDD.setMotorActivo(motor == 1 ? MotorBBDD.SQLITE : MotorBBDD.MYSQL);
        try (Connection ignored = ConexionBBDD.getConexion()) {
            System.out.println("Conexión correcta: " + ConexionBBDD.getMotorActivo());
            System.out.println("Configuración cargada desde config.properties.");
        }
    }

    private static void registrarPropietario() throws BBDDException {
        String dni = MiEntradaSalida.leerLinea("DNI: ").trim();
        String nombre = MiEntradaSalida.leerLinea("Nombre: ").trim();
        String apellidos = MiEntradaSalida.leerLinea("Apellidos: ").trim();
        int telefono = MiEntradaSalida.leerEntero("Teléfono: ");
        SERVICE.registrarPropietario(new Propietarios(0, dni, nombre, apellidos, telefono));
    }

    private static void insertarCoche(String matricula) throws BBDDException {
        if (matricula == null) matricula = MiEntradaSalida.leerLinea("Matrícula: ").trim();
        String marca = MiEntradaSalida.leerLinea("Marca: ").trim();
        String modelo = MiEntradaSalida.leerLinea("Modelo: ").trim();
        List<Extra> extras = leerExtras();
        double precio = MiEntradaSalida.leerDouble("Precio: ");
        SERVICE.insertarCoche(new Coche(matricula, marca, modelo, extras, precio, 0));
    }

    private static void modificarCoche() throws BBDDException {
        String matricula = MiEntradaSalida.leerLinea("Matrícula del coche a modificar: ").trim();
        String marca = MiEntradaSalida.leerLinea("Nueva marca: ").trim();
        String modelo = MiEntradaSalida.leerLinea("Nuevo modelo: ").trim();
        List<Extra> extras = leerExtras();
        double precio = MiEntradaSalida.leerDouble("Nuevo precio: ");
        SERVICE.modificarCoche(new Coche(matricula, marca, modelo, extras, precio, 0));
        System.out.println("Coche actualizado.");
    }

    private static List<Extra> leerExtras() {
        List<Extra> extras = new ArrayList<>();
        for (String extra : MiEntradaSalida.leerListaTexto("Extras (separados por coma, vacío si no hay): ", ","))
            extras.add(new Extra(extra));
        return extras;
    }

    private static void listarConcesionario() throws BBDDException {
        List<Coche> coches = SERVICE.listarCochesConcesionario();
        if (coches.isEmpty()) System.out.println("No hay coches en el concesionario.");
        else coches.forEach(System.out::println);
    }

    private static void listarPropietarios() throws BBDDException {
        List<String> coches = SERVICE.listarCochesPropietarios();
        if (coches.isEmpty()) System.out.println("No hay coches con propietario.");
        else coches.forEach(System.out::println);
    }

    private static void realizarTraspaso() throws BBDDException {
        String dni = MiEntradaSalida.leerLinea("DNI del comprador: ").trim();
        String matricula = MiEntradaSalida.leerLinea("Matrícula del coche: ").trim();
        double monto = MiEntradaSalida.leerDouble("Monto económico: ");
        SERVICE.realizarTraspaso(dni, matricula, monto);
        System.out.println("Traspaso realizado correctamente.");
    }

    private static void generarInforme() throws BBDDException, SQLException, IOException {
        List<String> lineas = new ArrayList<>();
        try (Connection connection = ConexionBBDD.getConexion()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM coches");
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                lineas.add("Número total de coches: " + rs.getInt(1));
            }
            lineas.add("");
            lineas.add("Coches agrupados por marca:");
            try (PreparedStatement ps = connection.prepareStatement("SELECT marca, COUNT(*) AS cantidad FROM coches GROUP BY marca ORDER BY marca");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lineas.add("- " + rs.getString("marca") + ": " + rs.getInt("cantidad"));
            }
            lineas.add("");
            Map<String, Integer> frecuenciaExtras = new HashMap<>();
            try (PreparedStatement ps = connection.prepareStatement("SELECT extras FROM coches");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String extras = rs.getString("extras");
                    if (extras != null) {
                        for (String extra : extras.split(",")) {
                            String nombre = extra.trim();
                            if (!nombre.isEmpty()) frecuenciaExtras.merge(nombre, 1, Integer::sum);
                        }
                    }
                }
            }
            lineas.add("Extra más repetido:");
            Map.Entry<String, Integer> masRepetido = frecuenciaExtras.entrySet().stream()
                    .max(Map.Entry.comparingByValue()).orElse(null);
            lineas.add(masRepetido == null ? "No hay extras registrados." :
                    masRepetido.getKey() + " (" + masRepetido.getValue() + " veces)");
        }
        InformeUtil.escribirInforme(lineas);
        System.out.println("Informe generado: informe_concesionario.txt");
    }

}
