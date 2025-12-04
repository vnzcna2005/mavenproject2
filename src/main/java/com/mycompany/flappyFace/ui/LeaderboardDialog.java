/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.flappyFace.ui;

import java.awt.*;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;

public class LeaderboardDialog extends JDialog {

    private Font pixelFont;
    

    private static final Map<String, Integer> TOP_PLAYERS = createFixedScores();

    private static Map<String, Integer> createFixedScores() {
       
        Map<String, Integer> scores = new LinkedHashMap<>();
        scores.put("Budoy", 1000);
        scores.put("Raul", 790);
        scores.put("Miguel", 700);
        scores.put("Natoy", 680);
        scores.put("Dagol", 670);    
        scores.put("Litoy", 590);   
     
       return scores;
    }

    public LeaderboardDialog(Frame owner) {
        super(owner, "🏆 Top Players 🏆", true); 
        
     
        try {
            pixelFont = Font.createFont(
                    Font.TRUETYPE_FONT, 
                    new File("src/main/resources/fonts/PressStart2P.ttf")
            ).deriveFont(16f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(pixelFont);
        } catch (Exception e) {
            pixelFont = new Font("Arial", Font.BOLD, 16);
        }

      
        setSize(400, 350);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

     
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Top Players", SwingConstants.CENTER);
        titleLabel.setFont(pixelFont.deriveFont(24f));
        titleLabel.setForeground(new Color(255, 215, 0)); 
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

   
        JPanel scorePanel = new JPanel(new GridLayout(8, 2, 5, 5));
        scorePanel.setBackground(new Color(30, 30, 30));
        
      
        addHeaderLabel(scorePanel, "Rank/Name", new Color(170, 170, 255));
        addHeaderLabel(scorePanel, "Score", new Color(170, 170, 255));

      
        int rank = 1;
        for (Map.Entry<String, Integer> entry : TOP_PLAYERS.entrySet()) {
            String name = entry.getKey();
            int score = entry.getValue();
            
            String rankAndName = rank + ". " + name;
            
         
            JLabel nameLabel = new JLabel(rankAndName, SwingConstants.LEFT);
            nameLabel.setFont(pixelFont.deriveFont(14f));
            nameLabel.setForeground(Color.WHITE);
            scorePanel.add(nameLabel);

      
            JLabel scoreLabel = new JLabel(String.valueOf(score), SwingConstants.RIGHT);
            scoreLabel.setFont(pixelFont.deriveFont(14f));
            scoreLabel.setForeground(new Color(0, 255, 0)); 
            scorePanel.add(scoreLabel);
            
            rank++;
        }

        mainPanel.add(scorePanel);
        mainPanel.add(Box.createVerticalGlue()); 

        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }
    
  
    private void addHeaderLabel(JPanel panel, String text, Color color) {
        JLabel header = new JLabel(text, SwingConstants.CENTER);
        header.setFont(pixelFont.deriveFont(Font.BOLD, 14f));
        header.setForeground(color);
        panel.add(header);
    }
}
