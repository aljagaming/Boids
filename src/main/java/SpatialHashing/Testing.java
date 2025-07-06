package SpatialHashing;

import java.util.ArrayList;
import java.util.Scanner;
import java.awt.*;
public class Testing {
    public static void main(String[] args) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();




        //int[] test=new int[]{1,1,1}; //lets see what are the surrounding boxes of the box with index 1,1,1
        int[] test=new int[]{3,3,3};

        ArrayList<int[]> surroundingBoxesIds = new ArrayList<>();

        int numberOfBoxesX=(int)Math.ceil((double) screenSize.width/50); // So that 154/50 = 4 and not 3
        int numberOfBoxesY=(int)Math.ceil((double) screenSize.height/50);
        int numberOfBoxesZ=1000/50;

        System.out.println("NumberOfBoxesX: "+numberOfBoxesX+ "     numberOfBoxesY: "+numberOfBoxesY+"  NumberOfBoxesZ: "+numberOfBoxesZ);



        int x=test[0];
        int y=test[1];
        int z=test[2];



        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k<= 1; k++) {

                    int newX = x + k;
                    int newY = y + j;
                    int newZ = z + i;

                    if (newX == x && newY == y && newZ == z) {
                        continue;
                        //dont get the same this box in
                    }

                    // Skip unreachable boxes
                    if (newX < 0 || newY < 0 || newZ < 0 || newX >= numberOfBoxesX || newY >= numberOfBoxesY || newZ >= numberOfBoxesZ) {
                        continue;
                    }

                    surroundingBoxesIds.add(new int[]{newX, newY, newZ});

                }
            }
        }



        for (int i = 0; i < surroundingBoxesIds.size(); i++) {

            for (int j = 0; j < 3; j++) {
                System.out.print(surroundingBoxesIds.get(i)[j] +" ");
            }
            System.out.println(" Actuall id in array: "+
                    (surroundingBoxesIds.get(i)[0] +
                    surroundingBoxesIds.get(i)[1]*numberOfBoxesX +
                    surroundingBoxesIds.get(i)[2]*numberOfBoxesX*numberOfBoxesY)
            );

            System.out.println();
        }
        System.out.println("Width: "+screenSize.width+"Height: "+screenSize.height);
        System.out.println("Length: "+surroundingBoxesIds.size());
    }
}
