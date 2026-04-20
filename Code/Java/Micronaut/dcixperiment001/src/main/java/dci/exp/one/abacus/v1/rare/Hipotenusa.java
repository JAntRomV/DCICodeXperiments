package dci.exp.one.abacus.v1.rare;

public class Hipotenusa {
    public static void main(String[] args) {
        double cateto1 = 3;
        double cateto2 = 4;
        double hipotenusa = Math.sqrt(cateto1*cateto1 + cateto2*cateto2);
        System.out.println("Hipotenusa: " + hipotenusa);
    }
}