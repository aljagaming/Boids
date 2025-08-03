package SpatialHashing;

import Boids.BoidMPI;

import java.util.ArrayList;

public class BoxMPI {
    int generaLboxId;
    int[] surroundingBoxesIdsARRAY;
    ArrayList<BoidMPI> boidsInThisBox;
    ArrayList<BoidMPI> neighbours;
    int firstGeneralBoxId;
    int lastGeneralBoxId;


    public BoxMPI(int[] boxData) {

        this.generaLboxId = boxData[0];
        boidsInThisBox = new ArrayList<>();
        neighbours = new ArrayList<>();


        this.surroundingBoxesIdsARRAY=new int[boxData.length-1];
        //remove the 1st entry that is the id of the current box
        for (int i = 1; i <surroundingBoxesIdsARRAY.length ; i++) {
            surroundingBoxesIdsARRAY[i-1]=boxData[i];
        }

    }

    public ArrayList<BoidMPI> getBoidsInThisBox() {
        return boidsInThisBox;
    }

    public void addBoidToBox(BoidMPI b){
        boidsInThisBox.add(b);
    }

    public void removeAll(){
        boidsInThisBox.clear();
    }

    public int[] getSurroundingBoxesIDs() {
        return surroundingBoxesIdsARRAY;
    }

    public int getFirstGeneralBoxId() {
        return firstGeneralBoxId;
    }

    public void setFirstGeneralBoxId(int firstGeneralBoxId) {
        this.firstGeneralBoxId = firstGeneralBoxId;
    }

    public int getLastGeneralBoxId() {
        return lastGeneralBoxId;
    }

    public void setLastGeneralBoxId(int lastGeneralBoxId) {
        this.lastGeneralBoxId = lastGeneralBoxId;
    }

    public int getGeneraLboxId() {
        return generaLboxId;
    }
}
