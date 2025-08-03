package Functions;

import Boids.BoidMPI;
import Boids.Vector3D;
import Distributed.DistributedVariables;
import mpi.MPI;

import java.util.ArrayList;
import java.util.Vector;

public class FunctionsMPI {
    public static Vector3D theAllMightyFunctionMPI(BoidMPI B, DistributedVariables distributedVariables){


        ArrayList<BoidMPI> neighbourhood=B.getNeighborhood();



        Vector3D v= new Vector3D(0,0,0);

        if (distributedVariables.getVisualRange()!=0 ) {

            if (distributedVariables.getCoherence() != 0) {
                Vector3D cohesionVector = cohesionMPI(B, neighbourhood, distributedVariables.getVisualRange());
                cohesionVector.multiply((float) distributedVariables.getCoherence() / 100);//  /100

                //System.out.println("Cohesion vector rn: "+ cohesionVector.getX()+" "+cohesionVector.getY()+" "+cohesionVector.getZ());

                v.add(cohesionVector);


            }





            if (distributedVariables.getSeparation() != 0) {
                Vector3D separationVector = separationMPI(B, neighbourhood, distributedVariables.getVisualRange());
                separationVector.multiply(distributedVariables.getSeparation()*100); //*100

                //System.out.println("Cohesion vector rn: "+ separationVector.getX()+" "+separationVector.getY()+" "+separationVector.getZ());


                v.add(separationVector);


            }



            if (distributedVariables.getAlignment() != 0) {
                Vector3D alignmentVector = alignmentMPI(B, neighbourhood, distributedVariables.getVisualRange());
                alignmentVector.multiply(distributedVariables.getAlignment() * 50); // *50

                //System.out.println("Aligment vector rn: "+ alignmentVector.getX()+" "+alignmentVector.getY()+" "+alignmentVector.getZ());



                v.add(alignmentVector);
            }

            //System.out.println("Total before limiting rn: "+ v.getX()+" "+v.getY()+" "+v.getZ());



            v.limitVector(-B.getMAX_FORCE(), B.getMAX_FORCE());
            //System.out.println("After limiting rn: "+ v.getX()+" "+v.getY()+" "+v.getZ());

        }



        //why does addding it precisely 3 more times make it perfect for
        Vector3D borderVector= borderMPI(B,distributedVariables.getBoidFieldWidth(),distributedVariables.getBoidFieldHeight(),distributedVariables.getBoidFieldDepth());


        v.add(borderVector);
        v.add(borderVector);
        v.add(borderVector);
        //System.out.println("After border rn: "+ v.getX()+" "+v.getY()+" "+v.getZ());
        //System.out.println( " Return Vector from function: "+ v.getX()+" "+ v.getY()+" "+ v.getZ()+" ");


        return v;
    }


    //Cohesion
    //------------------------------------------------------------------------------------------------------------------------
    private static Vector3D cohesionMPI(BoidMPI parameterBoid, ArrayList<BoidMPI> neighbourhood, int visualRange) {



        // max visual range=300 pixels
        Vector3D currentP=parameterBoid.getPosition();
        Vector3D currentV=parameterBoid.getVelocity();

        Vector3D avgPosition=new Vector3D(0,0,0);

        int total=0;
        for (BoidMPI b:neighbourhood) {



            if (b==null){

                continue;
            }

            if (currentP.distance(b.getPosition()) < visualRange && b != parameterBoid) {
                avgPosition.add(b.getPosition());
                total++;
            }
        }

        if (total>0) {

            avgPosition.divide(total);  // Average position of neighbors
            avgPosition.sub(currentP);  // Direction to average position
            avgPosition.sub(currentV);  // Adjust for current velocity

            //avgPosition.multiply(factor);  // Scale by cohesion factor
            //avgPosition.limitVector(-parameterBoid.getMAX_FORCE(), parameterBoid.getMAX_FORCE());

        }

        return avgPosition;
    }



