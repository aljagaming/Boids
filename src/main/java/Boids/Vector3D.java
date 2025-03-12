package Boids;

public class Vector3D {
    float x;
    float y;
    float z;
    float magnitude= (float) Math.sqrt(x*x+y*y+z*z);

    public Vector3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }


    public void setAll(Vector3D v){
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public void add(Vector3D v) {
        this.x=this.x + v.x;
        this.y=this.y + v.y;
        this.z=this.z + v.z;
    }
    public void sub(Vector3D v){
        this.x=this.x - v.x;
        this.y=this.y - v.y;
        this.z=this.z - v.z;
    }

    public void multiply(float multiplier){
        this.x=this.x*multiplier;
        this.y=this.y*multiplier;
        this.z=this.z*multiplier;
    }

    public void multiplyByVector(Vector3D v){
        this.x=this.x*v.getX();
        this.y=this.y*v.getY();
        this.z=this.z*v.getZ();
    }



    public void divide(float divisor){
        this.x=this.x/divisor;
        this.y=this.y/divisor;
        this.z=this.z/divisor;
    }

    public float calculateMagnitude(){
        return  (float) Math.sqrt(x*x+y*y+z*z);
    }

    public void setMagnitude(float magnitude) {
        this.magnitude = magnitude;
    }

    public float getMagnitude() {
        return magnitude;
    }

    public void normalize(){
        // makes sure that each of the components of Vector is in the range 0-1
        // this way we can multiply (scale) some other vector with this
        // we doo this by deviding each component of the vector by the vectors length


        if (magnitude > 0) { // Avoid division by zero
            x = (x /magnitude); // Scale X
            y = (y /magnitude); // Scale Y
            z = (z /magnitude); // Scale Z
        }

    }
    public void limitVector(float min, float max) {
        this.x = limit(this.x, min, max);
        this.y = limit(this.y, min, max);
        this.z = limit(this.z, min, max);
    }

    // Helper method to limit a single value between min and max
    private float limit(float value, float min, float max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }
    public float distance (Vector3D positionOfOtherBoid){

        float distanceX=this.x-positionOfOtherBoid.getX();
        float distanceY=this.y-positionOfOtherBoid.getY();
        float distanceZ=this.z-positionOfOtherBoid.getZ();

        return (float) Math.sqrt(distanceX*distanceX+distanceY*distanceY+distanceZ*distanceZ);
    }

    public Vector3D copy(){
        return new Vector3D(this.getX(),this.getY(), this.getZ());
    }





}
