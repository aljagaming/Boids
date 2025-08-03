package Distributed;

import SpatialHashing.BoxMPI;

import java.util.HashMap;

public class WorkersGrid {

    public BoxMPI[] myBoxes = null;
    public HashMap<Integer, Integer> generalIndexToLocal = new HashMap<>();

    public WorkersGrid(BoxMPI[] myBoxes, HashMap<Integer, Integer> generalIndexToLocal) {
        this.myBoxes = myBoxes;
        this.generalIndexToLocal = generalIndexToLocal;
    }
}
