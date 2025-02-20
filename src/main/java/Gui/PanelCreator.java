package Gui;

import Executive.Variables;

import javax.swing.*;
import java.awt.*;

public class PanelCreator extends JPanel {
    Variables variables;
    Dimension size;
    Color backgroundColor;

    public PanelCreator(Dimension size, Color backgroundColor, Variables variables) {
        this.variables=variables;
        this.size = size;
        this.backgroundColor = backgroundColor;

        this.setPreferredSize(size);
        this.setBackground(backgroundColor);
    }


    public void resizePanel(Dimension newSize){
        this.setPreferredSize(newSize);
        revalidate();
        repaint();
    }
}
