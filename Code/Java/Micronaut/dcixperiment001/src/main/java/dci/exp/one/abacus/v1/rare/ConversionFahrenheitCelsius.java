package dci.exp.one.abacus.v1.rare;

public class ConversionFahrenheitCelsius {
    public static void main(String[] args) {
        double fahrenheit = 77;
        double celsius = (fahrenheit - 32) * 5/9;
        System.out.println(fahrenheit + "°F = " + celsius + "°C");
    }
}