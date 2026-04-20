package dci.exp.one.abacus.v1.rare;

public class ConversionCelsiusFahrenheit {
    public static void main(String[] args) {
        double celsius = 25;
        double fahrenheit = (celsius * 9/5) + 32;
        System.out.println(celsius + "°C = " + fahrenheit + "°F");
    }
}