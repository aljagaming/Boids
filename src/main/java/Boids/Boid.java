package Boids;

import SpatialHashing.Box;
import SpatialHashing.Grid;

import java.util.ArrayList;
import java.util.Random;


public class Boid {
    private Vector3D position;
    private Vector3D velocity;//speed
    private Vector3D acceleration;
    private float course; //represented in radians from -PI -  +PI
    private float SPEED =0.5f; //actually speed is always set to speed limit so its more of just SPEED
    private final float TURN_RADIUS=200;//was 500
    public int[][] traceArray;
    public int traceLength=20;
    public int counter=0;
    public int oldestPos=traceLength-1;
    private float MAX_FORCE= SPEED/TURN_RADIUS; //has to be much much smaller than speed 0.001F

    //1 maxForce= 1/200=0.002   /4=0.0005 max force is greater when speed is greater
    //0.1 maxForce = 0.1/200=.0005  /4=0.0000125 max force is smaller when the speed is smaller
    private float deltaSpeed=0;
    private float lastSpeed=SPEED;
    private Random r=new Random();

    private ArrayList<Boid> neighbours = new ArrayList<Boid>();


    private Box pastBox;
    private Grid grid;
    private Box[] boxArray;

    private Box box;



    public Boid(Vector3D position, Grid grid) {
        this.position = position;
        this.grid=grid;
        this.boxArray=grid.getBoxArray();
        this.box= whichBox();


        //random number schanangance r.nextFloat ---> 0.0-1.0  and we need [-SPEED_LIMIT,+SPEED_LIMIT] so :
        this.velocity=new Vector3D(r.nextFloat()*2*SPEED-SPEED,r.nextFloat()*2*SPEED-SPEED,r.nextFloat()*2*SPEED-SPEED); // now at each direction max 20

        velocity.setMagnitude(SPEED); //so they can go at max Speed times speed

        // we need an angle so we would know where our boid points to X/Y wise
        // but we know the length of the X vector and length of Y vector
        // so we need an angle (alpha) such that cos(alpha)=X and sin(alpha)=Y
        // or in other words we need an angle that returns the sin(alpha)/cos(alpha) = X/Y  <---- NOOOOO Y/X
        // or tang(alpha)=Y/X
        // arctang(Y/X)= alpha!
        //Math.atan(velocity.getX()/velocity.getY()); doesnt work because x y can be negative
        //ALSO IT HAS TO BE Y/X (STUPID) because tan= sin/cos and not the other way around!!!!!
        
        course= (float) Math.atan2(velocity.getY(),velocity.getX());// this however needs to be updated each time velocity is updated
        acceleration = new Vector3D(0,0,0);
    }

    public void move(float speed){

        if (speed==0) return;
        speed=speed/100;

        if (this.SPEED!=speed) {
            this.SPEED = speed;
            this.velocity.setMagnitude(SPEED); //if velocity changed change how big the vector can be
            this.MAX_FORCE = SPEED / TURN_RADIUS; //adjuct max forece as well
        }


        velocity.add(acceleration);
        velocity.limitVector(-SPEED,SPEED);
        //float thingy=Math.abs(velocity.getX())+Math.abs(velocity.getY())+Math.abs(velocity.getZ());

        course= (float) Math.atan2(velocity.getY(),velocity.getX());
        position.add(velocity);
        putInABox();
        acceleration.multiply(0);


    }



    public void addAcceleration(Vector3D force) {
        acceleration.add(force);
        acceleration.limitVector(-getMAX_FORCE(),getMAX_FORCE());
    }



    public Vector3D getPosition() {
        return new Vector3D(position.getX(), position.getY(), position.getZ());
    }
    public Vector3D getVelocity() {
        return new Vector3D(velocity.getX(), velocity.getY(), velocity.getZ());
    }
    public float getCourse() {
        return course;
    }
    public float getSPEED() {
        return SPEED;
    }
    public float getMAX_FORCE() {
        return MAX_FORCE;
    }
    public float getTURN_RADIUS() {
        return TURN_RADIUS;
    }



    public ArrayList<Boid> getNeighborhood(){
        //this is A LOT OF COMPUTATION!!!
        //merges all the boids from this box and surrounding boxes (all the ones that can influence this boid)
        //rational is that each of the boxes should contain not that many boids
        //so its still faster then checking whole array always
        //however it benefits are mitigated if there is a lot of boids in one place
        //for that a tree should be used - solution that is not going to be explored




        neighbours.clear();
        neighbours.addAll(box.getBoidsInThisBox()); //add all of the boids of this Box


        //there was a problem here that was resolved
        //in parallel execution .getBoidsInThis Box can be runned and then .removeBoidFromBox and then return neighbourhood
        //creating null pointer

        ArrayList<Integer> idOfSurroundingBoxes = box.getSurroundingBoxesIds();



        //this now adds ALL of the surrounding boxes
        //maybe better if each box is devided in quadrants then less boxes to check

        for (Integer i : idOfSurroundingBoxes) {
            neighbours.addAll( boxArray[i].getBoidsInThisBox() );
        }

        return neighbours;
    }




    public synchronized void putInABox(){

        Box boxToBePlacedAt= whichBox();

        if(pastBox!=boxToBePlacedAt) {

            if (pastBox!=null) {
                pastBox.removeBoidToBox(this);
            }

            boxToBePlacedAt.addBoidToBox(this);

            this.box=boxToBePlacedAt;

            pastBox=boxToBePlacedAt;
        }
    }

    private Box whichBox(){
        int x = (int) Math.floor(position.getX() / grid.boxSize)-1; //this -1 is since box POSITION go from 0 - (numberBoxesX-1)
        int y = (int) Math.floor(position.getY() / grid.boxSize)-1; // so for example there is 20 boxes in Depth so if boid has position 970 - it will get put in 20 which would be out of bounds
        int z = (int) Math.floor(position.getZ() / grid.boxSize)-1;


        //since border is still not perfect (i.e. sometimes it happens that the boids clip to -1)
        //and because the above -1
        x=Math.max(x,0);
        y=Math.max(y,0);
        z=Math.max(z,0);

        int index =x + y * grid.numberOfBoxesX + z * (grid.numberOfBoxesY * grid.numberOfBoxesX);


        return boxArray[index];
    }

    public Box getBox() {
        return box;
    }

    public void removeMeFromMyBox() {
        box.removeBoidToBox(this);
    }
}
