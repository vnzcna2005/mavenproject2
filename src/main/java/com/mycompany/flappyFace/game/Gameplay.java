/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.flappyFace.game;

/**
 *
 * @author RALPHYY
 */
// Imported the Necessary Java Libraries that needed
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

//Creates the Main Window and extends JFrame
public class Gameplay extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Gameplay.class.getName());
    private static final long serialVersionUID = 1L;
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;
//Where the actual game happens
    private class GamePanel extends JPanel {
        private final SoundPlayer soundPlayer = new SoundPlayer();
        private static final long serialVersionUID = 1L;

        private JButton pauseButton;
        private JButton exitButton;
        private Image faceImage;
        private Face flappyFace;
        private boolean paused = false; // Add this near 'started' and 'gameOver'
        private final int FACE_WIDTH = 80;
        private final int FACE_HEIGHT = 80;

        private Image bgImage;
        private Image pipeImage;
        private Image flippedPipeImage;

        private final List<Pipe> pipes = new ArrayList<>();
        private final int PIPE_WIDTH = 180;        // pipe draw width
        private final int PIPE_GAP = 200;          // gap between top and bottom pipe
        private final int PIPE_SPEED = 4;          // movement speed when started
        private final int PIPE_COUNT = 100;         // total pipes to show at start
        private final int PIPE_SPACING = 300;      // horizontal spacing between pipes

        private javax.swing.Timer timer;
        private boolean started = false;
        private boolean gameOver = false;
        private int score = 0;
        private int bestScore = 0;

        private Image playerImage;

        public GamePanel(String characterImagePath) {

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

                BufferedImage bufFace = ImageIO.read(getClass().getResourceAsStream(characterImagePath));
                if (bufFace != null) {

                    playerImage = bufFace;
                } else {
                    logger.warning("Custom character image not found or unreadable at path: " + characterImagePath);

                    playerImage = ImageIO.read(getClass().getResourceAsStream("/images/Face.png"));
                }
            } catch (IOException ex) {
                logger.log(java.util.logging.Level.SEVERE, "Error loading player image from path: " + characterImagePath, ex);

                try {
                    playerImage = ImageIO.read(getClass().getResourceAsStream("/images/Face.png"));
                } catch (IOException e) {
                    logger.log(java.util.logging.Level.SEVERE, "Error loading fallback image.", e);
                }
            }

            try {
                BufferedImage buf = ImageIO.read(getClass().getResourceAsStream("/images/pipes.png"));
                if (buf != null) {
                    pipeImage = buf;

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

            int faceStartX = WINDOW_WIDTH / 4;
            int faceStartY = (WINDOW_HEIGHT - FACE_HEIGHT) / 2;

            flappyFace = new Face(faceStartX, faceStartY, FACE_WIDTH, FACE_HEIGHT, playerImage);

            int firstX = WINDOW_WIDTH / 2;
            for (int i = 0; i < PIPE_COUNT; i++) {

                int x = firstX + (i * PIPE_SPACING);

                int margin = 60;
                int minGapY = margin;
                int maxGapY = WINDOW_HEIGHT - PIPE_GAP - margin;
                int gapY = minGapY + (int) (Math.random() * Math.max(1, maxGapY - minGapY));
                pipes.add(new Pipe(x, PIPE_WIDTH, gapY, PIPE_GAP, 0, pipeImage, flippedPipeImage));

            }
            

            timer = new javax.swing.Timer(10, e -> {
                
                // --- GAME OVER ANIMATION LOGIC (Bounce and Roll) ---
                if (gameOver) {
                    // Update the face's falling motion (gravity applied)
                    flappyFace.update(); 
                    
                    // Continue the roll (increase angle) only while the face is visible
                    if (flappyFace.getY() < getHeight() + flappyFace.getHeight()) {
                        double newAngle = flappyFace.getRotationAngle() + 0.5; // Rotation speed
                        flappyFace.setRotationAngle(newAngle);
                    }
                    
                    // Stop the timer once the face has fallen completely off-screen
                    if (flappyFace.getY() > getHeight() + flappyFace.getHeight()) {
                        timer.stop();
                    }

                    repaint();
                    return; // Skip all normal game logic below when animating the game over state
                }
                // --- END GAME OVER ANIMATION LOGIC ---
                
                
                // --- NORMAL GAME LOOP LOGIC ---

                Iterator<Pipe> it = pipes.iterator();
                while (it.hasNext()) {
                    Pipe p = it.next();
                    
                    // Pipes only move if the game is started and NOT over
                    if (started && !gameOver) {
                        p.update(); 
                    }

                    if (!p.isPassed() && p.getX() < flappyFace.getX() && started) {
                        score++;
                        p.setPassed(true);
                    }

                    Rectangle faceBounds = flappyFace.getBounds();

                    // Pipe Collision Check
                    for (Rectangle pipeBox : p.getBoundsList(getHeight())) {
                        if (faceBounds.intersects(pipeBox)) {
                            
                            if (!gameOver) { // Only execute collision logic once
                                gameOver = true;
                                
                                // BOUNCE BACK: Reverse horizontal pipe speed for recoil effect
                                for (Pipe activePipe : pipes) {
                                    activePipe.setSpeed(-PIPE_SPEED * 2); 
                                }
                                
                                // Apply high initial rotation angle for the start of the roll
                                flappyFace.setRotationAngle(flappyFace.getRotationAngle() + 2.0); 
                                soundPlayer.playClip("bonk.wav");
                                
                                if (score > bestScore) {
                                    bestScore = score;
                                }
                            }
                            break;
                        }
                    }
                }

                // Ground/Ceiling Collision Check
                int groundY = getHeight();
                if (flappyFace.getY() + flappyFace.getHeight() >= groundY || flappyFace.getY() <= 0) {
                    if (!gameOver) {
                        gameOver = true;
                        soundPlayer.playClip("bonk.wav"); 
                        
                        // Stop pipe movement instantly on floor/ceiling hit
                        for (Pipe activePipe : pipes) {
                            activePipe.setSpeed(0); 
                        }
                        
                        if (score > bestScore) {
                            bestScore = score;
                        }
                    }
                }

                if (started && !gameOver) {
                    flappyFace.update();
                }

                repaint();
            });

            timer.setRepeats(true);

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
                        soundPlayer.playClip("swoosh.wav");
                    } else if (gameOver) {
                        // Check for double click to restart
                        if (e.getClickCount() == 2) {
                            restartGame(); // Call the new restart method
                        }
                    } else {
                        // Standard jump while playing
                        flappyFace.jump();
                        soundPlayer.playClip("swoosh.wav");
                    }
                }
            });

        }
//eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (bgImage != null) {
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(Color.CYAN);
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            for (Pipe p : pipes) {
                p.draw(g, getHeight());
            }

            flappyFace.draw(g);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 60));
            String scoreStr = String.valueOf(score);
            int strW_score = g.getFontMetrics().stringWidth(scoreStr);
            g.drawString(scoreStr, (getWidth() - strW_score) / 2, 70);

            g.setFont(new Font("Arial", Font.BOLD, 24));
            String bestStr = "BEST SCORE: " + bestScore;
            int bestStrW = g.getFontMetrics().stringWidth(bestStr);


            // Position: Near the right edge (getWidth() - margin) and high up (e.g., 40)

            int margin = 20;
            g.drawString(bestStr, getWidth() - bestStrW - margin, 40);

            if (!started && !gameOver) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                String s = "TAP TO START";
                int strW = g.getFontMetrics().stringWidth(s);

                g.drawString(s, (getWidth() - strW) / 2, getHeight() / 2 - 200);
            }

            if (gameOver) {

                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());

                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 72));
                String go = "GAME OVER";
                int goW = g.getFontMetrics().stringWidth(go);
                g.drawString(go, (getWidth() - goW) / 2, getHeight() / 2 - 150);

                g.setColor(new Color(255, 255, 255, 200));
                int boxW = 400;
                int boxH = 150;
                int boxX = (getWidth() - boxW) / 2;
                int boxY = (getHeight() - boxH) / 2;
                g.fillRect(boxX, boxY, boxW, boxH);

                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 30));

                String currentStr = "Score: " + score;
                int currentW = g.getFontMetrics().stringWidth(currentStr);
                g.drawString(currentStr, (getWidth() - currentW) / 2, boxY + 50);

                bestStr = "Best: " + bestScore;
                int bestW = g.getFontMetrics().stringWidth(bestStr);
                g.drawString(bestStr, (getWidth() - bestW) / 2, boxY + 110);

                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.ITALIC, 20));
                String r = "Tap twice to restart";
                int rW = g.getFontMetrics().stringWidth(r);
                g.drawString(r, (getWidth() - rW) / 2, boxY + boxH + 50);

            }

            Toolkit.getDefaultToolkit().sync();
        }

        private void restartGame() {

            started = false;
            gameOver = false;
            score = 0;

            pipes.clear();
            int firstX = WINDOW_WIDTH / 2;
            for (int i = 0; i < PIPE_COUNT; i++) {
                int x = firstX + (i * PIPE_SPACING);
                int margin = 60;
                int minGapY = margin;
                int maxGapY = WINDOW_HEIGHT - PIPE_GAP - margin;
                int gapY = minGapY + (int) (Math.random() * Math.max(1, maxGapY - minGapY));

                Pipe newPipe = new Pipe(x, PIPE_WIDTH, gapY, PIPE_GAP, 0, pipeImage, flippedPipeImage);
                newPipe.setPassed(false);
                pipes.add(newPipe);
            }

            int faceStartX = WINDOW_WIDTH / 4;
            int faceStartY = (WINDOW_HEIGHT - FACE_HEIGHT) / 2;
            flappyFace.setPosition(faceStartX, faceStartY);

            timer.stop();
            repaint();

        }

    }

    public void fadeIn() {

        setOpacity(0f);

        Timer timer = new Timer(20, null);
        final float[] opacity = {0f};

        timer.addActionListener(e -> {
            opacity[0] += 0.05f;
            if (opacity[0] >= 1f) {
                opacity[0] = 1f;
                setOpacity(1f);
                timer.stop();
            } else {
                setOpacity(opacity[0]);
            }
        });
        timer.start();
    }

    /**
     * Creates new form Gameplay
     */
    public Gameplay(String characterImagePath) {

        setUndecorated(true);

        GamePanel gamePanel = new GamePanel(characterImagePath);
        setContentPane(gamePanel);

        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        pack();
        setLocationRelativeTo(null);

        setVisible(true);

        fadeIn();
    }

    public Gameplay() {
        initComponents();
        try {
            setUndecorated(true);

        } catch (Exception e) {
            logger.warning("Opacity settings might not be fully supported on this OS/Java version.");
        }

        GamePanel gamePanel = new GamePanel("/images/Face.png");
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
