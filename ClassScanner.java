import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;

public class ClassScanner {

    private static final String TARGET_PACKAGE = "Code.Java.Abacus.v1.Rare";
    private static final String OUTPUT_FILE    = "classes_to_profile.txt";

    public static void main(String[] args) throws Exception {
        List<String> found = new ArrayList<>();
        String cp = System.getProperty("java.class.path");

        for (String entry : cp.split(File.pathSeparator)) {
            File f = new File(entry);
            if (f.isDirectory()) {
                scanDirectory(f, f, found);
            } else if (entry.endsWith(".jar")) {
                scanJar(f, found);
            }
        }

        // Filtrar solo las que tienen main()
        List<String> executable = new ArrayList<>();
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        for (String className : found) {
            try {
                Class<?> c = cl.loadClass(className);
                c.getMethod("main", String[].class);
                executable.add(className);
                System.out.println("[OK] " + className);
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                System.out.println("[SKIP] " + className + " — sin main()");
            }
        }

        Files.write(Paths.get(OUTPUT_FILE),
                    String.join("\n", executable).getBytes());
        System.out.println("\n" + executable.size() + " clases guardadas en " + OUTPUT_FILE);
    }

    private static void scanDirectory(File root, File current, List<String> result) {
        for (File f : Objects.requireNonNull(current.listFiles())) {
            if (f.isDirectory()) {
                scanDirectory(root, f, result);
            } else if (f.getName().endsWith(".class")) {
                String rel  = root.toURI().relativize(f.toURI()).getPath();
                String name = rel.replace("/", ".").replace(".class", "");
                if (name.startsWith(TARGET_PACKAGE)) result.add(name);
            }
        }
    }

    private static void scanJar(File jar, List<String> result) throws Exception {
        try (JarFile jf = new JarFile(jar)) {
            Enumeration<JarEntry> en = jf.entries();
            while (en.hasMoreElements()) {
                String name = en.nextElement().getName();
                if (name.endsWith(".class")) {
                    String cn = name.replace("/", ".").replace(".class", "");
                    if (cn.startsWith(TARGET_PACKAGE)) result.add(cn);
                }
            }
        }
    }
}