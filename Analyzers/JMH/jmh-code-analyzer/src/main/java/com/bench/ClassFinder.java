package com.bench;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import java.util.Set;

public class ClassFinder {
    public static Set<Class<?>> getClassesInPackage(String packageName) {
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
        return reflections.getSubTypesOf(Object.class);
    }
}