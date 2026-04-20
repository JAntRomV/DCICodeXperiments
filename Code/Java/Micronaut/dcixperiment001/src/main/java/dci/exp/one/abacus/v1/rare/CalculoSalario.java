package dci.exp.one.abacus.v1.rare;

public class CalculoSalario {
    public static void main(String[] args) {
        double salarioPorHora = 50;
        int horasTrabajadas = 160;
        double salarioTotal = salarioPorHora * horasTrabajadas;
        System.out.println("Salario total: $" + salarioTotal);
    }
}