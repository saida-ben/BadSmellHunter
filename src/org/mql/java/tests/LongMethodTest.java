package org.mql.java.tests;

public class LongMethodTest {
    public void veryLongMethod() {
        for (int i = 0; i < 50; i++) {
            System.out.println("Line " + i);
        }
        // Répète des instructions inutiles pour la taille
        for (int i = 0; i < 50; i++) {
            System.out.println("Line " + i);
        }
    }
    public void longMethodWithTooManyParams(String p1, String p2, int p3, double p4, boolean p5, char p6) {
        System.out.println(p1 + p2 + p3 + p4 + p5 + p6);
    }

}
