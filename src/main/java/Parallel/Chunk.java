package Parallel;

import Boids.Boid;
import SpatialHashing.Box;
import SpatialHashing.Grid;

import java.util.ArrayList;
import java.util.Arrays;

public class Chunk { // defined as a set of boxes
    Grid grid;
    Box[] boxArray;
    ArrayList<Box> boxesOfThisChunkArrayList=new ArrayList<>();
    Box[] boxesOfThisChunk;



    public Chunk(Grid grid, int startXIndex, int endXIndex) {
        this.grid=grid;
        this.boxArray= grid.getBoxArray();



        //take just the strip of x boxes
        //say 0 is the startIndex
        //say end index is 10
        //the first box will have index 0 0 0
        //and the last box will have 10 numberOfBoxesY numberOfBoxesZ

        //END INDEX IS NOT INCLUSIVE WATCH OUT
        for (int i = startXIndex; i < endXIndex; i++) {
            for (int j = 0; j < grid.numberOfBoxesY; j++) {
                for (int k = 0; k < grid.numberOfBoxesZ; k++) {
                    //Z major
                    //Y second
                    //X last

                    int index=i + j * grid.numberOfBoxesX + k * grid.numberOfBoxesX * grid.numberOfBoxesY;
                    boxesOfThisChunkArrayList.add(boxArray[index]);

                }
            }
        }
        boxesOfThisChunk = boxesOfThisChunkArrayList.toArray(new Box[0]);
    }

    public ArrayList<Boid> getBoidsInThisChunk(){
        //important note this will add the boids in order
        //in which ever order they are added is that order will the thread pick and update each one of them
        //to try to minimize the overlap in sections of 2 neighbours each tread will start from left
        //and do the collumn completely first (so Y and Z axis first and then shift to X)
        //this reduces contention somewhat

        ArrayList<Boid> returnArray=new ArrayList<>();

        for (Box box : boxesOfThisChunk) {
            returnArray.addAll(box.getBoidsInThisBox());
        }

        return returnArray;
    }

    public Box[] getBoxesOfThisChunk() {
        return boxesOfThisChunk;
    }
}
