package SpatialHashing;

import Executive.Variables;

import java.util.ArrayList;



//grid needs to be made on the beginning as then the screen is the biggest
//and when user goes full screen


public class Grid {
    Variables variables;
    public final int boxSize=50;    //max visual range is 100 so we will use 50
    public final int numberOfBoxesX; // So that 154/50 = 4 and not 3
    public final int numberOfBoxesY;
    public final int numberOfBoxesZ; //can be private as they are read only and we need to know them because of hashing function

    private Box[] boxArray;


    public Grid(Variables variables) {
        this.variables = variables;

        //this could further be more optimized if boxSize dynamically changed in respect to visual range
        int width=variables.getBoidFieldSize().width;
        int height=variables.getBoidFieldSize().height;
        int depth=variables.getDEPTH();



        //it is probalby smart to set the number of boxes to max screen size so that we dont have to add more boxes so we create grid only once
        //when resizing

        //THIS IS THE ONLY PLACE WHERE THE number of boxes is assigned variables are asigned

        //specially since if we add more boxes boxGrid.elementAt(x*numberOfBoxesX+y*numberOfBoxesY+z*numberOFBoxesZ+1) wont work
        numberOfBoxesX=(int)Math.ceil((double) width/boxSize); // So that 154/50 = 4 and not 3
        numberOfBoxesY=(int)Math.ceil((double) height/boxSize);
        numberOfBoxesZ=depth/boxSize;



        //this can also be an array this would gain a bit of efficiency but for the sake of code complexity we will leave it at ArrayList
        //boxArray=new ArrayList<Box>(numberOfBoxesX*numberOfBoxesY*numberOfBoxesZ); //3*3*2=18 --


        createGrid();
    }



    public void createGrid(){
        boxArray = new Box[numberOfBoxesX * numberOfBoxesY * numberOfBoxesZ];


        //part of hash function
        //generally a given box (with x,y,z) can be found at boxGrit.elementAt(x*numberOfBoxesX+y*numberOfBoxesY+z*numberOFBoxesZ+1)

        int index = 0;
        for (int z = 0; z < numberOfBoxesZ; z++) { //then go into depth with Z
            for (int y = 0; y < numberOfBoxesY; y++) { //then Y
                for (int x = 0; x < numberOfBoxesX; x++) { //first fill the X axis

                    //we need to access boxes in O(1) from this array kind of a hash function
                    //boxArray.add(new Box(new int[]{x,y,z},numberOfBoxesX,numberOfBoxesY,numberOfBoxesZ));

                    boxArray[index] = new Box(new int[]{x, y, z}, numberOfBoxesX, numberOfBoxesY, numberOfBoxesZ,index);
                    index++;
                    //note that here numberOfBoxes goes up to (numberOfBoxes-1)

                }
            }
        }
        System.out.println(index);
    }


    //a function that when you give it a box it returns an array of all the objects in itself
    public Box[] getBoxArray() {
        return boxArray;
    }
}
