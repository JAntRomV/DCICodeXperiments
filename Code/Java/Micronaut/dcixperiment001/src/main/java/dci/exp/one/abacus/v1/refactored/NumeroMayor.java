package dci.exp.one.abacus.v1.refactored;

/**
 * Clase NumeroMayor - Versión refactorizada con mejores prácticas.
 * @author Estudiante
 * @version 2.0
 */
public class NumeroMayor {
    public static void main(String[] args) {
        int a = 25, b = 40, c = 15;
        int mayor = a;
        if (b > mayor) mayor = b;
        if (c > mayor) mayor = c;
        System.out.println("El número mayor es: " + mayor);
    }
}