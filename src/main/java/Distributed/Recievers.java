package Distributed;

import Boids.BoidMPI;
import Boids.Vector3D;
import Functions.FunctionsMPI;
import SpatialHashing.BoxMPI;
import mpi.MPI;

import java.util.HashMap;
import java.util.Vector;

public class Recievers {

    public static WorkersGrid workerCp(WorkersGrid grid) {

        float[] commonData = new float[10];

        MPI.COMM_WORLD.Bcast(commonData, 0, commonData.length, MPI.FLOAT, 0);

        int[] tinyArr = new int[3];

        int[] cutBoxData;
        int[] boxData;
        float[] boidsData;
        float[] ultimateData;
        int boxDataLength;


        int totalNumberOfBoidsInChunk;
        int numberOfBoxes;
        BoidMPI[] boidsInOrderOfRecieving;


        //get the common data

        DistributedVariables distributedVariables =
                new DistributedVariables(
                        (int) commonData[0],
                        (int) commonData[1],
                        (int) commonData[2],
                        (int) commonData[3],
                        (int) commonData[4],
                        (int) commonData[5],
                        (int) commonData[6]);

        MPI.COMM_WORLD.Recv(tinyArr, 0, tinyArr.length, MPI.INT, 0, 1);


        /*
        for (int i = 0; i < commonData.length; i++) {
            System.out.println("Commondata"+ i+" Reciever" + MPI.COMM_WORLD.Rank()+" Data "+commonData[i] );
        }
         */


        totalNumberOfBoidsInChunk = tinyArr[0];
        numberOfBoxes = tinyArr[1];
        boxDataLength = tinyArr[2];


        if (boxDataLength != 0 && grid == null) { //Just first while loop we make the workers grid also we check if we did run the while loop twice or so for that && grid==null
            grid = new WorkersGrid(new BoxMPI[numberOfBoxes], new HashMap<>(numberOfBoxes));
        }

        //ultimate data-------------------------------------------------------

        cutBoxData = new int[numberOfBoxes];
        boxData = new int[boxDataLength];
        boidsData = new float[totalNumberOfBoidsInChunk * 8];

        boidsInOrderOfRecieving = new BoidMPI[totalNumberOfBoidsInChunk];


        if (boxDataLength == 0) {//we didnt get any box data - we do that only first time
            ultimateData = new float[totalNumberOfBoidsInChunk * 8];
        } else {
            ultimateData = new float[numberOfBoxes + boxDataLength + (totalNumberOfBoidsInChunk * 8)];
        }


        //System.out.println("Reciever"+MPI.COMM_WORLD.Rank()+" Ultimate data length "+ultimateData.length+ " Number of boids "+totalNumberOfBoidsInChunk);

        //RECIEVE ULTIMATE DATA

        MPI.COMM_WORLD.Recv(ultimateData, 0, ultimateData.length, MPI.FLOAT, 0, 2);


        if (boxDataLength == 0) {
            boidsData = ultimateData;
        } else {

            for (int i = 0; i < numberOfBoxes; i++) {
                cutBoxData[i] = (int) ultimateData[i];
            }

            for (int i = numberOfBoxes; i < numberOfBoxes + boxDataLength; i++) {
                boxData[i - numberOfBoxes] = (int) ultimateData[i];
            }

            for (int i = numberOfBoxes + boxDataLength; i < ultimateData.length; i++) {
                boidsData[i - numberOfBoxes - boxDataLength] = ultimateData[i];
            }

        }


        /*
        for (int i = 0; i < boidsData.length; i++) {
            System.out.println("Recieving boidsData"+i+" Data: "+boidsData[i]);
        }


         */

        //Processs BOXES ------------------------------------------------------


        if (boxDataLength != 0) { //only first itteration

            int currentOffset = numberOfBoxes; //start from number of boxes

            for (int i = 0; i < numberOfBoxes; i++) {
                int cut = cutBoxData[i];
                int[] boxNeighbourhood = new int[cut];

                for (int k = 0; k < cut; k++) {
                    boxNeighbourhood[k] = (int) ultimateData[currentOffset++]; //and count all the way up
                }

                grid.myBoxes[i] = new BoxMPI(boxNeighbourhood);
                grid.generalIndexToLocal.put(boxNeighbourhood[0], i);
            }
        }

        grid.generalIndexToLocal.put(0, 0); //Ehhh........








        int order=0;
        for (int i = 0; i < boidsData.length; i+=8) {


            int generalBoxIdOfThisBoid=(int) boidsData[i + 6];
            int localBoxIdOfThisBoid=0;


            if (grid.generalIndexToLocal.get(generalBoxIdOfThisBoid) == null) {
                System.err.println("Missing mapping for generalBoxId: " + generalBoxIdOfThisBoid);
                continue; // or handle the missing case appropriately
            }else{
                localBoxIdOfThisBoid=grid.generalIndexToLocal.get(generalBoxIdOfThisBoid);
            }





            /*

                        boidsData[k * 8 + 0] = b.getPosition().getX();
                        boidsData[k * 8 + 1] = b.getPosition().getY();
                        boidsData[k * 8 + 2] = b.getPosition().getZ();

                        boidsData[k * 8 + 3] = b.getVelocity().getX();
                        boidsData[k * 8 + 4] = b.getVelocity().getY();
                        boidsData[k * 8 + 5] = b.getVelocity().getZ();

                        boidsData[k * 8 + 6] = boxIndex;
                        boidsData[k * 8 + 7] = b.getCourse();

             */

            Vector3D position= new Vector3D(boidsData[i + 0], boidsData[i + 1], boidsData[i + 2]);
            Vector3D velocity= new Vector3D(boidsData[i + 3], boidsData[i + 4], boidsData[i + 5]);
            //int genBoxIndex=(int)boidsData[i+6];
            BoxMPI myBox= grid.myBoxes[localBoxIdOfThisBoid];
            float curse=boidsData[i+7];


            /*
            commonData[7]= uselessBoid.getSPEED();
            commonData[8]= uselessBoid.getTURN_RADIUS();
            commonData[9]= uselessBoid.getMAX_FORCE();
             */


            float SPEED=commonData[7]; //actually speed is always set to speed limit so its more of just SPEED
            float TURN_RADIUS=commonData[8];//was 500
            float MAX_FORCE=commonData[9];

            /*
            (Vector3D position,
                   Vector3D velocity,
                   BoxMPI myBox,
                   BoxMPI[] allBoxes,
                   HashMap generalIndexToLocal,
                   float SPEED,
                   float TURN_RADIUS,
                   float MAX_FORCE,
                   float course) {

             */

            BoidMPI boidMPI = new BoidMPI(
                    position,
                    velocity,
                    myBox,
                    grid.myBoxes,
                    grid.generalIndexToLocal,
                    SPEED,
                    TURN_RADIUS,
                    MAX_FORCE,
                    curse);

            grid.myBoxes[localBoxIdOfThisBoid].addBoidToBox(boidMPI);
            boidsInOrderOfRecieving[order]=boidMPI;
            order++;
        }






        float[] returnArr = new float[totalNumberOfBoidsInChunk * 3];



        for (int i = 0; i < boidsInOrderOfRecieving.length; i++) {
            BoidMPI b = boidsInOrderOfRecieving[i];
            Vector3D returnV = FunctionsMPI.theAllMightyFunctionMPI(b, distributedVariables);

            int baseIndex = i * 3;
            returnArr[baseIndex] = returnV.getX();
            returnArr[baseIndex + 1] = returnV.getY();
            returnArr[baseIndex + 2] = returnV.getZ();

        }






        /*


        for (int i = 1; i < 8; i++) {

            if (MPI.COMM_WORLD.Rank()==i) {
                System.out.println(" Sender1" +i+"-----------------------------------");
                for (int j = 0; j < returnArr.length; j++) {
                    System.out.println("Data " + j + " " + returnArr[j]);
                }
            }
        }

         */


        MPI.COMM_WORLD.Send(returnArr,0,returnArr.length,MPI.FLOAT,0,4);



        //prepare for next round

        for (BoxMPI box:grid.myBoxes) {
            box.getBoidsInThisBox().clear();
        }

        //this is linking between boxes so it remains
        //grid.generalIndexToLocal;



        return grid;
    }
}
