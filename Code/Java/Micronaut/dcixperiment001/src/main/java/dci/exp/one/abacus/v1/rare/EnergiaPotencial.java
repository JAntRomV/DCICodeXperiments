package dci.exp.one.abacus.v1.rare;

public class EnergiaPotencial {
    public static void main(String[] args) {
        double masa = 10; // kg
        double gravedad = 9.8; // m/s²
        double altura = 5; // m
        double energia = masa * gravedad * altura;
        System.out.println("Energía potencial: " + energia + " J");
    }
}