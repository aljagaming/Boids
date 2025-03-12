package Gui;

import Executive.ExecutionStyle;
import Executive.Variables;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SideMenu extends PanelCreator {
    public SideMenu(Dimension size, Color backgroundColor, Variables variables) {

        super(size, backgroundColor, variables);

        this.setLayout(new BorderLayout());



        //---------------------------------------------------------------------------------------------------------------
        JPanel upperPanel=new JPanel();
        upperPanel.setBackground(getBackground());
        upperPanel.setLayout(new GridLayout(4,1));



        JButton restart=new JButton();
        restart.setText("Restart");

        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                variables.log("Restarting...");
                variables.setRestarting(true);
            }
        });




        JButton tracePath=new JButton();
        tracePath.setText("Trace Paths");

        tracePath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                variables.setTracePaths(!variables.getTracePaths());

                if (variables.getTracePaths()){
                    Logger.getInstance().log("Trace paths: ON ");
                }else{
                    Logger.getInstance().log("Trace paths: OFF ");
                }

            }
        });



        JPanel counter=new JPanel();
        counter.setBackground(getBackground());
        counter.setLayout(new GridLayout(1,3));


        JButton incrementButton = new JButton("+");
        JButton decrementButton = new JButton("-");
        JTextField counterTextField=new JTextField(Integer.toString(variables.getNumOfBoids()));
        counterTextField.setHorizontalAlignment(SwingConstants.CENTER);


        incrementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                variables.setNumOfBoids(variables.getNumOfBoids()+1);
                counterTextField.setText(Integer.toString(variables.getNumOfBoids()));

                Logger.getInstance().log("#Boids: "+variables.getNumOfBoids());
            }
        });

        decrementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (variables.getNumOfBoids()>0) {
                    variables.setNumOfBoids(variables.getNumOfBoids() - 1);
                    counterTextField.setText(Integer.toString(variables.getNumOfBoids()));

                    Logger.getInstance().log("#Boids: "+variables.getNumOfBoids());
                }
            }
        });


        counterTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = counterTextField.getText();
                try {
                    variables.setNumOfBoids(Integer.parseInt(text));
                    Logger.getInstance().log("#Boids: "+variables.getNumOfBoids());

                }catch (NumberFormatException ex) {

                    JOptionPane.showMessageDialog(null, "XD - I see what you did there !!!", "NotANumError", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        counter.add(incrementButton);
        counter.add(counterTextField);
        counter.add(decrementButton);



        String[] options={"Sequential execution","Parallel execution","Distributed execution"};
        JComboBox<String> selector= new JComboBox<>(options);


        selector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedOption = (String) selector.getSelectedItem();
                ExecutionStyle newStyle=ExecutionStyle.SEQUENTIAL; //default

                switch (selectedOption) {
                    case "Sequential execution":
                        Logger.getInstance().log("sequentialExe...");
                        newStyle = ExecutionStyle.SEQUENTIAL;
                        break;
                    case "Parallel execution":
                        Logger.getInstance().log("ParallelExe...");
                        newStyle = ExecutionStyle.PARALLEL;
                        break;
                    case "Distributed execution":
                        newStyle = ExecutionStyle.DISTRIBUTED;
                        Logger.getInstance().log("DistributedExe...");
                        break;
                }

                variables.updateExecutionStyle(newStyle);
            }
        });



        upperPanel.add(restart);
        upperPanel.add(tracePath);
        upperPanel.add(counter);
        upperPanel.add(selector);


        //-------------------------------------------------------------------------------------------------------------------------------------------------------------

        JPanel debugger=new JPanel();
        debugger.setLayout(new BorderLayout());
        debugger.setBackground(getBackground());



        JLabel debuggerLabel=new JLabel("-Debugger-");
        debuggerLabel.setHorizontalAlignment(getWidth()/2);
        debuggerLabel.setFont(new Font("Monospaced", Font.PLAIN, 18));






        debugger.add(debuggerLabel, BorderLayout.NORTH);
        debugger.add(Logger.getInstance().getScrollPane(), BorderLayout.CENTER);

        //-------------------------------------------------------------------------------------------------------------------------------------------


        add(upperPanel,BorderLayout.NORTH);
        add(debugger,BorderLayout.CENTER);


    }
}
