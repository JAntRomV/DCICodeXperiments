package dci.exp.one.abacus.v1.rare;

public class FuncionTangente {
    public static void main(String[] args) {
        double angulo = 45; // grados
        double radianes = Math.toRadians(angulo);
        double tangente = Math.tan(radianes);
        System.out.println("Tangente de " + angulo + "° = " + tangente);
    }
}