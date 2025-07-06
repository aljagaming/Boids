package Parallel;

import Boids.Boid;
import Executive.ExecutionInterface;
import Executive.Variables;
import FPS.Clock;
import Functions.Functions;
import Gui.Logger;

import java.util.concurrent.*;


public class ParallelExe implements ExecutionInterface {


    int numberOfThreads=0;
    CountDownLatch waitForAllThreads = new CountDownLatch(20);
    Variables variables;
    boolean running=true;
    ExecutorService executor;

    public ParallelExe(Variables variables) {
        this.variables = variables;
    }

    @Override
    public void start() {
        //Two/Three ways of doing this
        //Each boid is its own thread M1 Macbook Air typically can handle 25 000 software threads so should be fine
        //Boid array is split into chunks and each thread does one part
        //


        Logger.getInstance().log("Parallel.exe started!");

        int cores = Runtime.getRuntime().availableProcessors();
        numberOfThreads= cores-1;
        //numberOfThreads=2000;
        executor= Executors.newFixedThreadPool(numberOfThreads);




        while(running) {

            Clock.start();

            Functions.updateNumOfBoids(variables);
            waitForAllThreads = new CountDownLatch(variables.getNumOfBoids());



            for (Boid B : variables.getArrayOfBoids()) {

                executor.submit(new OneBoidOneThread(B,variables, waitForAllThreads));

            }

            try {
                waitForAllThreads.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            variables.draw();

            try {
                variables.getBarrier().await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }


            Clock.end();
        }

        executor.shutdown();
        variables.getCurrentExe().start();
    }

    @Override
    public void stop() {
        running=false;
        Logger.getInstance().log("Parallel.exe stopping...");
    }


}
