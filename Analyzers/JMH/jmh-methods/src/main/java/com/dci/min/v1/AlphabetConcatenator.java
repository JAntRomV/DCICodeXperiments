package com.dci.min.v1;

public class AlphabetConcatenator {

    /**
     * Devuelve las primeras N letras del alfabeto en minúscula.
     *
     * @param N número de letras a concatenar
     * @return cadena con las primeras N letras: "a", "ab", "abc", ...
     * @throws IllegalArgumentException si N está fuera de rango [0, 26]
     */
    public static String concatLetters(int N) {
        if (N < 0 || N > 26) {
            throw new IllegalArgumentException("N debe estar entre 0 y 26");
        }

        StringBuilder builder = new StringBuilder(N);
        for (int i = 0; i < N; i++) {
            builder.append((char) ('a' + i));
        }
        return builder.toString();
    }
}
