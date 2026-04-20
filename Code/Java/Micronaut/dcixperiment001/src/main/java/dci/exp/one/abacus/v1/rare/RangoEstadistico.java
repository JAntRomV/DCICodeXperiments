package dci.exp.one.abacus.v1.rare;

public class RangoEstadistico {
    public static void main(String[] args) {
        double[] datos = {5, 2, 8, 1, 9, 3, 7};
        double min = datos[0], max = datos[0];
        for (double dato : datos) {
            if (dato < min) min = dato;
            if (dato > max) max = dato;
        }
        double rango = max - min;
        System.out.println("Rango: " + rango);
    }
}