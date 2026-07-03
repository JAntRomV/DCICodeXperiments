package Code.Java.Minimalist.v2;

public class MedidorTiempo {

    /**
     * Mide el tiempo de ejecución de un bloque de código en nanosegundos.
     * @param codigo El bloque de código o línea a ejecutar.
     * @return El tiempo transcurrido en nanosegundos.
     */
    public static long medirEnNanosegundos(Runnable codigo) {
        long inicio = System.nanoTime();
        
        codigo.run(); // Se ejecuta la línea o bloque de código
        
        long fin = System.nanoTime();
        return (fin - inicio);
    }

    public static void main(String[] args) {
        // Ejemplo de uso con una sola línea
        long tiempoLinea = medirEnNanosegundos(() -> Math.sin(Math.toRadians(45)));
        System.out.println("La operación matemática tardó: " + tiempoLinea + " ns");
    }
}