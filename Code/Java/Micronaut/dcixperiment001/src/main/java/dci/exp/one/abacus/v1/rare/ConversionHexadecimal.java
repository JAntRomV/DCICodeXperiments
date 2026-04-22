package dci.exp.one.abacus.v1.rare;

public class ConversionHexadecimal {
    public static void main(String[] args) {
        int numero = 255;
        String hexadecimal = Integer.toHexString(numero);
        System.out.println(numero + " en hexadecimal es: " + hexadecimal);
    }
}