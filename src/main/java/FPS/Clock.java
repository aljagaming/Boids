package FPS;

import Gui.BoidField;

public class Clock {

    static long start;

    static final int AVGTHISMANY = 50;
    static int countAvg=0;

    static long SUM;


    public static void start(){
        start=System.nanoTime();
    }

    public static void end(){


        long elapsed = System.nanoTime() - start;
        SUM += elapsed;
        countAvg++;



        if (countAvg >= AVGTHISMANY) {
            long avg = SUM / AVGTHISMANY;

            long fps =  Math.round(1000_000_000.0 / avg);

            BoidField.fpsString = String.format("FPS: " +fps+ " MS: " + avg/1000_000);

            SUM = 0;
            countAvg = 0;
        }

    }

}
