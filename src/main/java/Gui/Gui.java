package Gui;

import Executive.Variables;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Gui extends JFrame{

    //should just DRAW stuff
    //-NO MATH IN HERE
    //should be able to just turn it off

    Variables variables;

    private int screenWidth=1440;

    private int screenHeight=800;
    private BoidField boidField;

    private final float WIDTHRATIO= 0.85F;
    private final float HEIGHTRATIO= 0.93F;


    private Dimension boidsFieldSize=new Dimension((int) (screenWidth*WIDTHRATIO), (int) (screenHeight*HEIGHTRATIO));




    public Gui(Variables variables){

        this.variables = variables;
        variables.setBoidFieldSize(boidsFieldSize);
        Logger.getInstance().log("Welcome to Boids Simulation! (by yours truly)");

        //JFrame
        //------------------------------------------------------------------------------------------------------------------------

        setTitle("Boids simulation");
        setSize(screenWidth,screenHeight);// HELLEELLELELL
        setLayout(new BorderLayout());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //JPanels
        //------------------------------------------------------------------------------------------------------------------------
        boidField=new BoidField(variables,Color.decode("#1A79DB"));



        // it was screenHeight*HeigthRatio before
        SideMenu sidePanel=new SideMenu(new Dimension((int) (screenWidth*(1-WIDTHRATIO)), (screenHeight)),Color.decode("#A6DFF2"), variables);
        BottomMenu bottomPanel=new BottomMenu(new Dimension((screenWidth), (int) (screenHeight*(1-HEIGHTRATIO))),Color.decode("#A6DFF2"),variables);


        add(boidField,BorderLayout.WEST);
        add(sidePanel,BorderLayout.EAST);
        add(bottomPanel,BorderLayout.SOUTH);


        //------------------------------------------------------------------------------------------------------------------------

        //THERE IS A GETWIDTH THINGY

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                screenWidth=getWidth();
                screenHeight=getHeight();



                boidsFieldSize=new Dimension((int) (screenWidth*WIDTHRATIO), (int) (screenHeight*HEIGHTRATIO));
                variables.setBoidFieldSize(boidsFieldSize);
                boidField.resizePanel(boidsFieldSize);


                sidePanel.resizePanel(new Dimension((int) (screenWidth*(1-WIDTHRATIO)), (int) (screenHeight)));
                bottomPanel.resizePanel(new Dimension((screenWidth), (int) (screenHeight*(1-HEIGHTRATIO))));


                boidsFieldSize=new Dimension((int) (screenWidth*WIDTHRATIO), (int) (boidField.getHeight()));
                variables.setBoidFieldSize(boidsFieldSize);
                boidField.resizePanel(boidsFieldSize);

                Logger.getInstance().log("Screen: "+screenWidth+"w "+screenHeight+"h");
                Logger.getInstance().log("BoidsFieldSize: "+boidsFieldSize.width+"w "+boidsFieldSize.height+"h " +boidField.getHeight()+"w "+boidField.getWidth()+"h");
                Logger.getInstance().log("BottomMenu: "+bottomPanel.getWidth()+"w "+bottomPanel.getHeight()+"h ");


            }
        });


        setVisible(true);
    }


    public void guiDraw(){
        boidField.repaint();//this trigures the
    }

    public void guiLog(String s){
        Logger.getInstance().log(s);
    }
}
