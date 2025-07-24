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
            long avgNano = SUM / AVGTHISMANY;
            double ms = Math.round(avgNano / 1_000_000.0);
            double fps = Math.round( 1000.0 / ms);

            BoidField.fpsString = String.format("FPS: " +fps+ " MS: " + ms);

            SUM = 0;
            countAvg = 0;
        }

    }

}
