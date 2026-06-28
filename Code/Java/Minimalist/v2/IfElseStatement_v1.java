package Code.Java.Minimalist.v1.Rare;

public class IfElseStatement_v1 {
    public static void main(String[] args) {
        long begin = System.nanoTime();
        System.out.println(begin);
        int value = 0;
        System.out.println(System.nanoTime());
        if (value > 0) {
            System.out.println(System.nanoTime());
        } else {
            System.out.println(System.nanoTime());
        }
        long end = System.nanoTime();
        System.out.println(end-begin);
    }
}
