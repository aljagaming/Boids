package Gui;

import Boids.Boid;
import Boids.Vector3D;
import Executive.Variables;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.BrokenBarrierException;

public class BoidField extends PanelCreator{
    public BoidField(Dimension size, Color backgroundColor, Variables variables) {
        super(size, backgroundColor, variables);
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);  // Always call the superclass' paintComponent method first to ensure proper rendering

        drawBoids(g);
    }


    //WATCH OUT YOU ARE DOING THIS FOR   EVERY   SINGLE   BOID    - it gets expensive
    public void drawBoids(Graphics g){

        //if its bigger then some other boid it should be Infront;
        //in other words If big-> draw Last
        //but if u directly change the order of variables.getArrayOfBoids() then when you delete you will be left just with small fellas


        //There must be a more elegant way to coppy an array list but leave it like this for now
        ArrayList<Boid> copy = new ArrayList<>(variables.getArrayOfBoids());



        copy.sort(new Comparator<Boid>() {

            //if negative that means 01 is less than o2
            //if negative that positive 01 is bigger than o2
            @Override
            public int compare(Boid o1, Boid o2) {

                //Float compare vs Int compare here
                return Float.compare(o1.getPosition().getZ(), o2.getPosition().getZ());
            }
        });


        for (Boid b: copy) {

            Vector3D position=b.getPosition();
            float course=b.getCourse();
            float size= position.getZ()/100+variables.getMIN_BOID_SIZE(); //replace 10 with MAX_BOID_SIZE


            //in case size goes over bounds


            // for each of the 3 vertecies of triangle we need both x and y cordinates
            int[] xPoints = new int[3];
            int[] yPoints = new int[3];

            float wingLength = size / 2; // means the base is 2 times shorter
            float wingAngle = (float) Math.PI / 6; // 30 degrees for a pointy triangle

            float x=position.getX();
            float y=position.getY();

            float centerX = (float) (x - (size / 2) * Math.cos(course));
            float centerY = (float) (y - (size / 2) * Math.sin(course));


            // Nose of the boid (forward)
            xPoints[0] = (int) (centerX + size * Math.cos(course));
            yPoints[0] = (int) (centerY + size * Math.sin(course));


            //Left wing
            xPoints[1] = (int) (centerX + wingLength * Math.cos(course - wingAngle)); // Left wing x = x + wingLength * cos(course - wingAngle)
            yPoints[1] = (int) (centerY + wingLength * Math.sin(course - wingAngle)); // Left wing y = y + wingLength * sin(course - wingAngle)

            // Right wing (angle = course + wingAngle)
            xPoints[2] = (int) (centerX + wingLength * Math.cos(course + wingAngle)); // Right wing x = x + wingLength * cos(course + wingAngle)
            yPoints[2] = (int) (centerY + wingLength * Math.sin(course + wingAngle));


            g.setColor(Color.WHITE);
            g.fillPolygon(xPoints, yPoints, 3);


            //this bellow is COOL
            // also how do you handle
            //but is expensive so maybe dont use it - no one cares bout GUI :(




            g.setColor(Color.BLACK);
            g.drawPolygon(xPoints, yPoints, 3);

            //g.setColor(Color.BLACK); // Set dot color
            //g.fillOval((int) (x - 4 / 2), (int) (y - 4 / 2), 4, 4);




            //g.setColor(Color.YELLOW);
            //g.drawOval((int)(x-variables.getVisualRange()/2),(int)(y-variables.getVisualRange()/2),variables.getVisualRange(), variables.getVisualRange());

        }





        g.setColor(Color.BLACK);
        int soft=500;

        g.drawRect(soft/4,soft/4,variables.getBoidFieldSize().width-2*soft/4,variables.getBoidFieldSize().height-2*soft/4);

        g.drawRect(soft/2,soft/2,variables.getBoidFieldSize().width-2*soft/2,variables.getBoidFieldSize().height-2*soft/2);



        //this is only important for Sequential part
        //in parallel part gui will have to be its own thread - cos its slow as crap

        try {
            variables.getBarrier().await(); // this is so the loop can continue back at Sequential Part
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }




    }
}
