package com.mycompany.flappyFace.ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

// Assuming HomeUI is in the same package or imported
import com.mycompany.flappyFace.ui.HomeUI; 
import java.util.logging.Logger;

// ... other imports like java.awt.*, javax.swing.* ...

import com.mycompany.flappyFace.ui.VideoUi; 

// ... rest of the HomeUI class ...
public class VideoUi extends JFrame {

    private static final Logger logger = Logger.getLogger(VideoUi.class.getName());
    private final HomeUI homeUI;
    private static final int VIDEO_DURATION_MS = 5000; // 5 seconds duration for the video/animation

    /**
     * @param homeUI The instance of the main menu frame to return to.
     */
    public VideoUi(HomeUI homeUI) {
        this.homeUI = homeUI;
        
        // --- Frame Setup ---
        setUndecorated(true);
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // --- Content Panel (Simulated Video) ---
        JPanel panel = new JPanel() {
            private static final long serialVersionUID = 1L;
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(50, 0, 0)); // Dark red/black background
                g.fillRect(0, 0, getWidth(), getHeight());
                
                g.setColor(Color.WHITE);
                g.setFont(new Font("Monospaced", Font.BOLD, 72));
                String message = "GAME OVER";
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(message)) / 2;
                int y = (getHeight() / 2) - 50;
                g.drawString(message, x, y);
                
                g.setFont(new Font("Monospaced", Font.ITALIC, 28));
                String subtitle = "Preparing return to Home screen...";
                int subX = (getWidth() - fm.stringWidth(subtitle)) / 2;
                g.drawString(subtitle, subX, y + 60);
            }
        };
        setContentPane(panel);
        
        // --- Timer for Transition ---
        ActionListener transitionListener = e -> {
            // 1. Dispose of the current video screen
            this.dispose(); 
            
            // 2. Show the HomeUI (Main Menu)
            if (homeUI != null) {
                homeUI.setVisible(true);
                // Call fade-in if HomeUI has it, for a smooth return
                // We assume HomeUI has a public fadeIn() method.
                try {
                    homeUI.getClass().getMethod("fadeIn").invoke(homeUI);
                } catch (Exception ex) {
                    logger.warning("HomeUI.fadeIn() method not found or failed to invoke.");
                    // Fallback: just ensure it's visible
                    homeUI.setVisible(true);
                }
            }
        };
        
        // Start the timer to transition back after 5 seconds
        Timer timer = new Timer(VIDEO_DURATION_MS, transitionListener);
        timer.setRepeats(false); // Only run once
        timer.start();
    }
}