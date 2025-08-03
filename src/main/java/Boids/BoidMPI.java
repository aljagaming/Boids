package Boids;

import SpatialHashing.BoxMPI;

import java.util.ArrayList;
import java.util.HashMap;

public class BoidMPI {

    Vector3D position;
    Vector3D velocity;
    BoxMPI myBox;
    ArrayList<BoidMPI> neighbours;
    BoxMPI[] allBoxes;
    int firstGeneralBoxId;
    int lastGeneralBoxId;


     float course; //represented in radians from -PI -  +PI
     float SPEED; //actually speed is always set to speed limit so its more of just SPEED
     float TURN_RADIUS;
     float MAX_FORCE;
     HashMap<Integer,Integer> generalIndexToLocal;

    public BoidMPI(Vector3D position,
                   Vector3D velocity,
                   BoxMPI myBox,
                   BoxMPI[] allBoxes,
                   HashMap generalIndexToLocal,
                   float SPEED,
                   float TURN_RADIUS,
                   float MAX_FORCE,
                   float course) {


        this.position = position;
        this.velocity = velocity;
        this.myBox=myBox;
        this.allBoxes =allBoxes;
        neighbours=new ArrayList<>();

        this.generalIndexToLocal=generalIndexToLocal;
        this.course=course;

        this.SPEED=SPEED; //actually speed is always set to speed limit so its more of just SPEED
        this.TURN_RADIUS=TURN_RADIUS;
        this.MAX_FORCE=MAX_FORCE;

    }

    public Vector3D getPosition() {
        return new Vector3D(position.getX(), position.getY(), position.getZ());
    }

    public Vector3D getVelocity() {
        return new Vector3D(velocity.getX(), velocity.getY(), velocity.getZ());
    }



    public ArrayList<BoidMPI> getNeighborhood(){


        neighbours.clear();
        neighbours.addAll(myBox.getBoidsInThisBox()); //add all of the boids of this Box
        neighbours.remove(this);

        int[] idOfSurroundingBoxes = myBox.getSurroundingBoxesIDs();


        Integer localIndex;
        for (int i : idOfSurroundingBoxes) {

            localIndex=generalIndexToLocal.get(i);

            if (localIndex==null){
                //System.out.println("Hallo problem");
            }else{
                //System.out.println("Hit");
                neighbours.addAll(allBoxes[localIndex].getBoidsInThisBox());
            }
        }

        return neighbours;
    }

    public float getCourse() {
        return course;
    }

    public float getSPEED() {
        return SPEED;
    }

    public float getTURN_RADIUS() {
        return TURN_RADIUS;
    }

    public float getMAX_FORCE() {
        return MAX_FORCE;
    }
}
