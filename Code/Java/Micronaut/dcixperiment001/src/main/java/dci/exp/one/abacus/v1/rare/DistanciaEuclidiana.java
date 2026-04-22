package dci.exp.one.abacus.v1.rare;

public class DistanciaEuclidiana {
    public static void main(String[] args) {
        double x1 = 1, y1 = 2;
        double x2 = 4, y2 = 6;
        double distancia = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
        System.out.println("Distancia euclidiana: " + distancia);
    }
}