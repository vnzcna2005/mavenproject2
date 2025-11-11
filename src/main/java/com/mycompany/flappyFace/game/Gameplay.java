/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.flappyFace.game;

/**
 *
 * @author VNZ
 */
import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Gameplay extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Gameplay.class.getName());
    private static final long serialVersionUID = 1L;
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;
   
    
    private class GamePanel extends JPanel {

        private static final long serialVersionUID = 1L;

        private Image faceImage;
        private Face flappyFace;

        private final int FACE_WIDTH = 80;
        private final int FACE_HEIGHT = 80;

        private Image bgImage;
        private Image pipeImage;
        private Image flippedPipeImage;

        private final List<Pipe> pipes = new ArrayList<>();
        private final int PIPE_WIDTH = 180;        // pipe draw width
        private final int PIPE_GAP = 210;          // gap between top and bottom pipe
        private final int PIPE_SPEED = 2;          // movement speed when started
        private final int PIPE_COUNT = 15;         // total pipes to show at start
        private final int PIPE_SPACING = 300;      // horizontal spacing between pipes

        private javax.swing.Timer timer;
        private boolean started = false;
        private boolean gameOver = false;
        private int score = 0;
        private int bestScore = 0;

        public GamePanel() {

            java.net.URL bgUrl = getClass().getResource("/images/game_bg.png");
            if (bgUrl == null) {
                bgUrl = getClass().getResource("/images/home_bg.png");
            }
            if (bgUrl != null) {
                bgImage = new ImageIcon(bgUrl).getImage();
            } else {
                bgImage = null;
                logger.warning("Background not found");
            }

            try {

                BufferedImage bufFace = ImageIO.read(getClass().getResourceAsStream("/images/Face.png"));
                if (bufFace != null) {
                    faceImage = bufFace;
                } else {
                    logger.warning("Face.png not found or unreadable");
                }
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Error loading Face.png", ex);
            }

            //top-facing pipe
            try {
                BufferedImage buf = ImageIO.read(getClass().getResourceAsStream("/images/pipes.png"));
                if (buf != null) {
                    pipeImage = buf;
                    // create flipped (vertical) version for bottom pipe
                    AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
                    tx.translate(0, -buf.getHeight());
                    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    BufferedImage flipped = op.filter(buf, null);
                    flippedPipeImage = flipped;
                } else {
                    pipeImage = null;
                    flippedPipeImage = null;
                    logger.warning("pipes.png not found or unreadable");
                }
            } catch (IOException ex) {
                pipeImage = null;
                flippedPipeImage = null;
                logger.log(Level.WARNING, "Error loading pipes.png", ex);
            }

            setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
            setDoubleBuffered(true);

            pipes.clear();

            // Initialize the flappy face
            int faceStartX = WINDOW_WIDTH / 4; // Roughly left-center
            int faceStartY = (WINDOW_HEIGHT - FACE_HEIGHT) / 2; // Vertically centered
            flappyFace = new Face(faceStartX, faceStartY, FACE_WIDTH, FACE_HEIGHT, faceImage);

            int firstX = WINDOW_WIDTH / 2;
            for (int i = 0; i < PIPE_COUNT; i++) {

                int x = firstX + (i * PIPE_SPACING);

                int margin = 60;
                int minGapY = margin;
                int maxGapY = WINDOW_HEIGHT - PIPE_GAP - margin;
                int gapY = minGapY + (int) (Math.random() * Math.max(1, maxGapY - minGapY));
                pipes.add(new Pipe(x, PIPE_WIDTH, gapY, PIPE_GAP, 0, pipeImage, flippedPipeImage));

            }

            // Timer but don't move until click          
            timer = new javax.swing.Timer(10, e -> {
                if (gameOver) {
                    //stop the timer and don't update anything
                    timer.stop();
                    repaint();
                    return; // Exit the update loop
                }

                // update pipe positions
                Iterator<Pipe> it = pipes.iterator();
                while (it.hasNext()) {
                    Pipe p = it.next();
                    p.update();

                    // --- SCORE CHECK (NEW) ---                  
                    if (!p.isPassed() && p.getX() < flappyFace.getX() && started) {
                        score++;
                        p.setPassed(true);
                    }

                    // --- COLLISION CHECK (EXISTING) ---
                    // 1. Get the face's bounding box
                    Rectangle faceBounds = flappyFace.getBounds();

                    // 2. Check collision with top and bottom pipes
                    for (Rectangle pipeBox : p.getBoundsList(getHeight())) {
                        if (faceBounds.intersects(pipeBox)) {
                            // COLLISION DETECTED!
                            gameOver = true;
                            // Update best score when game is over
                            if (score > bestScore) {
                                bestScore = score;

                            }
                            break;
                        }
                    }
                }

                // Check collision with ground/ceiling
                int groundY = getHeight();
                if (flappyFace.getY() + flappyFace.getHeight() >= groundY || flappyFace.getY() <= 0) { // Added ceiling check here too
                    if (!gameOver) {
                        gameOver = true;
                        if (score > bestScore) {
                            bestScore = score;
                            // prefs.putInt(BEST_SCORE_KEY, bestScore); // If using Preferences
                        }
                    }
                }

                // Update face position only if game is started and NOT over
                if (started && !gameOver) {
                    flappyFace.update();
                }

                repaint();
            });

            timer.setRepeats(true);
            // Start movement only when user clicks             
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (!started) {
                        started = true;

                        for (Pipe p : pipes) {
                            p.setSpeed(PIPE_SPEED);
                        }
                        timer.start();
                        flappyFace.jump();
                    } else if (gameOver) {
                        // Check for double click to restart
                        if (e.getClickCount() == 2) {
                            restartGame(); // Call the new restart method
                        }
                    } else {
                        // Standard jump while playing
                        flappyFace.jump();
                    }
                }
            });

        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // draw background
            if (bgImage != null) {
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(Color.CYAN);
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            // draw pipes
            for (Pipe p : pipes) {
                p.draw(g, getHeight());
            }

            flappyFace.draw(g);

            // DRAW SCORE 
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 60));
            String scoreStr = String.valueOf(score);
            int strW_score = g.getFontMetrics().stringWidth(scoreStr);
            g.drawString(scoreStr, (getWidth() - strW_score) / 2, 70);

            // DRAW BEST SCORE 
            // ------------------------------------------
            g.setFont(new Font("Arial", Font.BOLD, 24)); // Smaller font for indicator
            String bestStr = "BEST SCORE: " + bestScore;
            int bestStrW = g.getFontMetrics().stringWidth(bestStr);

           // Position: Near the right edge (getWidth() - margin) and high up (e.g., 40)
            int margin = 20;
            g.drawString(bestStr, getWidth() - bestStrW - margin, 40);

            // DRAW START INSTRUCTION (New/Modified)
            if (!started && !gameOver) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 40)); // Use a slightly larger font
                String s = "TAP TO START"; // Clearer instruction
                int strW = g.getFontMetrics().stringWidth(s);

                // Position the instruction slightly above the vertical center
                g.drawString(s, (getWidth() - strW) / 2, getHeight() / 2 - 200);
            }

            // --- ADD GAME OVER MESSAGE ---
            if (gameOver) {
                // Draw translucent background for score card
                g.setColor(new Color(0, 0, 0, 150)); // Black, 150 opacity
                g.fillRect(0, 0, getWidth(), getHeight());

                // Game Over Text
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 72));
                String go = "GAME OVER";
                int goW = g.getFontMetrics().stringWidth(go);
                g.drawString(go, (getWidth() - goW) / 2, getHeight() / 2 - 150);

                // Score Card Box
                g.setColor(new Color(255, 255, 255, 200));
                int boxW = 400;
                int boxH = 150;
                int boxX = (getWidth() - boxW) / 2;
                int boxY = (getHeight() - boxH) / 2;
                g.fillRect(boxX, boxY, boxW, boxH);

                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 30));

                // Current Score
                String currentStr = "Score: " + score;
                int currentW = g.getFontMetrics().stringWidth(currentStr);
                g.drawString(currentStr, (getWidth() - currentW) / 2, boxY + 50);

                // Best Score
                bestStr = "Best: " + bestScore;
                int bestW = g.getFontMetrics().stringWidth(bestStr);
                g.drawString(bestStr, (getWidth() - bestW) / 2, boxY + 110);

                // Restart Instruction
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.ITALIC, 20));
                String r = "Tap twice to restart";
                int rW = g.getFontMetrics().stringWidth(r);
                g.drawString(r, (getWidth() - rW) / 2, boxY + boxH + 50);

            }

            Toolkit.getDefaultToolkit().sync();
        }

        private void restartGame() {
            // 1. Reset Game State
            started = false;
            gameOver = false;
            score = 0;

            // 2. Reset Pipes
            pipes.clear();
            int firstX = WINDOW_WIDTH / 2;
            for (int i = 0; i < PIPE_COUNT; i++) {
                int x = firstX + (i * PIPE_SPACING);
                int margin = 60;
                int minGapY = margin;
                int maxGapY = WINDOW_HEIGHT - PIPE_GAP - margin;
                int gapY = minGapY + (int) (Math.random() * Math.max(1, maxGapY - minGapY));

                // Reset pipes with speed 0 and passed=false
                Pipe newPipe = new Pipe(x, PIPE_WIDTH, gapY, PIPE_GAP, 0, pipeImage, flippedPipeImage);
                newPipe.setPassed(false);
                pipes.add(newPipe);
            }

            // 3. Reset Face position
            int faceStartX = WINDOW_WIDTH / 4;
            int faceStartY = (WINDOW_HEIGHT - FACE_HEIGHT) / 2;
            flappyFace.setPosition(faceStartX, faceStartY);

            timer.stop();
            repaint();

        }
    }

    /**
     * Creates new form Gameplay
     */
    public Gameplay() {

        initComponents();

        GamePanel gamePanel = new GamePanel();
        setContentPane(gamePanel);

        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gamePanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout gamePanel1Layout = new javax.swing.GroupLayout(gamePanel1);
        gamePanel1.setLayout(gamePanel1Layout);
        gamePanel1Layout.setHorizontalGroup(
            gamePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );
        gamePanel1Layout.setVerticalGroup(
            gamePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 700, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(gamePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(gamePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Gameplay().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel gamePanel1;
    // End of variables declaration//GEN-END:variables
}
