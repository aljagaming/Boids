package Functions;

import Boids.Boid;
import Boids.Vector3D;

import java.util.ArrayList;

public class Functions {


//REMEMBER THAT THE FACTOR IS INTEGER
    //Cohesion 8
    //Separation 3
    //Aligment 2

    public static Vector3D theAllMightyFunction(Boid B, ArrayList<Boid> neighbourhood,int vRange,int cohesionFactor, int separationFactor, int alignmentFactor){

        Vector3D v=new Vector3D(0,0,0);

        //System.out.println("cohision factor "+ cohesionFactor);
        //System.out.println("separation factor "+ separationFactor);
        //System.out.println("aligment factor "+ alignmentFactor);



        if (cohesionFactor!=0){
            Vector3D cohesionVector = cohesion(B, neighbourhood, vRange);
            cohesionVector.multiply((float) cohesionFactor /100);
            v.add(cohesionVector);
        }else{
            System.out.println("thing");
        }


        if (separationFactor!=0){
            Vector3D separationVector = separation(B, neighbourhood, vRange);
            separationVector.multiply(separationFactor);
            v.add(separationVector);
        }


        if (alignmentFactor!=0){
            Vector3D alignmentVector = alignment(B, neighbourhood, vRange);
            alignmentVector.multiply(alignmentFactor*2);
            v.add(alignmentVector);
        }


        v.limitVector(-B.getMAX_FORCE(),B.getMAX_FORCE());

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
}
