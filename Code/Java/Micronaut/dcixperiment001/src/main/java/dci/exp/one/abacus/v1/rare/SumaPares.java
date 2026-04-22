package dci.exp.one.abacus.v1.rare;

public class SumaPares {
    public static void main(String[] args) {
        int n = 50;
        int suma = 0;
        for (int i = 2; i <= n; i += 2) {
            suma += i;
        }
        System.out.println("La suma de números pares hasta " + n + " es: " + suma);
    }
}