package dci.exp.one.services;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.classgraph.ClassInfo;
import jakarta.inject.Singleton;

import java.lang.reflect.Method;

@Singleton
public class RunnerService {

    private static final String PACKAGE_TO_SCAN = "dci.exp.one.abacus.v1.rare";

    /**
     * Escanea el paquete y ejecuta el main() de todas las clases que lo tengan.
     */
    public void runAllMains() {
        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .acceptPackages(PACKAGE_TO_SCAN)
                .scan()) {

            for (ClassInfo classInfo : scanResult.getAllClasses()) {
                try {
                    Class<?> clazz = classInfo.loadClass();
                    Method main = clazz.getMethod("main", String[].class);

                    System.out.println("\n==============================");
                    System.out.println(" Ejecutando: " + clazz.getName());
                    System.out.println("==============================");

                    String[] args = new String[]{}; // si necesitas args, ponlos aquí
                    main.invoke(null, (Object) args);

                } catch (NoSuchMethodException e) {
                    // La clase no tiene main(String[]), la ignoramos
                    System.out.println("Clase sin main(): " + classInfo.getName() + ", se omite.");
                } catch (Exception e) {
                    System.err.println("Error ejecutando " + classInfo.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public void runHelloNTimes(int n){
        if(n<=0){
            
        }
    }
}
