package dci.exp.one.abacus.v1.rare;

public class NumeroPositivoNegativo {
    public static void main(String[] args) {
        int numero = -5;
        if (numero > 0) {
            System.out.println(numero + " es positivo");
        } else if (numero < 0) {
            System.out.println(numero + " es negativo");
        } else {
            System.out.println("El número es cero");
        }
    }
}