package Gui;

import Executive.Variables;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class BottomMenu extends PanelCreator {

    public BottomMenu(Dimension size, Color backgroundColor, Variables variables) {
        super(size, backgroundColor, variables);


        setLayout(new GridLayout(2,5));

        String[] labelNames=new String[] {"Coherence","Separation","Alignment","Visual range","Speed"};


        for (int i = 0; i < 5; i++) {
            JLabel label=new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER/2);
            label.setText(labelNames[i]);
            label.setFont(new Font("Roboto", Font.BOLD, 18));
            add(label);
        }



        for (int i = 0; i < 5; i++) {

            JSlider slider=new JSlider();
            slider.setName(labelNames[i]);



            slider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                     JSlider sliderThatTrigeredTheEvent= (JSlider) e.getSource();
                     int value= sliderThatTrigeredTheEvent.getValue();

                     if (!sliderThatTrigeredTheEvent.getValueIsAdjusting()) {

                         switch (sliderThatTrigeredTheEvent.getName()) {

                             case "Coherence":

                                 variables.setCoherence(value);
                                 Logger.getInstance().log("Coherence: " + value);

                                 break;
                             case "Separation":

                                 variables.setSeparation(sliderThatTrigeredTheEvent.getValue());
                                 Logger.getInstance().log("Separation: " + value);

                                 break;
                             case "Alignment":

                                 variables.setAlignment(sliderThatTrigeredTheEvent.getValue());
                                 Logger.getInstance().log("Alignment: " + value);

                                 break;
                             case "Visual range":

                                 variables.setVisualRange(sliderThatTrigeredTheEvent.getValue());
                                 Logger.getInstance().log("Visual range: " + value);

                                 break;
                             case "Speed":

                                 variables.setAnimationSpeed(sliderThatTrigeredTheEvent.getValue());
                                 Logger.getInstance().log("Speed: " + value);

                                 break;
                         }
                     }

                }
            });


            add(slider);

        }





    }

}