    //Separation
    //------------------------------------------------------------------------------------------------------------------------
    private static Vector3D separationMPI(BoidMPI parameterBoid, ArrayList<BoidMPI> neighbourhood,int visualRange) {



        int min=20;
        Vector3D currentP=parameterBoid.getPosition();//---------------------------------------------
        Vector3D currentV=parameterBoid.getVelocity();





        Vector3D goAwayVector=new Vector3D(0,0,0);

        int total=0;
        for (BoidMPI b:neighbourhood) {
            if (b==null){

                continue;
            }



            //DISTANCE IS ZERO FOR SOME REASON
            float distance = currentP.distance(b.getPosition());////---------------------------------------------


            //visual range was *3
            if (distance < Math.min(visualRange,min) && b != parameterBoid) {

                //from other boids position substract my position
                Vector3D difference = currentP.copy();//also be carefull not to change my position

                difference.sub(b.getPosition());
                difference.divide(distance);//it is inversly proportional to distance


                goAwayVector.add(difference);
                total++;
            }
        }

        if (total>0) {
            goAwayVector.divide(total);  // Average position of neighbors
            goAwayVector.sub(currentV);  // Adjust for current velocity
        }



        return goAwayVector;
    }






    //Alignment
    //------------------------------------------------------------------------------------------------------------------------
    private static Vector3D alignmentMPI(BoidMPI parameterBoid, ArrayList<BoidMPI> neighbourhood, int visualRange) {



        Vector3D position=parameterBoid.getPosition();
        Vector3D currentV=parameterBoid.getVelocity();
        Vector3D avgVelocity=new Vector3D(0,0,0);

        int total=0;
        for (BoidMPI b:neighbourhood) {
            if (b==null){

                continue;
            }
            float distance=position.distance(b.getPosition());

            //visual range * 2 here
            if (distance<visualRange && b!=parameterBoid){
                avgVelocity.add(b.getVelocity());
                total++;
            }
        }

        if (total > 0) {
            avgVelocity.divide(total);  // Average velocity of neighbors

            avgVelocity.sub(currentV);  // Adjust for current velocity

            avgVelocity.limitVector(-parameterBoid.getMAX_FORCE(),parameterBoid.getMAX_FORCE());

        }


        //System.out.println("aligment thingy: "+avgVelocity.getX()+" "+avgVelocity.getY()+" "+avgVelocity.getY());

        return avgVelocity;
    }



    public static Vector3D borderMPI(BoidMPI b, int screenWidth, int screenHeight, int screenDepth){




        Vector3D returnV=new Vector3D(0,0,0);

        float xPos=b.getPosition().getX();
        float yPos=b.getPosition().getY();
        float zPos=b.getPosition().getZ();
        float course=b.getCourse();

        float turnRadius=b.getTURN_RADIUS(); //200

        //BUT THEY ONLY EVER NEED TO DO AT MAX A HALF TURN FOR BOTH X and Y so its turn radius/4
        float softBound=turnRadius/4+(-50+b.getSPEED()*100);// want this to be 50 so we dont take to much space of the screen
        //the -50+b.getSPEED()*10 is fine tuning if the speed is max (1) we will add +50 - doubling the size of the boundary
        float maxForce=b.getMAX_FORCE();

        //For X
        if (xPos<softBound){//LEFT WALL
            if (course < Math.PI) {
                returnV.setX(+maxForce);
            } else {//Math.Pi-2*Math.Pi
                returnV.setX(-maxForce);
            }

        } else if (xPos>(screenWidth-softBound)) {//RIGHT WALL
            if (course < Math.PI) {//0-Math.PI
                returnV.setX(-maxForce);
            } else {//Math.Pi-2*Math.Pi
                returnV.setX(+maxForce);
            }
        }

        //For Y
        if (yPos<softBound){//CEILING
            if (course>Math.PI*3/2 && course<Math.PI/2) {
                returnV.setY(-maxForce);
            } else {//Math.Pi-2*Math.Pi
                returnV.setY(+maxForce);
            }
        } else if (yPos>screenHeight-softBound) {//FLOOR
            if (course>Math.PI*3/2 && course<Math.PI/2) {
                returnV.setY(+maxForce);
            } else {//Math.Pi-2*Mathi
                returnV.setY(-maxForce);
            }
        }

        //For Z
        if (zPos<1+softBound){
            returnV.setZ(+turnRadius);

        }else if(zPos>screenDepth-softBound){
            returnV.setZ(-turnRadius);
        }

        return returnV;

    }

}
