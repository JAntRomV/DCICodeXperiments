package dci.exp.one.abacus.v1.refactored;

/**
 * Clase PerimetroRectangulo - Versión refactorizada con mejores prácticas.
 * @author Estudiante
 * @version 2.0
 */
public class PerimetroRectangulo {
    public static void main(String[] args) {
        double base = 10;
        double altura = 5;
        double perimetro = 2 * (base + altura);
        System.out.println("El perímetro del rectángulo es: " + perimetro);
    }
}