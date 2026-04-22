package dci.exp.one.abacus.v1.rare;

public class PotenciaRaiz {
    public static void main(String[] args) {
        double base = 4;
        double exponente = 3;
        double potencia = Math.pow(base, exponente);
        double raizCuadrada = Math.sqrt(16);
        System.out.println(base + " elevado a " + exponente + " = " + potencia);
        System.out.println("La raíz cuadrada de 16 = " + raizCuadrada);
    }
}