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
    CountDownLatch latch =new CountDownLatch(2);

    int visualRange;
    int cohesionFactor;
    int separationFactor;
    int alignmentFactor;

    boolean secondLap=false;
    int temp = 0;

    Vector3D finalAcceleration =new Vector3D(0,0,0);

    public SequentialExe(Variables variables) {
        this.variables = variables;
    }

    @Override
    public void start() {
        Random r=new Random();


        while (true){

            cohesionFactor=variables.getCoherence();
            separationFactor=variables.getSeparation();
            alignmentFactor=variables.getAlignment();
            visualRange= variables.getVisualRange();




            Vector3D cummulativeAceleration=new Vector3D(0,0,0);

            //1. check if #num of boids changed
            updateNumOfBoids();

            for (Boid B:variables.getArrayOfBoids()) {


                if (visualRange!=0){

                    finalAcceleration=Functions.theAllMightyFunction(B,variables.getArrayOfBoids(),visualRange,cohesionFactor,separationFactor,alignmentFactor);

                    B.addAcceleration(finalAcceleration);
                    finalAcceleration.multiply(0);//set back to zero

                };

                B.addAcceleration(border(B));

                B.move(variables.getAnimationSpeed()); //normalize speed - 0-1

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


    private void updateNumOfBoids(){
        Random r=new Random();
        int numOfBoids= variables.getNumOfBoids();
        //restarting logic
        if (secondLap){
            secondLap=false;
            numOfBoids=temp;
        }

        if (variables.isRestarting() && !secondLap) {
            variables.setRestarting(false);
            temp = numOfBoids;
            numOfBoids = 0;
            secondLap=true;
        }


        if (numOfBoids > variables.getArrayOfBoids().size()) {
                while (numOfBoids!= variables.getArrayOfBoids().size()) {

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
    //---------------------------------------------------------------------------------------------------------



    public Vector3D border(Boid b){
        Vector3D returnV=new Vector3D(0,0,0);
        //need to calculate the buffer zone how much it takes for a boid to turns around completely


        float xPos=b.getPosition().getX();
        float yPos=b.getPosition().getY();
        float zPos=b.getPosition().getZ();
        float softBound=b.getSPEED()/b.getMAX_FORCE(); //20

        softBound=softBound/2;
        float turnRadius=b.getMAX_FORCE();
        float course=b.getCourse();





        //For X
        if (xPos<softBound){//LEFT WALL
            if (course < Math.PI) {
                returnV.setX(+turnRadius);
            } else {//Math.Pi-2*Math.Pi
                returnV.setX(-turnRadius);
            }

        } else if (xPos>(variables.getBoidFieldSize().width-softBound)) {//RIGHT WALL
            if (course < Math.PI) {//0-Math.PI
                returnV.setX(-turnRadius);
            } else {//Math.Pi-2*Math.Pi
                returnV.setX(+turnRadius);
            }
        }


        if (yPos<softBound){//CEILING
            if (course>Math.PI*3/2 && course<Math.PI/2) {
                returnV.setY(-turnRadius);
            } else {//Math.Pi-2*Math.Pi
                returnV.setY(+turnRadius);
            }
        } else if (yPos>variables.getBoidFieldSize().height-softBound) {//FLOOR
            if (course>Math.PI*3/2 && course<Math.PI/2) {
                returnV.setY(+turnRadius);
            } else {//Math.Pi-2*Math.Pi
                returnV.setY(-turnRadius);
            }
        }


        if (zPos<0+softBound){
            returnV.setZ(+turnRadius);

        }else if(zPos>1000-softBound){
            returnV.setZ(-turnRadius);
        }



        /*

    if (xPos < 10) {
        // Gradually turn towards the new direction
        if (b.getCourse() > Math.PI) {
            b.setCourse(b.getCourse() - turnRate);  // Turn counter-clockwise
        } else {
            b.setCourse(b.getCourse() + turnRate);  // Turn clockwise
        }
        b.setxPos(10);
    } else if (xPos > (variables.getBoidFieldSize().width - 20)) {
        // Gradually turn towards the new direction
        if (b.getCourse() < Math.PI) {
            b.setCourse(b.getCourse() + turnRate);  // Turn clockwise
        } else {
            b.setCourse(b.getCourse() - turnRate);  // Turn counter-clockwise
        }
        b.setxPos(variables.getBoidFieldSize().width - 20);
    } else if (yPos < 10) {
        b.setCourse(b.getCourse() - turnRate);  // Gradually turn upwards
        b.setyPos(10);
    } else if (yPos > (variables.getBoidFieldSize().height - 20)) {
        b.setCourse(b.getCourse() + turnRate);  // Gradually turn downwards
        b.setyPos(variables.getBoidFieldSize().height - 20);
    }
}
         */

        return returnV;
    }

}
