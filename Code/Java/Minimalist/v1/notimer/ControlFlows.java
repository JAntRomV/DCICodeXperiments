package code.java.minimalist.v1.notimer;

public class ControlFlows {

    private int[] values;

    public static void main(String[] args) {
        int n = 10;

        for (int i = 0; i < n; i++) {
            System.out.println(String.format("Parametro N=%d",i));
            ControlFlows controlFlows = new ControlFlows();
            controlFlows.run(i);
        }
    }

    public void run(int n) {
        initValues(n);

        // IF
        if ((n % 2) == 0) {
            System.out.println("El valor " + n + " es par");
        }

        // IF ELSE
        if ((n % 2) == 0) {
            System.out.println("Entró por el bloque if");
        } else {
            System.out.println("Entró por el bloque else");
        }

        // FOR
        for (int i = 0; i < n; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        // FOR EACH
        for (int value : values) {
            System.out.print(value + " ");
        }
        System.out.println();

        // WHILE
        int j = 0;
        while (j <= n) {
            j++;
        }

        // DO WHILE
        int k = 0;
        do {
            k++;
        } while (k <= n);

        // SWITCH
        switch (n % 2) {
            case 0:
                System.out.println("Switch: caso par");
                break;
            case 1:
                System.out.println("Switch: caso impar");
                break;
            default:
                System.out.println("Switch: caso por defecto");
        }

        System.out.println("Ejecución completada");
    }

    private void initValues(int n) {
        values = new int[n];
        for (int i = 0; i < n; i++) {
            values[i] = i;
        }
    }
}
