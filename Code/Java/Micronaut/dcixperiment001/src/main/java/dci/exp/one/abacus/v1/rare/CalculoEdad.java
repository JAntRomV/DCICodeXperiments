package dci.exp.one.abacus.v1.rare;

public class CalculoEdad {
    public static void main(String[] args) {
        int anioNacimiento = 1990;
        int anioActual = 2025;
        int edad = anioActual - anioNacimiento;
        System.out.println("Tu edad es: " + edad + " años");
    }
}