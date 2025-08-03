package Sequential;

import Boids.Boid;
import Boids.Vector3D;
import Executive.ExecutionInterface;
import Executive.Variables;
import FPS.Clock;
import Functions.Functions;
import Gui.Logger;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;

public class SequentialExe implements ExecutionInterface {
    Variables variables;

    boolean running=true;
    //boolean secondLap = false;
    Vector3D finalAcceleration = new Vector3D(0, 0, 0);

    public SequentialExe(Variables variables) {
        this.variables = variables;
    }



    @Override
    public void start() {

        Clock.reset();

        Clock.start();

        Logger.getInstance().log("Sequential.exe Started!");

        boolean first=true;
        while (running ) {

            if (!first) {
                Clock.start();
            }

            Functions.updateNumOfBoids(variables);


            for (Boid B : variables.getArrayOfBoids()) {

                finalAcceleration = Functions.theAllMightyFunction(B, variables);
                B.addAcceleration(finalAcceleration);

                //COMMENTED THE BOTTOM ONE OUT

                //finalAcceleration.multiply(0);//set back to zero
                B.move(variables.getAnimationSpeed()); //how fast

            }
            //this makes another thread because the JAVA-SWING uses Event Dispatcher Thread which draws stuff on gui
            //but would still consider this sequential....(I mean in sequential you cant have a button after all)
            //however we will need a blocking statement so while loop waits for thread to finish drawing - that way we kinda go back to sequential again
            variables.draw();

            //this kinda returns sequentially because we have to wait for other thread
            try {
                variables.getBarrier().await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }

            Clock.end();


            if (first) {
                first=false;
            }
        }

        //System.out.println("3 Hello I stopped");

        variables.getCurrentExe().start();



    }

    @Override
    public void stop() {

        //System.out.println("1 Hello Sequential should stop");
        Logger.getInstance().log("Sequential.exe stopping...");
        running=false;

    }

}