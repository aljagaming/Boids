package Functions;

import Boids.Boid;
import Boids.Vector3D;
import Executive.Variables;

import java.util.ArrayList;

public class Functions {


//REMEMBER THAT THE FACTOR IS INTEGER
    //Cohesion 8
    //Separation 3
    //Aligment 2

    public static Vector3D theAllMightyFunction(Boid B, Variables variables){

        ArrayList<Boid> neighbourhood=variables.getArrayOfBoids();
        Vector3D v=new Vector3D(0,0,0);

        if (variables.getVisualRange()!=0) {

            if (variables.getCoherence() != 0) {
                Vector3D cohesionVector = cohesion(B, neighbourhood, variables.getVisualRange());
                cohesionVector.multiply((float) variables.getCoherence() / 100);
                v.add(cohesionVector);
            }


            if (variables.getSeparation() != 0) {
                Vector3D separationVector = separation(B, neighbourhood, variables.getVisualRange());
                separationVector.multiply(variables.getSeparation());
                v.add(separationVector);
            }


            if (variables.getAlignment() != 0) {
                Vector3D alignmentVector = alignment(B, neighbourhood, variables.getVisualRange());
                alignmentVector.multiply(variables.getAlignment() * 2);
                v.add(alignmentVector);
            }

            v.limitVector(-B.getMAX_FORCE(), B.getMAX_FORCE());
        }



        //why does addding it precisely 3 more times make it perfect for
        v.add(border(B,variables.getBoidFieldSize().width,variables.getBoidFieldSize().height,variables.getDEPTH()));
        v.add(border(B,variables.getBoidFieldSize().width,variables.getBoidFieldSize().height,variables.getDEPTH()));
        v.add(border(B,variables.getBoidFieldSize().width,variables.getBoidFieldSize().height,variables.getDEPTH()));

        //v.limitVector(-B.getMAX_FORCE(), B.getMAX_FORCE()); ???

        return v;
    }



    //Cohesion
    //------------------------------------------------------------------------------------------------------------------------
    private static Vector3D cohesion(Boid parameterBoid, ArrayList<Boid> neighbourhood, int visualRange) {


        // max visual range=300 pixels
        Vector3D currentP=parameterBoid.getPosition();
        Vector3D currentV=parameterBoid.getVelocity();

        Vector3D avgPosition=new Vector3D(0,0,0);
        int total=0;
        for (Boid b:neighbourhood) {
            //visual range is *8 here
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
    private static Vector3D separation(Boid parameterBoid, ArrayList<Boid> neighbourhood,int visualRange) {


        int min=20;
        Vector3D currentP=parameterBoid.getPosition();
        Vector3D currentV=parameterBoid.getVelocity();


        Vector3D goAwayVector=new Vector3D(0,0,0);

        int total=0;
        for (Boid b:neighbourhood) {

            float distance = currentP.distance(b.getPosition());

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
    private static Vector3D alignment(Boid parameterBoid, ArrayList<Boid> neighbourhood, int visualRange) {

        Vector3D position=parameterBoid.getPosition();
        Vector3D currentV=parameterBoid.getVelocity();
        Vector3D avgVelocity=new Vector3D(0,0,0);

        int total=0;
        for (Boid b:neighbourhood) {
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



    public static Vector3D border(Boid b,int screenWidth,int screenHeight, int screenDepth){
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
            } else {//Math.Pi-2*Math.Pi
                returnV.setY(-maxForce);
            }
        }

        //For Z
        if (zPos<0+softBound){
            returnV.setZ(+turnRadius);

        }else if(zPos>screenDepth-softBound){
            returnV.setZ(-turnRadius);
        }

        return returnV;
    }



}
