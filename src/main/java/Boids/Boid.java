package Boids;

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




    public Boid(Vector3D position) {
        this.position = position;

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
}
