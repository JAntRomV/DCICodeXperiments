import os

# --- Lista de todos los programas en Java ---
# Cada elemento es un diccionario con el nombre del archivo y su contenido de código.
programs_data = [
    {
        "filename": "HolaMundo.java",
        "code": """package Code.Gemini.v1.Rare;

public class HolaMundo {
    public static void main(String[] args) {
        // Imprime el clásico saludo en la consola
        System.out.println("¡Hola, Mundo!");
    }
}
"""
    },
    {
        "filename": "CalculadoraSimple.java",
        "code": """package Code.Gemini.v1.Rare;

public class CalculadoraSimple {
    public static void main(String[] args) {
        int numero1 = 20;
        int numero2 = 5;

        int suma = numero1 + numero2;
        int resta = numero1 - numero2;
        int multiplicacion = numero1 * numero2;
        int division = numero1 / numero2;

        System.out.println("La suma es: " + suma);
        System.out.println("La resta es: " + resta);
        System.out.println("La multiplicación es: " + multiplicacion);
        System.out.println("La división es: " + division);
    }
}
"""
    },
    {
        "filename": "UnionDeNombres.java",
        "code": """package Code.Gemini.v1.Rare;

public class UnionDeNombres {
    public static void main(String[] args) {
        String nombre = "Juan";
        String apellido = "Pérez";
        
        String nombreCompleto = nombre + " " + apellido;
        
        System.out.println("El nombre completo es: " + nombreCompleto);
    }
}
"""
    },
    {
        "filename": "VerificadorDeEdad.java",
        "code": """package Code.Gemini.v1.Rare;

public class VerificadorDeEdad {
    public static void main(String[] args) {
        int edad = 21;

        if (edad >= 18) {
            System.out.println("Es mayor de edad.");
        } else {
            System.out.println("Es menor de edad.");
        }
    }
}
"""
    },
    {
        "filename": "TablaDeMultiplicar.java",
        "code": """package Code.Gemini.v1.Rare;

public class TablaDeMultiplicar {
    public static void main(String[] args) {
        int numeroParaTabla = 7;
        
        System.out.println("Tabla de multiplicar del " + numeroParaTabla + ":");
        
        for (int i = 1; i <= 10; i++) {
            int resultado = numeroParaTabla * i;
            System.out.println(numeroParaTabla + " x " + i + " = " + resultado);
        }
    }
}
"""
    },
    {
        "filename": "ContadorConWhile.java",
        "code": """package Code.Gemini.v1.Rare;

public class ContadorConWhile {
    public static void main(String[] args) {
        int contador = 1;
        
        System.out.println("Iniciando conteo...");
        
        while (contador <= 5) {
            System.out.println("Número: " + contador);
            contador++;
        }
        
        System.out.println("Conteo finalizado.");
    }
}
"""
    },
    {
        "filename": "ListaDeFrutas.java",
        "code": """package Code.Gemini.v1.Rare;

public class ListaDeFrutas {
    public static void main(String[] args) {
        String[] frutas = {"Manzana", "Banana", "Naranja", "Fresa"};
        
        System.out.println("Mi lista de frutas:");
        
        for (String fruta : frutas) {
            System.out.println("- " + fruta);
        }
    }
}
"""
    },
    {
        "filename": "SaludoConMetodo.java",
        "code": """package Code.Gemini.v1.Rare;

public class SaludoConMetodo {

    public static void main(String[] args) {
        saludarUsuario("Ana");
    }

    public static void saludarUsuario(String nombre) {
        System.out.println("¡Hola, " + nombre + "! Bienvenido/a.");
    }
}
"""
    },
    {
        "filename": "EvaluadorLogico.java",
        "code": """package Code.Gemini.v1.Rare;

public class EvaluadorLogico {
    public static void main(String[] args) {
        int edad = 25;
        boolean tieneLicencia = true;

        if (edad >= 18 && tieneLicencia) {
            System.out.println("Puede conducir un coche.");
        } else {
            System.out.println("No cumple los requisitos para conducir.");
        }
        
        boolean esEstudiante = false;
        boolean esMayorDe65 = true;
        
        if (esEstudiante || esMayorDe65) {
            System.out.println("Tiene derecho a un descuento.");
        } else {
            System.out.println("No tiene descuento disponible.");
        }
    }
}
"""
    },
    {
        "filename": "ConversionDeTipos.java",
        "code": """package Code.Gemini.v1.Rare;

public class ConversionDeTipos {
    public static void main(String[] args) {
        double precioConDecimales = 99.99;
        
        int precioSinDecimales = (int) precioConDecimales;
        
        System.out.println("Precio original (double): " + precioConDecimales);
        System.out.println("Precio convertido (int): " + precioSinDecimales);
    }
}
"""
    },
    {
        "filename": "ContadorDoWhile.java",
        "code": """package Code.Gemini.v1.Rare;

public class ContadorDoWhile {
    public static void main(String[] args) {
        int i = 10;

        System.out.println("Este bucle se ejecutará al menos una vez.");
        
        do {
            System.out.println("Valor de i: " + i);
            i++;
        } while (i < 5);
        
        System.out.println("Bucle terminado. El valor final de i es " + i);
    }
}
"""
    },
    {
        "filename": "DiaDeLaSemana.java",
        "code": """package Code.Gemini.v1.Rare;

public class DiaDeLaSemana {
    public static void main(String[] args) {
        int dia = 4;
        String nombreDelDia;

        switch (dia) {
            case 1: nombreDelDia = "Lunes"; break;
            case 2: nombreDelDia = "Martes"; break;
            case 3: nombreDelDia = "Miércoles"; break;
            case 4: nombreDelDia = "Jueves"; break;
            case 5: nombreDelDia = "Viernes"; break;
            default: nombreDelDia = "Fin de semana"; break;
        }
        
        System.out.println("Hoy es " + nombreDelDia);
    }
}
"""
    },
    {
        "filename": "EncontrarMaximo.java",
        "code": """package Code.Gemini.v1.Rare;

public class EncontrarMaximo {
    public static void main(String[] args) {
        int[] numeros = {45, 88, 12, 105, 3, 99};
        int maximo = numeros[0];

        for (int i = 1; i < numeros.length; i++) {
            if (numeros[i] > maximo) {
                maximo = numeros[i];
            }
        }

        System.out.println("El número más grande en el array es: " + maximo);
    }
}
"""
    },
    {
        "filename": "ObjetoPerro.java",
        "code": """package Code.Gemini.v1.Rare;

class Perro {
    String nombre = "Fido";
    void ladrar() {
        System.out.println("¡Guau! ¡Guau!");
    }
}

public class ObjetoPerro {
    public static void main(String[] args) {
        Perro miPerro = new Perro();

        System.out.println("El nombre de mi perro es: " + miPerro.nombre);
        System.out.print("Mi perro hace: ");
        miPerro.ladrar();
    }
}
"""
    },
    {
        "filename": "AreaRectangulo.java",
        "code": """package Code.Gemini.v1.Rare;

public class AreaRectangulo {

    public static void main(String[] args) {
        double base = 10.5;
        double altura = 5.2;
        
        double area = calcularArea(base, altura);
        
        System.out.println("El área del rectángulo es: " + area);
    }
    
    public static double calcularArea(double b, double h) {
        return b * h;
    }
}
"""
    },
    {
        "filename": "VotacionConTernario.java",
        "code": """package Code.Gemini.v1.Rare;

public class VotacionConTernario {
    public static void main(String[] args) {
        int edad = 17;
        
        String resultado = (edad >= 18) ? "Puede votar" : "No puede votar";
        
        System.out.println(resultado);
    }
}
"""
    },
    {
        "filename": "DibujoSimple.java",
        "code": """package Code.Gemini.v1.Rare;

public class DibujoSimple {
    public static void main(String[] args) {
        int lado = 5;

        for (int i = 0; i < lado; i++) {
            for (int j = 0; j < lado; j++) {
                System.out.print("* ");
            }
            System.out.println();
        }
    }
}
"""
    },
    {
        "filename": "ManipulacionDeTexto.java",
        "code": """package Code.Gemini.v1.Rare;

public class ManipulacionDeTexto {
    public static void main(String[] args) {
        String frase = "Java es divertido";

        int longitud = frase.length();
        String enMayusculas = frase.toUpperCase();
        char primerCaracter = frase.charAt(0);

        System.out.println("Frase original: " + frase);
        System.out.println("Longitud de la frase: " + longitud);
        System.out.println("Frase en mayúsculas: " + enMayusculas);
        System.out.println("El primer carácter es: " + primerCaracter);
    }
}
"""
    },
    {
        "filename": "SumaArray.java",
        "code": """package Code.Gemini.v1.Rare;

public class SumaArray {
    public static void main(String[] args) {
        int[] valores = {10, 20, 30, 40, 50};
        int sumaTotal = 0;

        for (int valor : valores) {
            sumaTotal += valor;
        }

        System.out.println("La suma de los elementos del array es: " + sumaTotal);
    }
}
"""
    },
    {
        "filename": "ContadorDeGatos.java",
        "code": """package Code.Gemini.v1.Rare;

class Gato {
    static int contadorDeGatos = 0;

    public Gato() {
        contadorDeGatos++;
    }
}

public class ContadorDeGatos {
    public static void main(String[] args) {
        System.out.println("Creando gatos...");
        
        Gato gato1 = new Gato();
        Gato gato2 = new Gato();
        Gato gato3 = new Gato();
        
        System.out.println("Número total de gatos creados: " + Gato.contadorDeGatos);
    }
}
"""
    },
    {
        "filename": "EncontrarMinimo.java",
        "code": """package Code.Gemini.v1.Rare;

public class EncontrarMinimo {
    public static void main(String[] args) {
        int[] numeros = {56, 23, 11, 78, 45, 9};
        int minimo = numeros[0];

        for (int i = 1; i < numeros.length; i++) {
            if (numeros[i] < minimo) {
                minimo = numeros[i];
            }
        }
        System.out.println("El número más pequeño es: " + minimo);
    }
}
"""
    },
    {
        "filename": "PromedioArray.java",
        "code": """package Code.Gemini.v1.Rare;

public class PromedioArray {
    public static void main(String[] args) {
        double[] calificaciones = {8.5, 9.0, 7.8, 10.0, 6.5};
        double suma = 0.0;

        for (double calificacion : calificaciones) {
            suma += calificacion;
        }

        double promedio = suma / calificaciones.length;
        System.out.println("El promedio de las calificaciones es: " + promedio);
    }
}
"""
    },
    {
        "filename": "Libreria.java",
        "code": """package Code.Gemini.v1.Rare;

class Libro {
    String titulo;
    String autor;

    public Libro(String t, String a) {
        titulo = t;
        autor = a;
    }

    void mostrarInfo() {
        System.out.println("Título: " + titulo + ", Autor: " + autor);
    }
}

public class Libreria {
    public static void main(String[] args) {
        Libro miLibro = new Libro("Don Quijote", "Miguel de Cervantes");
        miLibro.mostrarInfo();
    }
}
"""
    },
    {
        "filename": "RevertirCadena.java",
        "code": """package Code.Gemini.v1.Rare;

public class RevertirCadena {
    public static void main(String[] args) {
        String original = "Hola";
        String revertida = "";

        for (int i = original.length() - 1; i >= 0; i--) {
            revertida += original.charAt(i);
        }

        System.out.println("Cadena original: " + original);
        System.out.println("Cadena revertida: " + revertida);
    }
}
"""
    },
    {
        "filename": "FizzBuzz.java",
        "code": """package Code.Gemini.v1.Rare;

public class FizzBuzz {
    public static void main(String[] args) {
        for (int i = 1; i <= 20; i++) {
            if (i % 3 == 0 && i % 5 == 0) {
                System.out.println("FizzBuzz");
            } else if (i % 3 == 0) {
                System.out.println("Fizz");
            } else if (i % 5 == 0) {
                System.out.println("Buzz");
            } else {
                System.out.println(i);
            }
        }
    }
}
"""
    },
    {
        "filename": "AreaCirculo.java",
        "code": """package Code.Gemini.v1.Rare;

public class AreaCirculo {
    public static void main(String[] args) {
        final double PI = 3.14159;
        double radio = 5.0;

        double area = PI * radio * radio;

        System.out.println("El área del círculo es: " + area);
    }
}
"""
    },
    {
        "filename": "ParOImpar.java",
        "code": """package Code.Gemini.v1.Rare;

public class ParOImpar {
    public static void main(String[] args) {
        int numero = 27;

        if (numero % 2 == 0) {
            System.out.println(numero + " es un número par.");
        } else {
            System.out.println(numero + " es un número impar.");
        }
    }
}
"""
    },
    {
        "filename": "MatrizSimple.java",
        "code": """package Code.Gemini.v1.Rare;

public class MatrizSimple {
    public static void main(String[] args) {
        int[][] matriz = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(matriz[i][j] + " ");
            }
            System.out.println();
        }
    }
}
"""
    },
    {
        "filename": "ContadorDeVocales.java",
        "code": """package Code.Gemini.v1.Rare;

public class ContadorDeVocales {
    public static void main(String[] args) {
        String texto = "Este es un texto de prueba".toLowerCase();
        int contador = 0;

        for (int i = 0; i < texto.length(); i++) {
            char caracter = texto.charAt(i);
            if (caracter == 'a' || caracter == 'e' || caracter == 'i' || caracter == 'o' || caracter == 'u') {
                contador++;
            }
        }
        System.out.println("El número de vocales es: " + contador);
    }
}
"""
    },
    {
        "filename": "SumaSobrecargada.java",
        "code": """package Code.Gemini.v1.Rare;

public class SumaSobrecargada {
    
    public static int sumar(int a, int b) {
        System.out.println("Usando la suma de enteros.");
        return a + b;
    }

    public static double sumar(double a, double b) {
        System.out.println("Usando la suma de dobles.");
        return a + b;
    }

    public static void main(String[] args) {
        System.out.println("Resultado 1: " + sumar(5, 10));
        System.out.println("Resultado 2: " + sumar(3.5, 7.2));
    }
}
"""
    },
    {
        "filename": "DetenerBucle.java",
        "code": """package Code.Gemini.v1.Rare;

public class DetenerBucle {
    public static void main(String[] args) {
        int[] numeros = {10, 20, 30, -1, 40, 50};

        System.out.println("Buscando el número negativo...");
        for (int numero : numeros) {
            if (numero < 0) {
                System.out.println("Número negativo encontrado. Deteniendo el bucle.");
                break;
            }
            System.out.println("Número procesado: " + numero);
        }
    }
}
"""
    },
    {
        "filename": "SaltarIteracion.java",
        "code": """package Code.Gemini.v1.Rare;

public class SaltarIteracion {
    public static void main(String[] args) {
        System.out.println("Imprimiendo solo números impares del 1 al 10:");
        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0) {
                continue;
            }
            System.out.println(i);
        }
    }
}
"""
    },
    {
        "filename": "SecuenciaFibonacci.java",
        "code": """package Code.Gemini.v1.Rare;

public class SecuenciaFibonacci {
    public static void main(String[] args) {
        int n = 10;
        int t1 = 0, t2 = 1;

        System.out.print("Primeros " + n + " términos: ");
        for (int i = 1; i <= n; ++i) {
            System.out.print(t1 + " ");
            int sum = t1 + t2;
            t1 = t2;
            t2 = sum;
        }
        System.out.println();
    }
}
"""
    },
    {
        "filename": "ComparadorDeStrings.java",
        "code": """package Code.Gemini.v1.Rare;

public class ComparadorDeStrings {
    public static void main(String[] args) {
        String str1 = new String("Java");
        String str2 = new String("Java");

        System.out.println("Usando '==': " + (str1 == str2));
        System.out.println("Usando '.equals()': " + str1.equals(str2));
    }
}
"""
    },
    {
        "filename": "OperacionesMath.java",
        "code": """package Code.Gemini.v1.Rare;

public class OperacionesMath {
    public static void main(String[] args) {
        double base = 4;
        double exponente = 3;
        double numero = 64;

        double potencia = Math.pow(base, exponente);
        double raiz = Math.sqrt(numero);

        System.out.println(base + " elevado a " + exponente + " es " + potencia);
        System.out.println("La raíz cuadrada de " + numero + " es " + raiz);
    }
}
"""
    },
    {
        "filename": "VerificadorPalindromo.java",
        "code": """package Code.Gemini.v1.Rare;

public class VerificadorPalindromo {
    public static void main(String[] args) {
        String palabra = "reconocer";
        String reves = new StringBuilder(palabra).reverse().toString();

        if (palabra.equals(reves)) {
            System.out.println(palabra + " es un palíndromo.");
        } else {
            System.out.println(palabra + " no es un palíndromo.");
        }
    }
}
"""
    },
    {
        "filename": "CadenaDesdeArray.java",
        "code": """package Code.Gemini.v1.Rare;

public class CadenaDesdeArray {
    public static void main(String[] args) {
        char[] letras = {'H', 'o', 'l', 'a', ' ', 'M', 'u', 'n', 'd', 'o'};
        String saludo = new String(letras);

        System.out.println("El saludo creado es: " + saludo);
    }
}
"""
    },
    {
        "filename": "CuentaRegresiva.java",
        "code": """package Code.Gemini.v1.Rare;

public class CuentaRegresiva {
    public static void main(String[] args) {
        int contador = 10;
        
        System.out.println("Iniciando cuenta regresiva...");
        while (contador > 0) {
            System.out.println(contador);
            contador--;
        }
        System.out.println("¡Despegue!");
    }
}
"""
    },
    {
        "filename": "BuscarLetra.java",
        "code": """package Code.Gemini.v1.Rare;

public class BuscarLetra {
    public static void main(String[] args) {
        String frase = "La programación es fascinante";
        char letraBuscada = 'f';

        int posicion = frase.indexOf(letraBuscada);

        if (posicion != -1) {
            System.out.println("La letra '" + letraBuscada + "' se encontró en la posición: " + posicion);
        } else {
            System.out.println("La letra '" + letraBuscada + "' no se encontró.");
        }
    }
}
"""
    },
    {
        "filename": "InfoEstudiante.java",
        "code": """package Code.Gemini.v1.Rare;

class Estudiante {
    private String nombre = "Carlos";
    private int matricula = 12345;

    public String getNombre() {
        return nombre;
    }

    public int getMatricula() {
        return matricula;
    }
}

public class InfoEstudiante {
    public static void main(String[] args) {
        Estudiante e1 = new Estudiante();
        
        System.out.println("Nombre del estudiante: " + e1.getNombre());
        System.out.println("Matrícula: " + e1.getMatricula());
    }
}
"""
    },
    {
        "filename": "SelectorDeDificultad.java",
        "code": """package Code.Gemini.v1.Rare;

enum Nivel {
    FACIL,
    NORMAL,
    DIFICIL
}

public class SelectorDeDificultad {
    public static void main(String[] args) {
        Nivel dificultadElegida = Nivel.NORMAL;

        switch (dificultadElegida) {
            case FACIL: System.out.println("Has elegido el nivel Fácil."); break;
            case NORMAL: System.out.println("Has elegido el nivel Normal."); break;
            case DIFICIL: System.out.println("Has elegido el nivel Difícil."); break;
        }
    }
}
"""
    },
    {
        "filename": "PruebaHerencia.java",
        "code": """package Code.Gemini.v1.Rare;

class Animal {
    void comer() {
        System.out.println("Este animal come comida.");
    }
}

class GatoDomestico extends Animal {
    void maullar() {
        System.out.println("¡Miau!");
    }
}

public class PruebaHerencia {
    public static void main(String[] args) {
        GatoDomestico miGato = new GatoDomestico();
        miGato.comer();
        miGato.maullar();
    }
}
"""
    },
    {
        "filename": "ConstructorDeCadenas.java",
        "code": """package Code.Gemini.v1.Rare;

public class ConstructorDeCadenas {
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        String[] palabras = {"Hola", " ", "Mundo", " ", "desde", " ", "Java"};

        for (String palabra : palabras) {
            sb.append(palabra);
        }

        String resultado = sb.toString();
        System.out.println("La cadena construida es: " + resultado);
    }
}
"""
    },
    {
        "filename": "CalculoFactorial.java",
        "code": """package Code.Gemini.v1.Rare;

public class CalculoFactorial {
    public static void main(String[] args) {
        int numero = 5;
        long factorial = 1;

        for (int i = 1; i <= numero; ++i) {
            factorial *= i;
        }

        System.out.printf("El factorial de %d es %d%n", numero, factorial);
    }
}
"""
    },
    {
        "filename": "TiposDeCasting.java",
        "code": """package Code.Gemini.v1.Rare;

public class TiposDeCasting {
    public static void main(String[] args) {
        int miEntero = 100;
        double miDoble = miEntero;
        System.out.println("Casting implícito: " + miDoble);

        double otroDoble = 123.45;
        int otroEntero = (int) otroDoble;
        System.out.println("Casting explícito: " + otroEntero);
    }
}
"""
    },
    {
        "filename": "BuscadorDeSubcadena.java",
        "code": """package Code.Gemini.v1.Rare;

public class BuscadorDeSubcadena {
    public static void main(String[] args) {
        String textoPrincipal = "El lenguaje de programación Java es muy popular.";
        String subcadena = "Java";

        if (textoPrincipal.contains(subcadena)) {
            System.out.println("El texto contiene la palabra '" + subcadena + "'.");
        } else {
            System.out.println("El texto no contiene la palabra '" + subcadena + "'.");
        }
    }
}
"""
    },
    {
        "filename": "ReemplazoDeTexto.java",
        "code": """package Code.Gemini.v1.Rare;

public class ReemplazoDeTexto {
    public static void main(String[] args) {
        String original = "Me gusta programar en Python.";
        String modificada = original.replace("Python", "Java");

        System.out.println("Original: " + original);
        System.out.println("Modificada: " + modificada);
    }
}
"""
    },
    {
        "filename": "MetodoFinal.java",
        "code": """package Code.Gemini.v1.Rare;

class Vehiculo {
    public final void mostrarMarca() {
        System.out.println("Marca genérica de vehículo.");
    }
}

class Coche extends Vehiculo {
    // No se puede sobreescribir mostrarMarca()
}

public class MetodoFinal {
    public static void main(String[] args) {
        Coche miCoche = new Coche();
        miCoche.mostrarMarca();
    }
}
"""
    },
    {
        "filename": "IntercambioDeVariables.java",
        "code": """package Code.Gemini.v1.Rare;

public class IntercambioDeVariables {
    public static void main(String[] args) {
        int a = 10;
        int b = 20;
        
        System.out.println("Antes: a = " + a + ", b = " + b);

        int temp = a;
        a = b;
        b = temp;
        
        System.out.println("Después: a = " + a + ", b = " + b);
    }
}
"""
    },
    {
        "filename": "LanzarDado.java",
        "code": """package Code.Gemini.v1.Rare;

public class LanzarDado {
    public static void main(String[] args) {
        int numeroAleatorio = (int)(Math.random() * 6) + 1;
        
        System.out.println("Has lanzado un dado y ha salido: " + numeroAleatorio);
    }
}
"""
    },
    {
        "filename": "ValorAbsoluto.java",
        "code": """package Code.Gemini.v1.Rare;

public class ValorAbsoluto {
    public static void main(String[] args) {
        int numeroNegativo = -150;
        int valorAbsoluto = Math.abs(numeroNegativo);

        System.out.println("El valor absoluto de " + numeroNegativo + " es " + valorAbsoluto);
    }
}
"""
    },
    {
        "filename": "DivisionSegura.java",
        "code": """package Code.Gemini.v1.Rare;

public class DivisionSegura {
    public static void main(String[] args) {
        int dividendo = 10;
        int divisor = 0;

        if (divisor == 0) {
            System.out.println("Error: No se puede dividir por cero.");
        } else {
            int resultado = dividendo / divisor;
            System.out.println("El resultado es: " + resultado);
        }
    }
}
"""
    },
    {
        "filename": "SumaTotalMatriz.java",
        "code": """package Code.Gemini.v1.Rare;

public class SumaTotalMatriz {
    public static void main(String[] args) {
        int[][] matriz = {{5, 10, 15}, {20, 25, 30}, {35, 40, 45}};
        int suma = 0;

        for (int[] fila : matriz) {
            for (int valor : fila) {
                suma += valor;
            }
        }
        System.out.println("La suma de todos los elementos de la matriz es: " + suma);
    }
}
"""
    },
    {
        "filename": "Tienda.java",
        "code": """package Code.Gemini.v1.Rare;

class Producto {
    private String nombre;
    private double precio;

    public void setNombre(String nuevoNombre) {
        this.nombre = nuevoNombre;
    }

    public void setPrecio(double nuevoPrecio) {
        if (nuevoPrecio > 0) {
            this.precio = nuevoPrecio;
        }
    }

    void mostrarDetalles() {
        System.out.println("Producto: " + nombre + ", Precio: $" + precio);
    }
}

public class Tienda {
    public static void main(String[] args) {
        Producto p1 = new Producto();
        p1.setNombre("Laptop");
        p1.setPrecio(1200.50);
        p1.mostrarDetalles();
    }
}
"""
    },
    {
        "filename": "ContadorDePalabras.java",
        "code": """package Code.Gemini.v1.Rare;

public class ContadorDePalabras {
    public static void main(String[] args) {
        String frase = "Java es un lenguaje de programación muy versátil.";
        String[] palabras = frase.split(" ");

        int cantidadDePalabras = palabras.length;
        System.out.println("La frase tiene " + cantidadDePalabras + " palabras.");
    }
}
"""
    },
    {
        "filename": "LogicaXOR.java",
        "code": """package Code.Gemini.v1.Rare;

public class LogicaXOR {
    public static void main(String[] args) {
        boolean tieneLlaveA = true;
        boolean tieneLlaveB = false;

        if (tieneLlaveA ^ tieneLlaveB) {
            System.out.println("La puerta se abre.");
        } else {
            System.out.println("La puerta permanece cerrada.");
        }
    }
}
"""
    },
    {
        "filename": "PequeñoRetraso.java",
        "code": """package Code.Gemini.v1.Rare;

public class PequeñoRetraso {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Iniciando tarea...");
        Thread.sleep(2000); // Pausa la ejecución por 2000 milisegundos
        System.out.println("Tarea finalizada después de 2 segundos.");
    }
}
"""
    },
    {
        "filename": "MenuDeOpciones.java",
        "code": """package Code.Gemini.v1.Rare;

public class MenuDeOpciones {
    public static void main(String[] args) {
        String opcion = "Guardar";

        switch (opcion) {
            case "Abrir": System.out.println("Abriendo archivo..."); break;
            case "Guardar": System.out.println("Guardando cambios..."); break;
            case "Cerrar": System.out.println("Cerrando programa..."); break;
            default: System.out.println("Opción no reconocida."); break;
        }
    }
}
"""
    },
    {
        "filename": "IndiceDeElemento.java",
        "code": """package Code.Gemini.v1.Rare;

public class IndiceDeElemento {
    public static void main(String[] args) {
        String[] colores = {"Rojo", "Verde", "Azul", "Amarillo"};
        String colorBuscado = "Azul";
        int indice = -1;

        for (int i = 0; i < colores.length; i++) {
            if (colores[i].equals(colorBuscado)) {
                indice = i;
                break;
            }
        }

        if (indice != -1) {
            System.out.println("El color " + colorBuscado + " está en el índice " + indice + ".");
        } else {
            System.out.println("El color no se encontró en el array.");
        }
    }
}
"""
    },
    {
        "filename": "ClonarArray.java",
        "code": """package Code.Gemini.v1.Rare;

import java.util.Arrays;

public class ClonarArray {
    public static void main(String[] args) {
        int[] original = {1, 2, 3, 4, 5};
        int[] copia = original.clone();

        copia[0] = 99;

        System.out.println("Array Original: " + Arrays.toString(original));
        System.out.println("Array Copiado y Modificado: " + Arrays.toString(copia));
    }
}
"""
    },
    {
        "filename": "UsoArrayList.java",
        "code": """package Code.Gemini.v1.Rare;

import java.util.ArrayList;

public class UsoArrayList {
    public static void main(String[] args) {
        ArrayList<String> nombres = new ArrayList<>();
        
        nombres.add("Ana");
        nombres.add("Luis");
        nombres.add("Marta");
        
        System.out.println("El segundo nombre en la lista es: " + nombres.get(1));
        System.out.println("Tamaño de la lista: " + nombres.size());
    }
}
"""
    },
    {
        "filename": "RecorrerArrayList.java",
        "code": """package Code.Gemini.v1.Rare;

import java.util.ArrayList;

public class RecorrerArrayList {
    public static void main(String[] args) {
        ArrayList<Integer> numeros = new ArrayList<>();
        numeros.add(100);
        numeros.add(200);
        numeros.add(300);

        System.out.println("Elementos de la lista:");
        for (Integer numero : numeros) {
            System.out.println("- " + numero);
        }
    }
}
"""
    },
    {
        "filename": "OverrideToString.java",
        "code": """package Code.Gemini.v1.Rare;

class Coordenada {
    int x, y;

    public Coordenada(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coordenada(x=" + x + ", y=" + y + ")";
    }
}

public class OverrideToString {
    public static void main(String[] args) {
        Coordenada punto = new Coordenada(10, 25);
        System.out.println("El objeto es: " + punto);
    }
}
"""
    },
    {
        "filename": "ImplementarInterfaz.java",
        "code": """package Code.Gemini.v1.Rare;

interface Dibujable {
    void dibujar();
}

class Circulo implements Dibujable {
    public void dibujar() {
        System.out.println("Dibujando un círculo: O");
    }
}

public class ImplementarInterfaz {
    public static void main(String[] args) {
        Circulo miCirculo = new Circulo();
        miCirculo.dibujar();
    }
}
"""
    },
    {
        "filename": "UsoDeThis.java",
        "code": """package Code.Gemini.v1.Rare;

class Empleado {
    String nombre;

    public Empleado(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return this.nombre;
    }
}

public class UsoDeThis {
    public static void main(String[] args) {
        Empleado emp = new Empleado("David");
        System.out.println("Nombre del empleado: " + emp.getNombre());
    }
}
"""
    },
    {
        "filename": "ConversionStringInt.java",
        "code": """package Code.Gemini.v1.Rare;

public class ConversionStringInt {
    public static void main(String[] args) {
        String numeroEnTexto = "123";
        int numeroReal = Integer.parseInt(numeroEnTexto);
        System.out.println("Número convertido a int: " + (numeroReal + 1));

        int otroNumero = 456;
        String otroTexto = String.valueOf(otroNumero);
        System.out.println("Número convertido a String: " + (otroTexto + "1"));
    }
}
"""
    },
    {
        "filename": "VerificadorNumeroPrimo.java",
        "code": """package Code.Gemini.v1.Rare;

public class VerificadorNumeroPrimo {
    public static void main(String[] args) {
        int numero = 29;
        boolean esPrimo = true;

        if (numero <= 1) {
            esPrimo = false;
        } else {
            for (int i = 2; i <= numero / 2; i++) {
                if (numero % i == 0) {
                    esPrimo = false;
                    break;
                }
            }
        }

        if (esPrimo) {
            System.out.println(numero + " es un número primo.");
        } else {
            System.out.println(numero + " no es un número primo.");
        }
    }
}
"""
    },
    {
        "filename": "QuitarEspacios.java",
        "code": """package Code.Gemini.v1.Rare;

public class QuitarEspacios {
    public static void main(String[] args) {
        String conEspacios = "   Hola Mundo    ";
        String sinEspacios = conEspacios.trim();

        System.out.println("Original: '" + conEspacios + "'");
        System.out.println("Limpio: '" + sinEspacios + "'");
    }
}
"""
    },
    {
        "filename": "UtilidadEstatica.java",
        "code": """package Code.Gemini.v1.Rare;

class ConvertidorTemperatura {
    public static double celsiusAFahrenheit(double celsius) {
        return (celsius * 9 / 5) + 32;
    }
}

public class UtilidadEstatica {
    public static void main(String[] args) {
        double tempCelsius = 20.0;
        double tempFahrenheit = ConvertidorTemperatura.celsiusAFahrenheit(tempCelsius);
        
        System.out.println(tempCelsius + "°C equivale a " + tempFahrenheit + "°F.");
    }
}
"""
    },
    {
        "filename": "CajeroAutomatico.java",
        "code": """package Code.Gemini.v1.Rare;

class CuentaBancaria {
    private double saldo = 0.0;

    public void depositar(double cantidad) {
        if (cantidad > 0) {
            saldo += cantidad;
            System.out.println("Depósito exitoso. Nuevo saldo: " + saldo);
        }
    }

    public void retirar(double cantidad) {
        if (cantidad > 0 && cantidad <= saldo) {
            saldo -= cantidad;
            System.out.println("Retiro exitoso. Nuevo saldo: " + saldo);
        } else {
            System.out.println("Fondos insuficientes o cantidad inválida.");
        }
    }
}

public class CajeroAutomatico {
    public static void main(String[] args) {
        CuentaBancaria miCuenta = new CuentaBancaria();
        miCuenta.depositar(500.0);
        miCuenta.retirar(150.0);
        miCuenta.retirar(400.0);
    }
}
"""
    },
    {
        "filename": "VerificadorDeCaracteres.java",
        "code": """package Code.Gemini.v1.Rare;

public class VerificadorDeCaracteres {
    public static void main(String[] args) {
        char ch1 = 'A';
        char ch2 = '7';
        char ch3 = ' ';

        System.out.println("'" + ch1 + "' es una letra? " + Character.isLetter(ch1));
        System.out.println("'" + ch2 + "' es un dígito? " + Character.isDigit(ch2));
        System.out.println("'" + ch3 + "' es un espacio en blanco? " + Character.isWhitespace(ch3));
    }
}
"""
    },
    {
        "filename": "UnionDeArrays.java",
        "code": """package Code.Gemini.v1.Rare;

import java.util.Arrays;

public class UnionDeArrays {
    public static void main(String[] args) {
        int[] array1 = {1, 2, 3};
        int[] array2 = {4, 5, 6};
        
        int[] resultado = new int[array1.length + array2.length];
        
        System.arraycopy(array1, 0, resultado, 0, array1.length);
        System.arraycopy(array2, 0, resultado, array1.length, array2.length);
        
        System.out.println("El array combinado es: " + Arrays.toString(resultado));
    }
}
"""
    },
    {
        "filename": "AdivinaElNumero.java",
        "code": """package Code.Gemini.v1.Rare;

public class AdivinaElNumero {
    public static void main(String[] args) {
        int numeroSecreto = 42;
        int intento = 30;

        System.out.println("Intentando adivinar el número secreto...");
        
        if (intento == numeroSecreto) {
            System.out.println("¡Felicidades! Adivinaste el número.");
        } else if (intento < numeroSecreto) {
            System.out.println("El número secreto es mayor.");
        } else {
            System.out.println("El número secreto es menor.");
        }
    }
}
"""
    },
    {
        "filename": "CalculoMCD.java",
        "code": """package Code.Gemini.v1.Rare;

public class CalculoMCD {
    public static void main(String[] args) {
        int n1 = 50, n2 = 150;
        int mcd = 1;

        for (int i = 1; i <= n1 && i <= n2; i++) {
            if (n1 % i == 0 && n2 % i == 0) {
                mcd = i;
            }
        }
        System.out.printf("El MCD de %d y %d es %d.%n", n1, n2, mcd);
    }
}
"""
    },
    {
        "filename": "EliminarElemento.java",
        "code": """package Code.Gemini.v1.Rare;

import java.util.ArrayList;

public class EliminarElemento {
    public static void main(String[] args) {
        ArrayList<String> planetas = new ArrayList<>();
        planetas.add("Mercurio");
        planetas.add("Venus");
        planetas.add("Tierra");
        
        planetas.remove(1);
        
        System.out.println("La lista de planetas actualizada es: " + planetas);
    }
}
"""
    },
    {
        "filename": "ListaVacia.java",
        "code": """package Code.Gemini.v1.Rare;

import java.util.ArrayList;

public class ListaVacia {
    public static void main(String[] args) {
        ArrayList<String> miLista = new ArrayList<>();

        if (miLista.isEmpty()) {
            System.out.println("La lista está vacía. Añadiendo elementos...");
            miLista.add("Primer Elemento");
        }
        
        System.out.println("Ahora la lista contiene: " + miLista);
    }
}
"""
    },
    {
        "filename": "ProgramaVacio.java",
        "code": """package Code.Gemini.v1.Rare;

public class ProgramaVacio {
    public static void main(String[] args) {
        // Este programa se compila y ejecuta, pero no hace nada.
    }
}
"""
    },
    {
        "filename": "AccesoAMatriz.java",
        "code": """package Code.Gemini.v1.Rare;

public class AccesoAMatriz {
    public static void main(String[] args) {
        String[][] tablero = {
            {"A1", "B1", "C1"},
            {"A2", "B2", "C2"},
            {"A3", "B3", "C3"}
        };

        String centro = tablero[1][1];
        System.out.println("La casilla central del tablero es: " + centro);
    }
}
"""
    },
    {
        "filename": "LogicaShortCircuit.java",
        "code": """package Code.Gemini.v1.Rare;

public class LogicaShortCircuit {
    
    public static boolean segundoMetodo() {
        System.out.println("El segundo método fue llamado.");
        return true;
    }

    public static void main(String[] args) {
        System.out.println("Probando AND:");
        if (false && segundoMetodo()) {
            // No entra aquí y segundoMetodo() no se ejecuta
        }

        System.out.println("\\nProbando OR:");
        if (true || segundoMetodo()) {
            // Entra aquí, y segundoMetodo() no se ejecuta
        }
    }
}
"""
    },
    {
        "filename": "DivisorPorComas.java",
        "code": """package Code.Gemini.v1.Rare;

import java.util.Arrays;

public class DivisorPorComas {
    public static void main(String[] args) {
        String csv = "manzana,banana,naranja,fresa";
        String[] frutas = csv.split(",");
        
        System.out.println("Frutas obtenidas de la cadena:");
        System.out.println(Arrays.toString(frutas));
    }
}
"""
    },
    {
        "filename": "ManejoDeErrores.java",
        "code": """package Code.Gemini.v1.Rare;

public class ManejoDeErrores {
    public static void main(String[] args) {
        int[] numeros = {10, 20, 30};

        try {
            System.out.println("El valor es: " + numeros[5]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: Intentaste acceder a una posición fuera del array.");
        }
        
        System.out.println("El programa continúa después del error.");
    }
}
"""
    },
    {
        "filename": "RecursionSimple.java",
        "code": """package Code.Gemini.v1.Rare;

public class RecursionSimple {

    public static void cuentaRegresiva(int n) {
        if (n <= 0) {
            System.out.println("¡Fin!");
            return;
        }
        System.out.println(n);
        cuentaRegresiva(n - 1);
    }

    public static void main(String[] args) {
        cuentaRegresiva(5);
    }
}
"""
    },
    {
        "filename": "PruebaPolimorfismo.java",
        "code": """package Code.Gemini.v1.Rare;

class AnimalPolimorfico {
    public void hacerSonido() {
        System.out.println("El animal hace un sonido genérico.");
    }
}

class PerroPolimorfico extends AnimalPolimorfico {
    @Override
    public void hacerSonido() {
        System.out.println("El perro ladra: ¡Guau!");
    }
}

public class PruebaPolimorfismo {
    public static void main(String[] args) {
        AnimalPolimorfico miAnimal = new PerroPolimorfico();
        miAnimal.hacerSonido();
    }
}
"""
    },
    {
        "filename": "EncadenarMetodos.java",
        "code": """package Code.Gemini.v1.Rare;

public class EncadenarMetodos {
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Hola").append(" ").append("Mundo").reverse();
        
        System.out.println(sb.toString());
    }
}
"""
    },
    {
        "filename": "FechaYHora.java",
        "code": """package Code.Gemini.v1.Rare;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FechaYHora {
    public static void main(String[] args) {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        
        String fechaFormateada = ahora.format(formato);
        
        System.out.println("La fecha y hora actuales son: " + fechaFormateada);
    }
}
"""
    },
    {
        "filename": "CompararArrays.java",
        "code": """package Code.Gemini.v1.Rare;

import java.util.Arrays;

public class CompararArrays {
    public static void main(String[] args) {
        int[] arr1 = {1, 2, 3};
        int[] arr2 = {1, 2, 3};
        int[] arr3 = {3, 2, 1};
        
        System.out.println("¿arr1 es igual a arr2? " + Arrays.equals(arr1, arr2));
        System.out.println("¿arr1 es igual a arr3? " + Arrays.equals(arr1, arr3));
    }
}
"""
    },
    {
        "filename": "LlenarArray.java",
        "code": """package Code.Gemini.v1.Rare;

import java.util.Arrays;

public class LlenarArray {
    public static void main(String[] args) {
        int[] potenciasDeDos = new int[5];

        for (int i = 0; i < potenciasDeDos.length; i++) {
            potenciasDeDos[i] = (int) Math.pow(2, i);
        }

        System.out.println("Potencias de 2: " + Arrays.toString(potenciasDeDos));
    }
}
"""
    },
    {
        "filename": "ValidadorDePassword.java",
        "code": """package Code.Gemini.v1.Rare;

public class ValidadorDePassword {
    public static void main(String[] args) {
        String password = "123";
        
        if (password.length() >= 8) {
            System.out.println("La contraseña es segura.");
        } else {
            System.out.println("La contraseña es demasiado corta.");
        }
    }
}
"""
    },
    {
        "filename": "PalabraMasLarga.java",
        "code": """package Code.Gemini.v1.Rare;

public class PalabraMasLarga {
    public static void main(String[] args) {
        String frase = "Encuentra la palabra mas larga aqui";
        String[] palabras = frase.split(" ");
        String masLarga = "";

        for (String palabra : palabras) {
            if (palabra.length() > masLarga.length()) {
                masLarga = palabra;
            }
        }
        System.out.println("La palabra más larga es: '" + masLarga + "'");
    }
}
"""
    },
    {
        "filename": "PruebaConstructorPrivado.java",
        "code": """package Code.Gemini.v1.Rare;

class Configuracion {
    private static final Configuracion instancia = new Configuracion();
    
    private Configuracion() {}
    
    public static Configuracion obtenerInstancia() {
        return instancia;
    }
}

public class PruebaConstructorPrivado {
    public static void main(String[] args) {
        Configuracion conf1 = Configuracion.obtenerInstancia();
        Configuracion conf2 = Configuracion.obtenerInstancia();
        
        System.out.println("Se obtuvo la instancia única de Configuración.");
    }
}
"""
    },
    {
        "filename": "Interruptor.java",
        "code": """package Code.Gemini.v1.Rare;

public class Interruptor {
    public static void main(String[] args) {
        boolean luzEncendida = false;
        System.out.println("La luz está apagada.");

        luzEncendida = !luzEncendida;
        System.out.println("¡Click! La luz está ahora " + (luzEncendida ? "encendida." : "apagada."));

        luzEncendida = !luzEncendida;
        System.out.println("¡Click! La luz está ahora " + (luzEncendida ? "encendida." : "apagada."));
    }
}
"""
    },
    {
        "filename": "TallerMecanico.java",
        "code": """package Code.Gemini.v1.Rare;

class Auto {
    void acelerar() {
        System.out.println("El coche acelera normalmente.");
    }
}

class AutoDeportivo extends Auto {
    @Override
    void acelerar() {
        System.out.println("¡El coche deportivo acelera rápidamente!");
    }
}

public class TallerMecanico {
    public static void main(String[] args) {
        Auto miAuto = new Auto();
        AutoDeportivo miDeportivo = new AutoDeportivo();
        
        miAuto.acelerar();
        miDeportivo.acelerar();
    }
}
"""
    },
    {
        "filename": "ConvertirListaAArray.java",
        "code": """package Code.Gemini.v1.Rare;

import java.util.ArrayList;
import java.util.Arrays;

public class ConvertirListaAArray {
    public static void main(String[] args) {
        ArrayList<String> lista = new ArrayList<>();
        lista.add("Java");
        lista.add("Python");
        lista.add("C++");

        String[] array = new String[lista.size()];
        lista.toArray(array);

        System.out.println("Array resultante: " + Arrays.toString(array));
    }
}
"""
    },
    {
        "filename": "SumaDiagonalMatriz.java",
        "code": """package Code.Gemini.v1.Rare;

public class SumaDiagonalMatriz {
    public static void main(String[] args) {
        int[][] matriz = {
            {4, 5, 6},
            {7, 8, 9},
            {1, 2, 3}
        };
        int suma = 0;
        for (int i = 0; i < matriz.length; i++) {
            suma += matriz[i][i];
        }
        System.out.println("La suma de la diagonal principal es: " + suma);
    }
}
"""
    },
    {
        "filename": "CronometroSimple.java",
        "code": """package Code.Gemini.v1.Rare;

public class CronometroSimple {
    public static void main(String[] args) {
        long inicio = System.nanoTime();

        for(int i = 0; i < 100000; i++) {
            // Tarea a medir
        }

        long fin = System.nanoTime();
        long duracion = (fin - inicio) / 1000000;

        System.out.println("La tarea tomó: " + duracion + " milisegundos.");
    }
}
"""
    },
    {
        "filename": "DivisionYResto.java",
        "code": """package Code.Gemini.v1.Rare;

public class DivisionYResto {
    public static void main(String[] args) {
        int dividendo = 10;
        int divisor = 3;

        int cociente = dividendo / divisor;
        int resto = dividendo % divisor;

        System.out.println(dividendo + " / " + divisor + " = " + cociente + " con resto " + resto + ".");
    }
}
"""
    },
    {
        "filename": "PruebaInmutabilidad.java",
        "code": """package Code.Gemini.v1.Rare;

final class Mensaje {
    private final String contenido;

    public Mensaje(String contenido) {
        this.contenido = contenido;
    }

    public String getContenido() {
        return contenido;
    }
}

public class PruebaInmutabilidad {
    public static void main(String[] args) {
        Mensaje m = new Mensaje("Este es un mensaje secreto.");
        System.out.println(m.getContenido());
    }
}
"""
    },
    {
        "filename": "PiramideDeAsteriscos.java",
        "code": """package Code.Gemini.v1.Rare;

public class PiramideDeAsteriscos {
    public static void main(String[] args) {
        int filas = 5;

        for (int i = 1; i <= filas; i++) {
            for (int j = 1; j <= i; j++) {
                System.out.print("* ");
            }
            System.out.println();
        }
    }
}
"""
    },
    {
        "filename": "BuscarDuplicados.java",
        "code": """package Code.Gemini.v1.Rare;

public class BuscarDuplicados {
    public static void main(String[] args) {
        int[] numeros = {1, 2, 3, 4, 2, 5, 6, 1};
        
        System.out.println("Elementos duplicados encontrados:");
        for (int i = 0; i < numeros.length; i++) {
            for (int j = i + 1; j < numeros.length; j++) {
                if (numeros[i] == numeros[j]) {
                    System.out.println(numeros[j]);
                }
            }
        }
    }
}
"""
    },
    {
        "filename": "ProgramaCien.java",
        "code": """package Code.Gemini.v1.Rare;

public class ProgramaCien {
    public static void main(String[] args) {
        // ¡Lo logramos! Este es el programa número 100.
        System.out.println("¡Felicidades por completar los 100 programas en Java!");
    }
}
"""
    }
]

