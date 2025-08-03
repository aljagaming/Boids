package Distributed;

import Boids.Vector3D;
import Executive.ExecutionInterface;
import Executive.ExecutionStyle;
import Executive.Variables;
import FPS.Clock;
import Functions.Functions;
import Gui.Logger;
import Parallel.Chunk;
import Boids.Boid;
import Parallel.OneChunkOneThread;
import SpatialHashing.Box;

import mpi.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.concurrent.BrokenBarrierException;

public class DistributedExe implements ExecutionInterface {

    Variables variables;
    Boolean running = true;
    Boolean firstIteration = true;

    public DistributedExe(Variables variables) {
        this.variables = variables;
    }


    @Override
    public void start() {

        running=true;
        firstIteration = true;

        Clock.reset();
        Clock.start();

        Logger.getInstance().log("Distributed.exe started!");
        int size = MPI.COMM_WORLD.Size(); // 8

        if (size>8){
            System.out.println("Distributed can only run on up to: 8 cores");
            JOptionPane.showMessageDialog(null, "Distributed can only run on up to: 8 cores", "NotANumError", JOptionPane.INFORMATION_MESSAGE);
            variables.updateExecutionStyle(ExecutionStyle.SEQUENTIAL);
            variables.getCurrentExe().start();
            return;
        }

        int numberOfProccesingChunks = size - 1; // =7

        //numberOfProccesingChunks=Math.max(numberOfProccesingChunks,6);


        //Chunking---------------------------------------------------------------------------------------------------
        int chunkSizeX = variables.getBoxGrid().numberOfBoxesX / numberOfProccesingChunks;
        int remainderX = variables.getBoxGrid().numberOfBoxesX % numberOfProccesingChunks;
        Chunk[] distributeChunks = new Chunk[numberOfProccesingChunks];



        //Data------------------------------------------------------------------------------------------------
        float[] commonData = new float[10];
        int[] tinyArr=new int[3];

        int[] cutBoxData;
        ArrayList<Integer> boxDataList = new ArrayList<>();
        int[] boxData;

        float[] boidsData;


        float[] ultimateData;


        int totalNumberOfBoidsInChunk;
        int numberOfBoxes;
        Boid uselessBoid=new Boid();

        //float resultVectorArray=variables.getNumOfBoids()*3;
        ArrayList<Boid> boidsOrder=new ArrayList<>();





        //Data--------------------------------------------------------------------

        //chunking
        int startX = 0;
        //goes from 0 - 6
        for (int i = 0; i < numberOfProccesingChunks; i++) {
            int endX = startX + chunkSizeX;
            if (remainderX > 0) {
                endX += 1;
                remainderX -= 1;
            }
            //startX is inclusive and endX is exclusive
            distributeChunks[i] = new Chunk(variables.getBoxGrid(), startX, endX);
            startX = endX;
        }









        while (running) {

            //COMMON DATA ----------------------------------------------------------------------------------------------

            if (!firstIteration){
                Clock.start();
            }

            Functions.updateNumOfBoids(variables);

            float[] allData=new float[variables.getNumOfBoids()*3];

            commonData[0] = variables.getVisualRange();
            commonData[1] = variables.getCoherence();
            commonData[2] = variables.getSeparation();
            commonData[3] = variables.getAlignment();
            commonData[4] = variables.getBoidFieldSize().width;
            commonData[5] = variables.getBoidFieldSize().height;
            commonData[6] = variables.getDEPTH();
            commonData[7]= uselessBoid.getSPEED();
            commonData[8]= uselessBoid.getTURN_RADIUS();
            commonData[9]= uselessBoid.getMAX_FORCE();


            //Sends the data that every core needs to have and is the same
            MPI.COMM_WORLD.Bcast(commonData, 0, commonData.length, MPI.FLOAT, 0);


            //----------------------------------------------------------------------------------------------------------

            //number of processing chuks is=7 0-6 but MPI.rank 1-7
            //number of processing chuks is=7 0-6 but MPI.rank 1-7
            //number of processing chuks is=7 0-6 but MPI.rank 1-7
            for (int i = 0; i < numberOfProccesingChunks; i++) { //0-6


                //DISTRIBUTED CHUNKS 0-6
                totalNumberOfBoidsInChunk = distributeChunks[i].getBoidsInThisChunk().size();

                //boidsOrder.addAll(distributeChunks[i].getBoidsInThisChunk());//remember the order so according to that order we update the position


                //get number of the boxes
                numberOfBoxes = distributeChunks[i].getBoxesOfThisChunk().length;


                //Data

                cutBoxData = new int[numberOfBoxes];//what is the length of each Box neighbourhood, there are exactly numberOfBoxes boxes

                boidsData = new float[totalNumberOfBoidsInChunk * 8]; //boids Data



                //boidsOrder.clear();






                int currentBoidProcessing=0;

                for (int j = 0; j < numberOfBoxes; j++) {

                    Box box = distributeChunks[i].getBoxesOfThisChunk()[j];
                    int boxIndex =box.getIndexInBigArray();



                    //Send the Neighbourhood of the box with the first thing in array beeing the boxes own id

                    if (firstIteration) {

                        ArrayList<Integer> surroundingBoxes = new ArrayList<>(box.getSurroundingBoxesIds());
                        surroundingBoxes.add(0, boxIndex);
                        //the first one is the index of this box

                        cutBoxData[j] = surroundingBoxes.size();
                        boxDataList.addAll(surroundingBoxes);
                    }



                    //Packing BOIDS DATA

                    int boidIndex = 0;
                    for (int k = 0; k < box.getBoidsInThisBox().size(); k++) {

                        Boid b = box.getBoidsInThisBox().get(k);
                        //we go boid by boid box by box

                        //place boidsData

                        boidsOrder.add(b);

                        boidsData[currentBoidProcessing * 8 + 0] = b.getPosition().getX();
                        boidsData[currentBoidProcessing * 8 + 1] = b.getPosition().getY();
                        boidsData[currentBoidProcessing * 8 + 2] = b.getPosition().getZ();

                        boidsData[currentBoidProcessing * 8 + 3] = b.getVelocity().getX();
                        boidsData[currentBoidProcessing * 8 + 4] = b.getVelocity().getY();
                        boidsData[currentBoidProcessing * 8 + 5] = b.getVelocity().getZ();

                        boidsData[currentBoidProcessing * 8 + 6] = boxIndex;
                        boidsData[currentBoidProcessing * 8 + 7] = b.getCourse();
                        currentBoidProcessing++;




                    }
                }


                //System.out.println("Total boids serialized: " + currentBoidProcessing);

                boxData = boxDataList.stream().mapToInt(Integer::intValue).toArray();
                boxDataList.clear();


                //metadata
                tinyArr[0]=totalNumberOfBoidsInChunk;
                tinyArr[1]=numberOfBoxes;
                tinyArr[2]=boxData.length;



                //Sends Metadata that is needed for processing the ultimate data
                MPI.COMM_WORLD.Send(tinyArr,0,tinyArr.length,MPI.INT,i+1,1);



                //------------------------------------------------------------------------------------------------


               if (firstIteration){       //numberOfBoxes
                   ultimateData=new float[cutBoxData.length+boxData.length+boidsData.length];
               }else{
                   ultimateData=new float[boidsData.length];
               }

                for (int j = 0; j < ultimateData.length; j++) {


                    if (firstIteration) { //we are sending the grid data only first time

                        if (j < cutBoxData.length) {

                            ultimateData[j]=cutBoxData[j];

                        } else if (j < cutBoxData.length+boxData.length) {

                            ultimateData[j]=boxData[j-cutBoxData.length];

                        } else {

                            ultimateData[j]=boidsData[j-cutBoxData.length-boxData.length];
                        }
                    }else{

                        ultimateData[j]=boidsData[j];
                    }

                }








                MPI.COMM_WORLD.Send(ultimateData,0,ultimateData.length,MPI.FLOAT,i+1,2);

            }


            //RECIEVING DATA BACK----------------------------------------------------------------------------------------


            //
            int[] offset=new int[numberOfProccesingChunks];


            int count=0;
            for (int i = 0; i < numberOfProccesingChunks; i++) {
                offset[i]=count;
                count=count + distributeChunks[i].getBoidsInThisChunk().size()*3;//times 3 because we are recieving 3 things xyz of the vector

            }

            //now gather everthing at this place
            //MPI.COMM_WORLD.Gatherv(null,0,0,MPI.FLOAT,allData,0,howLongIsEachchunk,offset,MPI.FLOAT,0);
            //Poor mans Gatherv
            for (int i = 1; i < size; i++) {
                MPI.COMM_WORLD.Recv(allData,offset[i-1],distributeChunks[i-1].getBoidsInThisChunk().size()*3,MPI.FLOAT,i,4);
                //System.out.println("Recieved from: CHUNK" +(i-1)+" RANK"+i);
            }




            int totalBoids=0;


            for (int i = 0; i < variables.getNumOfBoids(); i++) {


                Boid b=boidsOrder.get(i);

                Vector3D returnV = new Vector3D(allData[i*3+0], allData[i*3+1], allData[i*3+2]);

                b.addAcceleration(returnV);

                b.move(variables.getAnimationSpeed());


                //returnV.add(Functions.border(b,variables.getBoidFieldSize().width,variables.getBoidFieldSize().height,variables.getDEPTH()));

            }


            variables.draw();






            try {
                variables.getBarrier().await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }

            firstIteration = false;
            boidsOrder.clear();

            Clock.end();










        }//end while
        variables.getCurrentExe().start();
    }//end start


    @Override
    public void stop() {
        running=false;
        Logger.getInstance().log("Distributed.exe stopping...");
    }
}
