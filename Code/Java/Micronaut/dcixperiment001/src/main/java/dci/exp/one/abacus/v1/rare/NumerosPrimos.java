package dci.exp.one.abacus.v1.rare;

public class NumerosPrimos {
    public static void main(String[] args) {
        int limite = 50;
        System.out.println("Números primos hasta " + limite + ":");
        for (int num = 2; num <= limite; num++) {
            boolean esPrimo = true;
            for (int i = 2; i <= Math.sqrt(num); i++) {
                if (num % i == 0) {
                    esPrimo = false;
                    break;
                }
            }
            if (esPrimo) System.out.print(num + " ");
        }
    }
}