# --- Lógica del Script para crear los archivos ---

def create_java_files():
    """
    Crea la estructura de directorios y escribe cada archivo Java.
    """
    # Define la ruta base para los paquetes.
    # os.path.join se encarga de usar el separador correcto ('/' o '\\')
    # según el sistema operativo.
    package_path = os.path.join("Code", "Gemini", "v1", "Rare")

    # Crea los directorios necesarios.
    # exist_ok=True evita que se lance un error si las carpetas ya existen.
    try:
        os.makedirs(package_path, exist_ok=True)
        print(f"Directorio '{package_path}' asegurado.")
    except OSError as e:
        print(f"Error al crear el directorio: {e}")
        return

    # Itera sobre la lista de programas y crea cada archivo.
    files_created_count = 0
    for program in programs_data:
        file_name = program["filename"]
        file_code = program["code"]
        file_path = os.path.join(package_path, file_name)

        try:
            # Abre el archivo en modo escritura ('w') con codificación UTF-8.
            # 'with' se encarga de cerrar el archivo automáticamente.
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(file_code)
            files_created_count += 1
        except IOError as e:
            print(f"No se pudo escribir el archivo {file_name}: {e}")

    print("-" * 30)
    print(f"✅ ¡Proceso completado! Se han generado {files_created_count} archivos .java.")
    print(f"Puedes encontrarlos en la carpeta: {os.path.abspath(package_path)}")

# --- Ejecución del script ---
if __name__ == "__main__":
    create_java_files()