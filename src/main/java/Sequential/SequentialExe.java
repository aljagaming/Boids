package Sequential;

import Boids.Boid;
import Boids.Vector3D;
import Executive.ExecutionInterface;
import Executive.Variables;
import Functions.Functions;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;

public class SequentialExe implements ExecutionInterface {
    Variables variables;
    boolean secondLap = false;
    int temp = 0;

    Vector3D finalAcceleration = new Vector3D(0, 0, 0);

    public SequentialExe(Variables variables) {
        this.variables = variables;
    }

    @Override
    public void start() {


        while (true) {


            //1. check if #num of boids changed
            updateNumOfBoids();

            for (Boid B : variables.getArrayOfBoids()) {

                finalAcceleration = Functions.theAllMightyFunction(B, variables);
                B.addAcceleration(finalAcceleration);
                finalAcceleration.multiply(0);//set back to zero
                B.move(variables.getAnimationSpeed()); //how fast

            }


            //this makes another thread because the JAVASWING uses Event Dispatcher Thread which draws stuff on gui
            //but would still consider this sequential....(I mean in sequential you cant have a button afterall)
            //however we will need a blocking statemant so while loop waits for thread to finsh - that way we kinda go back to sequential again
            variables.draw();

            //this kinda returns sequentuallity becasue we have to wait for other thread
            try {
                variables.getBarrier().await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (BrokenBarrierException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @Override
    public void stop() {

    }


    //------------------------------------------------------------------------------------------------------

    //Maybe make a class for this latter


    private void updateNumOfBoids() {
        Random r = new Random();
        int numOfBoids = variables.getNumOfBoids();
        //restarting logic
        if (secondLap) {
            secondLap = false;
            numOfBoids = temp;
        }

        if (variables.isRestarting() && !secondLap) {
            variables.setRestarting(false);
            temp = numOfBoids;
            numOfBoids = 0;
            secondLap = true;
        }


        if (numOfBoids > variables.getArrayOfBoids().size()) {
            while (numOfBoids != variables.getArrayOfBoids().size()) {

                int border = 10;

                //add a new boid
                //KEEP IN MIND THIS GENERATES [0-99] INCLUSIVE
                float possibleX = r.nextInt((variables.getBoidFieldSize().width) - border);
                float possibleY = r.nextInt((variables.getBoidFieldSize().height) - border);

                if (possibleX < variables.getMAX_BOID_SIZE()) {
                    possibleX = border;
                }

                if (possibleY < variables.getMAX_BOID_SIZE()) {
                    possibleY = border;
                }


                variables.getArrayOfBoids().add(new Boid(new Vector3D(possibleX, possibleY, 1000)));
                //depth here is 1000

            }
        } else if (numOfBoids < variables.getArrayOfBoids().size()) {
            while (numOfBoids != variables.getArrayOfBoids().size()) {
                //add a new boid
                //KEEP IN MIND THIS GENERATES [0-99] INCLUSIVE
                //variables.setNumOfBoids(variables.getNumOfBoids()-1);
                variables.getArrayOfBoids().removeLast();
            }
        }
    }


}