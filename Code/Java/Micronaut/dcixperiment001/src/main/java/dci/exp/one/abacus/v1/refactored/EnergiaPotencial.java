package dci.exp.one.abacus.v1.refactored;

/**
 * Clase EnergiaPotencial - Versión refactorizada con mejores prácticas.
 * @author Estudiante
 * @version 2.0
 */
public class EnergiaPotencial {
    public static void main(String[] args) {
        double masa = 10; // kg
        double gravedad = 9.8; // m/s²
        double altura = 5; // m
        double energia = masa * gravedad * altura;
        System.out.println("Energía potencial: " + energia + " J");
    }
}