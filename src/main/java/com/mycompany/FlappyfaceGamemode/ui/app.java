package com.mycompany.FlappyfaceGamemode.ui;

import javax.swing.*;

public class app {
    public static void main(String[] args) {
        int boardwidth =360; 
        int boardheight = 340;
        JFrame frame = new JFrame("Flappy Face");

        FlappyFace flappyface = new FlappyFace();
        

        frame.setSize(boardwidth,boardheight); 
        frame.setLocationRelativeTo(null); 
        frame.setResizable(false); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         
        frame.add(flappyface); 
        frame.pack(); 
        
        frame.setVisible(true);
    }
}
