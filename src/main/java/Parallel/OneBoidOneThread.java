package Parallel;

import Boids.Boid;
import Boids.Vector3D;
import Executive.Variables;
import Functions.Functions;
import java.util.concurrent.CountDownLatch;

public class OneBoidOneThread implements Runnable{

    Variables variables;
    private Boid b;  // store the passed boid
    CountDownLatch latch;
    Vector3D acceleration;

    public OneBoidOneThread(Boid boid, Variables variables, CountDownLatch latch) {
        this.b = boid;
        this.variables=variables;
        this.latch=latch;
    }

    @Override
    public void run() {
        acceleration = Functions.theAllMightyFunction(b, variables);
        b.addAcceleration(acceleration);
        b.move(variables.getAnimationSpeed());
        latch.countDown();
    }
}
