package Executive;

import Boids.Boid;
import Distributed.DistributedExe;
import Gui.Gui;
import Parallel.ParallelExe;
import Sequential.SequentialExe;
import SpatialHashing.Grid;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

public class Variables {

    String[] args;

    private ArrayList <Boid> arrayOfBoids=new ArrayList<>(0);
    private int numOfBoids=20;
    private Dimension boidFieldSize;
    private final int DEPTH=1000;
    private final int MAX_BOID_SIZE=20;
    private final int MIN_BOID_SIZE=10;

    private Gui myGui=new Gui(this);

    private Grid boxGrid=new Grid(this);

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------

    private ExecutionStyle executionStyleName;
    private ExecutionInterface currentExe=new SequentialExe(this);

    //----------------------------------------------------------------------------------------
    //
    private CyclicBarrier barrier=new CyclicBarrier(2);//needed because while loop tries to calculate stuff before it is drawn
    private boolean Restarting=false;

    //----------------------------------------------------------------------------------------
    //Mandatory:
    private int coherence=50;
    private int separation=50;
    private int alignment=50;
    //----------------------------------------------------------------------------------------
    //optional:
    private int visualRange=50;
    private int animationSpeed=50;
    private boolean tracePaths=false;
    //----------------------------------------------------------------------------------------


    public Variables(String[] args) {
        this.args = args;
    }

    //----------------------------------------------------------------------------------------

    public int getNumOfBoids() {
        return numOfBoids;
    }

    public void setNumOfBoids(int numOfBoids) {
        this.numOfBoids = numOfBoids;
    }

    public Dimension getBoidFieldSize() {
        return boidFieldSize;
    }
    public int getDEPTH() {
        return DEPTH;
    }

    public void setBoidFieldSize(Dimension boidFieldSize) {
        this.boidFieldSize = boidFieldSize;
    }

    public int getCoherence() {
        return coherence;
    }

    public void setCoherence(int coherence) {
        this.coherence = coherence;
    }

    public int getSeparation() {
        return separation;
    }

    public void setSeparation(int separation) {
        this.separation = separation;
    }

    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public int getVisualRange() {
        return visualRange;
    }

    public void setVisualRange(int visualRange) {
        this.visualRange = visualRange;
    }

    public int getAnimationSpeed() {
        return animationSpeed;
    }

    public void setAnimationSpeed(int animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    public boolean getTracePaths() {
        return tracePaths;
    }

    public void setTracePaths(boolean tracePaths) {
        this.tracePaths = tracePaths;
    }

    public ArrayList<Boid> getArrayOfBoids() {
        return arrayOfBoids;
    }

    public void setArrayOfBoids(ArrayList<Boid> arrayOfBoids) {
        this.arrayOfBoids = arrayOfBoids;
    }

    public int getMAX_BOID_SIZE() {
        return MAX_BOID_SIZE;
    }

    public int getMIN_BOID_SIZE() {
        return MIN_BOID_SIZE;
    }

    public boolean isRestarting() {
        return Restarting;
    }

    public void setRestarting(boolean restarting) {
        Restarting = restarting;
    }

    public CyclicBarrier getBarrier() {
        return barrier;
    }

    public Grid getBoxGrid() {
        return boxGrid;
    }

    public ExecutionInterface getCurrentExe() {
        return currentExe;
    }






    public void updateExecutionStyle(ExecutionStyle newExecutionStyle){

        if (executionStyleName==null){
            executionStyleName=ExecutionStyle.SEQUENTIAL;
            currentExe=new SequentialExe(this);
            currentExe.start(args);
            return;
        }



        if (newExecutionStyle != executionStyleName){ //if its not the same as currently running one

            executionStyleName = newExecutionStyle;

            if (currentExe!=null){

                currentExe.stop();

            }else{
                //executionStyleName
                return;
            }


            switch (executionStyleName){
                case SEQUENTIAL:
                    currentExe=new SequentialExe(this);
                    //currentExe.start();

                    //Call sequential execution

                    break;
                case PARALLEL:
                    currentExe=new ParallelExe(this);
                    //currentExe.start();
                    //Call parallel execution

                    break;
                case DISTRIBUTED:
                    currentExe=new DistributedExe(this);
                    //currentExe.start();
                    //Call Distributed execution

                    break;

            }
        }
    }


    public void draw(){
        myGui.guiDraw();
    }
    public void log(String str){
        myGui.guiLog(str);
    }





}
