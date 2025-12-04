/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// SettingsDialog.java - NEW FILE

package com.mycompany.flappyFace.ui; // Use your correct package name

import java.awt.*;
import javax.swing.*;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class SettingsDialog extends JDialog {

    private Clip backgroundClip;

    public SettingsDialog(Frame owner, Clip clip) {
        super(owner, "Game Settings", true);
        backgroundClip = clip;

        // Basic Setup 
        setSize(300, 200);
        setResizable(false);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Panel and Layout 
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Label 
        JLabel titleLabel = new JLabel("Volume Control", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18)); 
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

      
        JLabel volumeLabel = new JLabel("Music Volume:", SwingConstants.LEFT);
        volumeLabel.setForeground(new Color(170, 170, 170));
        volumeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 70); // Min, Max, Default
        volumeSlider.setBackground(new Color(40, 40, 40));
        volumeSlider.setForeground(Color.WHITE);
        volumeSlider.setMinorTickSpacing(10);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        
 
volumeSlider.addChangeListener(e -> {
    JSlider source = (JSlider) e.getSource();
    
  
    if (!source.getValueIsAdjusting() && backgroundClip != null && backgroundClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
        FloatControl gainControl = (FloatControl) backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);
        
       
        float gainPercentage = source.getValue() / 100.0f;
        
      
        float minDB = gainControl.getMinimum(); 
        float maxDB = gainControl.getMaximum(); 
        float range = maxDB - minDB;
        
       
        float db = (gainPercentage * range) + minDB;
        
     
        db = Math.min(maxDB, Math.max(minDB, db));
        
        gainControl.setValue(db); 
    }
});

        panel.add(volumeLabel);
        panel.add(volumeSlider);

        getContentPane().add(panel);
    }
    
 
}
