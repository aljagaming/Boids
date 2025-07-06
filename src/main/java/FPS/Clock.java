package FPS;

import Gui.BoidField;

public class Clock {

    static long start;

    static final int AVGTHISMANY = 50;
    static int countAvg=0;

    static long SUM;


    public static void start(){
        start=System.currentTimeMillis();
    }

    public static void end(){

        SUM = SUM + ( System.currentTimeMillis()-start );
        countAvg++;

        if (countAvg >= AVGTHISMANY){

            long timePassed=Math.divideExact(SUM, AVGTHISMANY);

            if (timePassed==0){
                BoidField.fpsString=("FPS: "+ 1000 + " MS: " + 0);
                return;
            }

            BoidField.fpsString = ("FPS: "+ (1000/timePassed) + " MS: " + timePassed);

            SUM=0;
            countAvg=0;
        }

    }

}
