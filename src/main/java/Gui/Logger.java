package Gui;

import javax.swing.*;
import java.awt.*;

public class Logger {

    private static Logger instance;
    private JTextArea textArea;
    private JScrollPane scrollPane;

    private Logger() {

        textArea=new JTextArea();
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setBackground(Color.decode("#0E4E90"));
        textArea.setForeground(Color.WHITE);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        scrollPane = new JScrollPane(textArea);
    }

    public synchronized void log(String message) {
        int maxLines=150;
        int numLines=textArea.getLineCount();
        if (numLines>maxLines){
            try {
                int endOffset = textArea.getLineEndOffset(numLines - maxLines - 1);
                textArea.replaceRange("", 0, endOffset);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        textArea.append(" "+message + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }


    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public JScrollPane getScrollPane() {
        if (instance == null) {
            instance = new Logger();
            return instance.getScrollPane();
        }
        return scrollPane;
    }

}



