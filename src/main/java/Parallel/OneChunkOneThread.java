package Parallel;

import Boids.Boid;
import Boids.Vector3D;
import Executive.Variables;
import FPS.Clock;
import Functions.Functions;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


public class OneChunkOneThread implements Runnable{

    Chunk chunk;
    Variables variables;
    int numberOfThreads;
    CyclicBarrier barrier;




    public OneChunkOneThread(Chunk chunk, Variables variables, int numberOfThreads, CyclicBarrier barrier) {
        this.chunk=chunk;
        this.variables = variables;
        this.numberOfThreads=numberOfThreads;
        this.barrier = barrier;

    }

    @Override
    public void run() {
        Vector3D acceleration;

        //this is time intensive task that can be paralelised;
        for (Boid b : chunk.getBoidsInThisChunk()) {
            acceleration = Functions.theAllMightyFunction(b, variables);
            b.addAcceleration(acceleration);
            b.move(variables.getAnimationSpeed());
        }


        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }

    }
}
