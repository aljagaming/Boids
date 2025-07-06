package SpatialHashing;

import Boids.Boid;

import java.util.ArrayList;

public class Box {
    int numberOfBoxesX;
    int numberOfBoxesY;
    int numberOfBoxesZ;



    public int[] boxIndex;// store the x,y,z position of box in grid
    //int[] boxPosition;
    ArrayList<Boid> boidsInThisBox= new ArrayList<>();; //boids currently in this box

    ArrayList<Integer> surroundingBoxesIds=new ArrayList<>();
    //IF I GIVE YOU BOX INDEX YOU CAN FIND ARRAY OF ALL of its surrounding Boxes

    //for box at [1,1,1]:
    //
    //its surrounding will be [[0,0,0],[1,0,0],[2,0,0], [0,1,0],[1,1,0],[2,1,0], [0,2,0],[1,2,0],[2,2,0], //front face (z=0)
    //                         [0,0,2],[1,0,2],[2,0,2], [0,1,2],[1,1,2],[2,1,2], [0,2,2],[1,2,2],[2,2,2], //back face  (z=2)
    //                         [0,0,1],[1,0,1],[2,0,1], [0,1,1],[1,1,1],[2,1,1], [0,2,1],[1,2,1],[2,2,1]] //middle face (z=1)
    //                     execept this one is already included----^
    // so total 3*9=27-1=26
    //but its different for corner cases however

    //Generally



    public Box(int[] boxIndex,int numberOfBoxesX,int numberOfBoxesY,int numberOfBoxesZ) {
        this.boxIndex = boxIndex;
        int x=boxIndex[0];
        int y=boxIndex[1];
        int z=boxIndex[2];
        this.numberOfBoxesX=numberOfBoxesX;
        this.numberOfBoxesY=numberOfBoxesY;
        this.numberOfBoxesZ=numberOfBoxesZ;
        //this.boxPosition=new int[] {x*50,y*50,z*50};


        /*
        System.out.println("BOX------------------------------------------------------------------------");
        System.out.println("This boxes pos: x= "+x+ " y= "+y+" z= "+z);
        System.out.println("This box index should be: "+(x+y*numberOfBoxesX+z*numberOfBoxesX*numberOfBoxesY));
        System.out.println("Surouding boxes pos:");

         */


        //this gives indexes in the grid array of all the boxes that are surrounding this one in space

        int counter=0;
        for (int i = -1; i <=1; i++) {
            for (int j = -1; j <=1; j++) {
                for (int k = -1; k<=1; k++) {

                    //First adds back face of 9 boxes -starts at -1 y -1 z in respect to the observed box
                    //Then adds middle face of 8 boxes
                    //Then adds front face

                    int newX = x + k;
                    int newY = y + j;
                    int newZ = z + i;

                    if (newX == x && newY == y && newZ == z) {
                        continue;
                        //dont get this.box in
                    }

                    // Skip unreachable boxes note that newX >= numberOfBoxesX bcs array so its 0-(numberOfBoxesX-1)
                    if (newX < 0 || newY < 0 || newZ < 0 || newX >= numberOfBoxesX || newY >= numberOfBoxesY || newZ >= numberOfBoxesZ) {
                        continue;
                    }

                    //but then each of these should be transfered to an actual position in array of all the boxes positi
                    //surroundingBoxesIds.add(new int[]{newX, newY, newZ});

                    /*
                    System.out.println("Surrounding box "+ counter +" : x= "+newX+ " y= "+newY+" z= "+newZ);
                    System.out.println("Surrounding box "+ counter +" id: "+(newX+newY*numberOfBoxesX+newZ*numberOfBoxesX*numberOfBoxesY));
                     */

                    //add the actual number of those boxes in the surrounding (grid) array
                    surroundingBoxesIds.add(newX+newY*numberOfBoxesX+newZ*numberOfBoxesX*numberOfBoxesY);

                    //newX+newY*(numberOfBoxesX-1)+newZ*(numberOfBoxesX-1)*(numberOfBoxesY-1)
                    counter++;

                }
            }
        }
    }


    public void clearBox() {
        boidsInThisBox.clear();
    }
    public synchronized void addBoidToBox(Boid b){
        boidsInThisBox.add(b);
    }

    public synchronized void removeBoidToBox(Boid b){
        boidsInThisBox.remove(b);
    }

    public ArrayList<Boid> getBoidsInThisBox() {
        return boidsInThisBox;
    }

    public ArrayList<Integer> getSurroundingBoxesIds() {
        return surroundingBoxesIds;
    }

}
