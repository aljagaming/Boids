package Parallel;

import Executive.ExecutionInterface;
import Executive.Variables;
import FPS.Clock;
import Functions.Functions;
import Gui.Logger;

import java.util.concurrent.*;


public class ParallelExe implements ExecutionInterface {


    int numberOfThreads=0;
    //CountDownLatch waitForAllThreads = new CountDownLatch(20);
    Variables variables;
    Boolean running=true;
    ExecutorService executor;
    CyclicBarrier collectAllThreads;
    //ExecutorService executor;

    public ParallelExe(Variables variables) {
        this.variables = variables;
    }

    @Override
    public void start(String[] args) {
        //Two/Three ways of doing this
        //Each boid is its own thread M1 Macbook Air typically can handle 25 000 software threads so should be fine
        //Boid array is split into chunks and each thread does one part

        Logger.getInstance().log("Parallel.exe started!");


        int cores = Runtime.getRuntime().availableProcessors();
        if (variables.getBoxGrid().numberOfBoxesX/2<cores){ //in the RARE case there is more cores than boxes in x direction
            numberOfThreads=variables.getBoxGrid().numberOfBoxesX/2;
        }else{
            numberOfThreads=cores-1;
        }


        int chunkSizeX = variables.getBoxGrid().numberOfBoxesX/numberOfThreads;
        int remainderX = variables.getBoxGrid().numberOfBoxesX % numberOfThreads;
        Runnable[] runnableArr=new Runnable[numberOfThreads];

        //SYNCHRONIZATION Tools
        collectAllThreads=new CyclicBarrier(numberOfThreads+1);
        executor=Executors.newFixedThreadPool(numberOfThreads);

        int startX=0;
        for (int i = 0; i < numberOfThreads; i++) {
            int endX = startX + chunkSizeX;
            if (remainderX>0){
                endX+=1;
                remainderX-=1;
            }
            //startX is inclusive and endX is exclusive
            Chunk chunk=new Chunk(variables.getBoxGrid(), startX, endX);
            Runnable job=new OneChunkOneThread(chunk, variables, numberOfThreads, collectAllThreads);
            runnableArr[i]=job;
            startX=endX;
        }


        int count=0;


        while(running){
            count++;
            Clock.start();
            Functions.updateNumOfBoids(variables);


            for (Runnable r: runnableArr) {
                executor.submit(r);
            }

            try {
                collectAllThreads.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }

            collectAllThreads.reset();

            variables.draw();
            //wait for drawing to be complete as drawing is another thread (which JVM does on its own)
            try {
                variables.getBarrier().await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }

            Clock.end();
        }


        executor.shutdown();
        variables.getCurrentExe().start(args);

    }

    @Override
    public void stop() {
        running=false;
        Logger.getInstance().log("Parallel.exe stopping...");

    }


}
