package FPS;

import Gui.BoidField;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Clock {

    static long start;

     static ArrayList<Integer> arr=new ArrayList<>();


    static final int AVGTHISMANY = 25;
    static int countAvg=0;

    static long SUM;

    public static void reset() {


        start=0;
        Clock.countAvg = 0;
        SUM=0;

        /*
        //  /Users/al/Boids/mpj-v0_44/

        try (FileWriter writer = new FileWriter("/Users/al/Boids/logs/test_output.txt", true)) {
            writer.write("------------------------------------------" + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }



        int writeAvg;
        int writeMax= -1;
        int writeMin=Integer.MAX_VALUE;
        int writeCount=0;

        for (int i = 0; i < arr.size(); i++) {

            if (arr.get(i)>writeMax){
                writeMax=arr.get(i);
            }

            if (arr.get(i)<writeMin){
                writeMin=arr.get(i);
            }

            writeCount=writeCount+arr.get(i);


        }
        if (writeCount==0){
            arr.clear();
            return;
        }

        writeAvg=writeCount/arr.size();



        try (FileWriter writer = new FileWriter("/Users/al/Boids/logs/test_output.txt", true)) {
            writer.write("Average FPS: "+writeAvg + " MAX FPS: "+ writeMax+ " MIN FPS: "+writeMin + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

         */


    }

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
