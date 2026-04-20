package dci.exp.one.abacus.v1.rare;

public class DiasAAnios {
    public static void main(String[] args) {
        int dias = 730;
        int anios = dias / 365;
        int diasRestantes = dias % 365;
        System.out.println(dias + " días = " + anios + " años y " + diasRestantes + " días");
    }
}