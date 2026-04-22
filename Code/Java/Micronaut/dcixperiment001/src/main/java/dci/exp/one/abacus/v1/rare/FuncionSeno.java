package dci.exp.one.abacus.v1.rare;

public class FuncionSeno {
    public static void main(String[] args) {
        double angulo = 30; // grados
        double radianes = Math.toRadians(angulo);
        double seno = Math.sin(radianes);
        System.out.println("Seno de " + angulo + "° = " + seno);
    }
}