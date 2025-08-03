package Distributed;

import java.util.ArrayList;

public class DistributedVariables {
    //------------

    //----------

    int boidFieldWidth;
    int boidFieldHeight;
    int boidFieldDepth;

    int visualRange;
    int coherence;
    int separation;
    int alignment;







    public DistributedVariables(int visualRange,int coherence, int separation, int alignment, int boidFieldWidth, int boidFieldHeight, int boidFieldDepth) {
        this.visualRange = visualRange;
        this.coherence = coherence;
        this.separation = separation;
        this.alignment = alignment;

        this.boidFieldWidth = boidFieldWidth;
        this.boidFieldHeight = boidFieldHeight;
        this.boidFieldDepth = boidFieldDepth;
    }


    public int getBoidFieldWidth() {
        return boidFieldWidth;
    }

    public int getBoidFieldHeight() {
        return boidFieldHeight;
    }

    public int getBoidFieldDepth() {
        return boidFieldDepth;
    }

    public int getVisualRange() {
        return visualRange;
    }

    public int getCoherence() {
        return coherence;
    }

    public int getSeparation() {
        return separation;
    }

    public int getAlignment() {
        return alignment;
    }
}