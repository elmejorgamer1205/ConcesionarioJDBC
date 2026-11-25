package utils;

import exceptions.MiEntrdadaSalidaException;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/** Reúno los métodos que uso para leer y mostrar datos por consola. */
public class MiEntradaSalida {
    private static Scanner sc;

    /** Creo y devuelvo el lector de consola cuando lo necesito. */
    private static Scanner getScanner() {
        if (sc == null) {
            sc = new Scanner(System.in);
        }
        return sc;
    }

    /** Leo y devuelvo un número entero válido. */
    public static int leerEntero(String mensaje) {
        int integer = 0;
        // Variable que almacenará un booleano que indicará si se le debe volver a pedir el dato al usuario.
        boolean flag = true;

        while (flag) {
            // Pedimos el entero por pantalla.
            System.out.println(mensaje);
            // Comprobamos si el usuario está introduciendo algo correcto usando la excepción del método parseInt.
            try {
                integer = Integer.parseInt(getScanner().nextLine());
                // Si llegamos hasta aquí, es porque el usuario ha introducido un dato correcto.
                flag = false;
            }
            // Si se lanza la excepción, informamos al usuario de su error.
            catch (NumberFormatException e) {
                // 2. Mensaje de error específico.
                System.out.println("Error: Debe introducir un número entero.");
            }

        }

        return integer;
    }

    /** Leo un entero positivo y permito el cero según el parámetro indicado. */
    public static int leerEnteroPositivo(String mensaje, boolean seAceptaCero) {
        int a;
        do {
            a = leerEntero(mensaje);
            if (a < 0 || a == 0 && !seAceptaCero) {
                System.out.println("Numero no válido.");
            }

        } while (a < 0 || a == 0 && !seAceptaCero);
        return a;
    }

    /** Leo y devuelvo un número decimal; acepto punto o coma. */
    public static double leerDouble(String mensaje) {
        double integer = 0;
        // Variable que almacenará un booleano que indicará si se le debe volver a pedir el dato al usuario.
        boolean flag = true;

        while (flag) {
            // Pedimos el entero por pantalla.
            System.out.println(mensaje);
            // Comprobamos si el usuario está introduciendo algo correcto usando la excepción del método parseInt.
            try {
                integer = Double.parseDouble(getScanner().nextLine().replace(',', '.'));
                // Si llegamos hasta aquí, es porque el usuario ha introducido un dato correcto.
                flag = false;
            }
            // Si se lanza la excepción, informamos al usuario de su error.
            catch (NumberFormatException e) {
                // 2. Mensaje de error específico.
                System.out.println("Error: Debe introducir un número decimal.");
            }

        }

        return integer;
    }

    /** Leo y devuelvo un número decimal de tipo float. */
    public static float leerFloat(String mensaje) {
        float integer = 0;
        // Variable que almacenará un booleano que indicará si se le debe volver a pedir el dato al usuario.
        boolean flag = true;

        while (flag) {
            // Pedimos el entero por pantalla.
            System.out.println(mensaje);
            // Comprobamos si el usuario está introduciendo algo correcto usando la excepción del método parseInt.
            try {
                integer = Float.parseFloat(getScanner().nextLine());
                // Si llegamos hasta aquí, es porque el usuario ha introducido un dato correcto.
                flag = false;
            }
            // Si se lanza la excepción, informamos al usuario de su error.
            catch (NumberFormatException e) {
                // 2. Mensaje de error específico.
                System.out.println("Error: Debe introducir un número entero.");
            }

        }

        return integer;
    }


    /** Genero un entero aleatorio entre uno y el máximo indicado. */
    public static int generaAleatorio(int max) {
        return (int) (Math.random() * max + 1);
    }

    /** Genero un entero aleatorio dentro del intervalo indicado. */
    public static int generaAleatorioEntre(int min, int max, boolean seAceptaElMaximo) {
        int ventana;

        if (seAceptaElMaximo) {
            ventana = max - min + 1;
        } else {
            ventana = max - min;
        }
        return (int) (Math.random() * ventana) + min;
    }

