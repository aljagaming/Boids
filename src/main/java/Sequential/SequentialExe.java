package Sequential;

import Boids.Boid;
import Boids.Vector3D;
import Executive.ExecutionInterface;
import Executive.Variables;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;

public class SequentialExe implements ExecutionInterface {
    Variables variables;
    CountDownLatch latch =new CountDownLatch(2);

    float visualRange;
    float cohesionFactor;
    float separationFactor;
    float alignmentFactor;
    Random r=new Random();

    Vector3D finalAcceleration =new Vector3D(0,0,0);

    public SequentialExe(Variables variables) {
        this.variables = variables;
    }

    @Override
    public void start() {
        Random r=new Random();


        while (true){

            cohesionFactor= (float) variables.getCoherence()/100;
            separationFactor= (float) variables.getSeparation()/100;
            alignmentFactor=(float) variables.getAlignment()/100;
            visualRange= variables.getVisualRange();




            Vector3D cummulativeAceleration=new Vector3D(0,0,0);

            //1. check if #num of boids changed
            updateNumOfBoids(r);

            for (Boid B:variables.getArrayOfBoids()) {


                if (visualRange!=0){


                    finalAcceleration.add(alignment(B,variables.getArrayOfBoids()));
                    finalAcceleration.add(cohesion(B,variables.getArrayOfBoids()));
                    finalAcceleration.add(separation(B,variables.getArrayOfBoids()));


                   finalAcceleration.limitVector(-B.getMAX_FORCE(),B.getMAX_FORCE());

                    B.addAcceleration(finalAcceleration);
                    finalAcceleration.multiply(0);//set back to zero

                };



                border(B);

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

    private void updateNumOfBoids(Random r){



        if (variables.getNumOfBoids()>variables.getArrayOfBoids().size()){
            while (variables.getNumOfBoids()!=variables.getArrayOfBoids().size()) {

                int border=10;

                //add a new boid
                //KEEP IN MIND THIS GENERATES [0-99] INCLUSIVE
                float possibleX=r.nextInt((variables.getBoidFieldSize().width) - border);
                float possibleY=r.nextInt((variables.getBoidFieldSize().height) - border);

                if (possibleX<variables.getMAX_BOID_SIZE()){
                    possibleX=border;
                }

                if (possibleY<variables.getMAX_BOID_SIZE()){
                    possibleY=border;
                }




                variables.getArrayOfBoids().add(new Boid(new Vector3D(possibleX,possibleY,r.nextFloat(1000))));
            }
        } else if (variables.getNumOfBoids()<variables.getArrayOfBoids().size()) {
            while (variables.getNumOfBoids()!=variables.getArrayOfBoids().size()) {
                //add a new boid
                //KEEP IN MIND THIS GENERATES [0-99] INCLUSIVE
                //variables.setNumOfBoids(variables.getNumOfBoids()-1);
                variables.getArrayOfBoids().removeLast();
            }
        }


        if (variables.isRestarting()){
            variables.setNumOfBoids(20);
            variables.setRestarting(false);
        }

        //---------------------------------------------------------------------------------------------------------
    }




    public void border(Boid b){
        Vector3D position=b.getPosition();

        if (position.getX()<0){
            position.setX((float) variables.getBoidFieldSize().getWidth());

        }else if(position.getX()>variables.getBoidFieldSize().getWidth()){
            position.setX(0);
        }
        if (position.getY()<0){
            position.setY((float) variables.getBoidFieldSize().getHeight());

        }else if(position.getY()>variables.getBoidFieldSize().getHeight()){
            position.setY(0);
        }


        if (position.getZ()<0){
            position.setZ(1000);

        }else if(position.getZ()>1000){
            position.setZ(0);
        }




    }


    private Vector3D alignment(Boid parameterBoid, ArrayList<Boid> neighbourhood) {

        Vector3D position=parameterBoid.getPosition();
        Vector3D currentV=parameterBoid.getVelocity();

        Vector3D avgVelocity=new Vector3D(0,0,0);

        if (alignmentFactor==0){
            return avgVelocity;
        }

        int total=0;
        for (Boid b:neighbourhood) {
            float distance=position.distance(b.getPosition());
            if (distance<visualRange/2 && b!=parameterBoid){
                avgVelocity.add(b.getVelocity());
                total++;
            }
        }

        if (total > 0) {
            avgVelocity.divide(total);  // Average velocity of neighbors

            avgVelocity.sub(currentV);  // Adjust for current velocity

            avgVelocity.limitVector(-parameterBoid.getMAX_FORCE()*alignmentFactor,parameterBoid.getMAX_FORCE()*alignmentFactor);

        }


        //System.out.println("aligment thingy: "+avgVelocity.getX()+" "+avgVelocity.getY()+" "+avgVelocity.getY());


        return avgVelocity;
    }


    private Vector3D cohesion(Boid parameterBoid, ArrayList<Boid> neighbourhood) {


        // max visual range=300 pixels
        Vector3D currentP=parameterBoid.getPosition();
        Vector3D currentV=parameterBoid.getVelocity();

        Vector3D avgPosition=new Vector3D(0,0,0);

        if (cohesionFactor==0){
            return avgPosition;
        }

        int total=0;
        for (Boid b:neighbourhood) {
            if (currentP.distance(b.getPosition()) < visualRange*8 && b != parameterBoid) {
                avgPosition.add(b.getPosition());
                total++;
            }
        }


        if (total>0) {

            avgPosition.divide(total);  // Average position of neighbors
            avgPosition.sub(currentP);  // Direction to average position
            avgPosition.sub(currentV);  // Adjust for current velocity

            avgPosition.multiply(cohesionFactor);  // Scale by cohesion factor
            avgPosition.limitVector(-parameterBoid.getMAX_FORCE() * cohesionFactor, +parameterBoid.getMAX_FORCE() * cohesionFactor);

        }




        return avgPosition;

    }



    private Vector3D separation(Boid parameterBoid, ArrayList<Boid> neighbourhood) {


        // max visual range=300 pixels


        //immportant to make NOT MAKE A REFERENCE
        Vector3D currentP = new Vector3D(parameterBoid.getPosition().getX(),parameterBoid.getPosition().getY(),parameterBoid.getPosition().getZ());

        //read only so its fine
        Vector3D currentV=parameterBoid.getVelocity();

        Vector3D goAwayVector=new Vector3D(0,0,0);

        if (separationFactor==0){
            return goAwayVector;
        }

        int total=0;
        int count=0;
        for (Boid b:neighbourhood) {

            float distance = currentP.distance(b.getPosition());

            if (distance < visualRange*3 && b != parameterBoid) {

                //from other boids position substract my position
                Vector3D difference = currentP;
                difference.sub(b.getPosition());



                difference.divide(distance);
                //it is inversly proportional to distance


                goAwayVector.add(difference);
                total++;
            }
        }


        if (total>0) {

            goAwayVector.divide(total);  // Average position of neighbors
            //goAwayVector.multiply(parameterBoid.getSPEED()*2);
            goAwayVector.sub(currentV);  // Adjust for current velocity
            goAwayVector.limitVector((-parameterBoid.getMAX_FORCE())*separationFactor, (parameterBoid.getMAX_FORCE()* separationFactor));
        }


        return goAwayVector;

    }



}
