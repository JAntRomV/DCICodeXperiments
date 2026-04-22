package dci.exp.one.abacus.v1.rare;

public class CalculoIMC {
    public static void main(String[] args) {
        double peso = 70; // kg
        double altura = 1.75; // metros
        double imc = peso / (altura * altura);
        System.out.println("Tu IMC es: " + imc);
    }
}