    /** Leo y devuelvo la siguiente palabra introducida. */
    public static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return getScanner().next();
    }

    /** Leo un entero y compruebo que esté dentro del rango indicado. */
    public static int leerEnteroRango(String mensaje, int min, int max) throws MiEntrdadaSalidaException {
        if (min > max) {
            //Mostrar error
            throw new MiEntrdadaSalidaException("El mínimo es mayor que el máximo");
        }
        int a;
        do {
            a = leerEntero(mensaje);
            if (a < min || a > max) {
                System.out.println("Número no válido, esta fuera del rango.");
            }
        } while (a < min || a > max);
        return a;
    }

    /** Leo y devuelvo una respuesta afirmativa o negativa. */
    public static char leerSN(String mensaje) {
        char caracter;
        do {
            String texto = leerTexto(mensaje).toUpperCase();
            caracter = texto.charAt(0);
            if (caracter != 'S' && caracter != 'N') {
                System.out.println("Respuesta no válida");
            }
        } while (caracter != 'S' && caracter != 'N');
        return caracter;
    }

    /** Muestro por consola una matriz de enteros. */
    public static void imprimirMatriz(int[][] matriz) {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.print("[" + matriz[i][j] + "]" + " ");
            }
            System.out.println();
        }
    }

    /** Muestro por consola una matriz de textos. */
    public static void imprimirMatrizString(String[][] matriz) {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.print("[" + matriz[i][j] + "]" + " ");
            }
            System.out.println();
        }
    }

    /** Leo y devuelvo una línea completa de texto. */
    public static String leerLinea(String mensaje) {
        System.out.print(mensaje);
        return getScanner().nextLine();
    }

    /** Leo una línea y devuelvo sus elementos separados por el delimitador indicado. */
    public static List<String> leerListaTexto(String mensaje, String delimitador) {
        List<String> elementos = new java.util.ArrayList<>();
        String linea = leerLinea(mensaje);
        if (linea.trim().isEmpty()) return elementos;
        for (String elemento : linea.split(delimitador)) {
            if (!elemento.trim().isEmpty()) elementos.add(elemento.trim());
        }
        return elementos;
    }

    /** Muestro las opciones no nulas que contiene el array. */
    public static void mostrarOpcionesSinNulos(String mensaje, Object[] array) {
        System.out.println(mensaje);
        int posicion = 1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                System.out.println(posicion++ + "." + " " + array[i]);
            }
        }
        System.out.println();
    }

    /** Muestro las opciones no nulas que contiene la colección. */
    public static void mostrarOpcionesSinNulos(String mensaje, Collection<Object> coleccion) {
        System.out.println(mensaje);
        int posicion = 1;
        for (Object o: coleccion) {
            if (o != null) {
                System.out.println(posicion++ + "." + " " + o);
            }
        }
        System.out.println();
    }

    /** Leo día, mes y año y devuelvo la fecha formada. */
    public static LocalDate fecha(String mensaje) throws MiEntrdadaSalidaException {
        int año =  leerEnteroPositivo("Introduce el año: ",true);
        int mes =  leerEnteroPositivo("Introduce el mes: ",true);
        int dia =  leerEnteroPositivo("Introduce el dia: ",true);
        try {
            return LocalDate.of(año,mes,dia);
        }catch (DateTimeException e){
            throw new MiEntrdadaSalidaException("Fecha no valida.");
        }

    }

    /** Leo y devuelvo un único carácter. */
    public static char leerChar(String mensaje) throws MiEntrdadaSalidaException{
        String texto = leerTexto(mensaje);
        if (texto.length() != 1) {throw new MiEntrdadaSalidaException("Debes introducir un caracter.");}
        return texto.charAt(0);
    }

}
