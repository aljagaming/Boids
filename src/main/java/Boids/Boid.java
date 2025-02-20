package Boids;

import java.util.Random;
import java.util.Vector;


public class Boid {
    private Vector3D position;
    private Vector3D velocity;//speed
    private Vector3D acceleration;
    private float course; //represented in radians from -PI -  +PI
    private final float SPEED =0.5F; //actually speed is always set to speed limit so its more of just SPEED
    private final float MAX_FORCE= 0.001F; //has to be much much smaller than speed
    private float deltaSpeed=0;
    private float lastSpeed=SPEED;
    private Random r=new Random();




    public Boid(Vector3D position) {
        this.position = position;

        //random number schanangance r.nextFloat ---> 0.0-1.0  and we need [-SPEED_LIMIT,+SPEED_LIMIT] so :
        this.velocity=new Vector3D(r.nextFloat()*2*SPEED-SPEED,r.nextFloat()*2*SPEED-SPEED,r.nextFloat()*2*SPEED-SPEED); // now at each direction max 20

        velocity.setMagnitude(3*SPEED); //so they can go at max 3 times speed


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

    public void move(float deltaTime){
        //for parallel execution it will probably be imperative that only one thread accesses on boid


        /*
        if (deltaTime==0){
            return;
        }
        deltaSpeed=(deltaTime/100)*SPEED;


        velocity.add(acceleration);
        velocity.setX(Math.max(-lastSpeed, Math.min(lastSpeed, velocity.getX())));
        velocity.setY(Math.max(-lastSpeed, Math.min(lastSpeed, velocity.getY())));
        velocity.setZ(Math.max(-lastSpeed, Math.min(lastSpeed, velocity.getZ())));



        if (deltaSpeed<lastSpeed){
            //only change speed when its differnet from the last speed
            //velocity.normalizeBySpeed(lastSpeed);
            velocity.multiply(deltaSpeed);
            lastSpeed=deltaSpeed;

        } else if (deltaSpeed >lastSpeed){

            velocity.normalize();
            velocity.multiply(deltaSpeed);

            lastSpeed=deltaSpeed;
        }


         */

        //velocity.add(new Vector3D(0.0002F,0.0002F,0.0002F)); // so they cant quite ever stop


        velocity.add(acceleration);
        velocity.limitVector(-SPEED,SPEED);
        float thingy=Math.abs(velocity.getX())+Math.abs(velocity.getY())+Math.abs(velocity.getZ());

        //System.out.println("Total velocity "+thingy);


        course= (float) Math.atan2(velocity.getY(),velocity.getX());
        position.add(velocity);
        acceleration.multiply(0);


    }



    public void addAcceleration(Vector3D force) {


        acceleration.add(force);

        /*
        //System.out.println("Force to be added: "+"   "+acceleration.getX()+"     "+acceleration.getY()+"     "+acceleration.getZ());
        if (acceleration.magnitude() > MAX_FORCE) {
            acceleration.normalizeBySpeed(MAX_FORCE);
        }

         */

    }






    public Vector3D getPosition() {
        return position;
    }

    public Vector3D getVelocity() {
        return velocity;
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
}
